package com.yetistep.delivr.service.impl;

import com.yetistep.delivr.dao.inf.MerchantDaoService;
import com.yetistep.delivr.dao.inf.UserDaoService;
import com.yetistep.delivr.model.*;
import com.yetistep.delivr.service.inf.MerchantService;
import com.yetistep.delivr.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Surendra
 * Date: 11/21/14
 * Time: 12:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class MerchantServiceImpl implements MerchantService {
    private static final Logger log = Logger.getLogger(MerchantServiceImpl.class);
    @Autowired
    MerchantDaoService merchantDaoService;

    @Autowired
    UserDaoService userDaoService;

    @Override
    public void saveMerchant(MerchantEntity merchant, String username, String password) throws Exception {
        log.info("++++++++++++++++++ Creating Merchant +++++++++++++++++");
        UserEntity user = merchant.getUser();
        if((username==null || password==null) || (username.isEmpty() || password.isEmpty()))
            throw new YSException("VLD009");

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(password);

        user.setUsername(username);
        user.setPassword(hashedPassword);
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
        }

        /* Update S3 Location to the Database */
        merchantDaoService.updateMerchantImageLinks(merchant);

        log.info("+++++++ Merchant Created Successfully ++++++");

    }

    private String saveCouponImages(String encodedString, String imageName, String dir, boolean doCompress) throws Exception {
        if (encodedString == null || encodedString.isEmpty())
            throw new YSException("VLD008");

        String imgType = encodedString.split(";")[0].split("/")[1];
        String tmpDir = System.getProperty("catalina.home") + File.separator + "temp";
        File tmpImg = new File(tmpDir, imageName + "." + imgType);

        //decode the string and save the image
        ImageConverter.decodeToImage(encodedString, tmpImg);
        log.info("Image Saved locally in: " + tmpImg.getPath());

        String imgUrl = null;

        if (doCompress) {
            String compressImgName = imageName;
            ImageConverter.compressImage(tmpImg, imgType, compressImgName, 0.7f);

            compressImgName += ".jpg";
            File compressedFile = new File(tmpDir + File.separator + compressImgName);
            log.info("Compressed and saved in " + compressedFile.getPath());

            //upload compressed image to the bucket
            log.info("Uploading coupon compress image to bucket");
            String compressUrl = AmazonUtil.uploadFileToS3(compressedFile, dir, compressedFile.getName(), true);
            imgUrl = AmazonUtil.cacheImage(compressUrl);
            return imgUrl;
        } else {
            //upload the original image to bucket
            String s3Url = AmazonUtil.uploadFileToS3(tmpImg, dir, tmpImg.getName(), true);
            return imgUrl == null ? AmazonUtil.cacheImage(s3Url) : imgUrl;
        }

    }
}
