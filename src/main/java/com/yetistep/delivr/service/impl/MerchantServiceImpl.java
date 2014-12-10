package com.yetistep.delivr.service.impl;

import com.yetistep.delivr.abs.AbstractManager;
import com.yetistep.delivr.dao.inf.MerchantDaoService;
import com.yetistep.delivr.dao.inf.UserDaoService;
import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.model.*;
import com.yetistep.delivr.service.inf.MerchantService;
import com.yetistep.delivr.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Surendra
 * Date: 11/21/14
 * Time: 12:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class MerchantServiceImpl extends AbstractManager implements MerchantService {
    private static final Logger log = Logger.getLogger(MerchantServiceImpl.class);
    @Autowired
    MerchantDaoService merchantDaoService;

    @Autowired
    UserDaoService userDaoService;

    @Autowired
    HttpServletRequest httpServletRequest;

    @Override
    public void saveMerchant(MerchantEntity merchant, HttpHeaders headers) throws Exception {
        log.info("++++++++++++++++++ Creating Merchant +++++++++++++++++");
        UserEntity user = merchant.getUser();
        HeaderDto headerDto = new HeaderDto();
        GeneralUtil.fillHeaderCredential(headers, headerDto);

        String code = MessageBundle.generateTokenString() + "_" + System.currentTimeMillis();

        user.setUsername(headerDto.getUsername());
        user.setPassword("");
        user.setVerificationCode(code);
        merchant.setUser(user);

        RoleEntity userRole = userDaoService.getRoleByRole(merchant.getUser().getRole().getRole());
        merchant.getUser().setRole(userRole);

        String base64encoded = merchant.getBusinessLogo();
        merchant.setBusinessLogo("");

        merchantDaoService.save(merchant);

        /* Uploading Business Logo To S3 Bucket */

        if(base64encoded!=null && !base64encoded.isEmpty()) {
            log.info("Uploading Business Logo to S3 Bucket ");

            String dir = MessageBundle.separateString("/", "Merchant_" + merchant.getId(), "B_Logo");
            boolean isLocal =  MessageBundle.isLocalHost();
            String logoName = "logo" + (isLocal ? "_tmp_" : "_") + merchant.getId();
            String s3Path = GeneralUtil.saveImageToBucket(base64encoded, logoName, dir, true);
            merchant.setBusinessLogo(s3Path);

             /* Update S3 Location to the Database */
            merchantDaoService.update(merchant);
        }

        //Sending Email For Merchant
        String hostName = getServerUrl();
        String url = hostName + "/assistance/create_password/" + code;
        String loginUrl = hostName + "/";
        log.info("Sending mail to " + user.getUsername() + " with new registration: " + url);

        //send email
        String body = EmailMsg.createPasswordForNewUser(url, user.getFullName(), user.getUsername(), " You have been added as Merchant");
        String subject = "Delivr: You have been added as Merchant ";
        sendMail(user.getUsername(), body, subject);

        log.info("+++++++ Merchant Created Successfully ++++++");

    }

    @Override
    public void activateMerchant(MerchantEntity merchantEntity) throws Exception {
        log.info("++++++++++++++ Activating Merchant " + merchantEntity.getId() + " +++++++++++");

        MerchantEntity dbMerchant = merchantDaoService.find(merchantEntity.getId());
        if(dbMerchant == null)
               throw new YSException("VLD011");

        if(merchantEntity.getCommissionPercentage() == null || BigDecimalUtil.isZero(merchantEntity.getCommissionPercentage()))
              throw new YSException("MRC001");

        if(merchantEntity.getType()==null || merchantEntity.getType().toString().isEmpty())
              throw new YSException("MRC002");

        dbMerchant.getUser().setVerifiedStatus(true);
        dbMerchant.setCommissionPercentage(merchantEntity.getCommissionPercentage());
        dbMerchant.setType(merchantEntity.getType());
        merchantDaoService.update(dbMerchant);


    }

    @Override
    public List<MerchantEntity> getMerchants() throws Exception {
        log.info("++++++++++++ Getting All Merchants +++++++++++++++");
        List<MerchantEntity> merchantEntities = new ArrayList<>();

        merchantEntities = merchantDaoService.findAll();
        return merchantEntities;

    }


    @Override
    public void saveStore(List<StoreEntity> stores) throws Exception {
        log.info("++++++++++++ Saving Store +++++++++++++++");
        for (StoreEntity store: stores){
            StoresBrandsEntity storesBrand = merchantDaoService.getBrandByBrandName(store.getStoresBrand().getBrandName());
            if(storesBrand != null)
                store.setStoresBrand(storesBrand);
        }
        merchantDaoService.saveStore(stores);

    }
}
