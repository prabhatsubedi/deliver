package com.yetistep.delivr.service.impl;

import com.yetistep.delivr.abs.AbstractManager;
import com.yetistep.delivr.dao.inf.MerchantDaoService;
import com.yetistep.delivr.dao.inf.UserDaoService;
import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.dto.RequestJsonDto;
import com.yetistep.delivr.enums.Role;
import com.yetistep.delivr.enums.UserStatus;
import com.yetistep.delivr.model.*;
import com.yetistep.delivr.service.inf.MerchantService;
import com.yetistep.delivr.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

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
        /*By default set to null during sign up*/
        merchant.setCommissionPercentage(null);
        RoleEntity userRole = userDaoService.getRoleByRole(Role.ROLE_MERCHANT);
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

        if(merchantEntity.getPartnershipStatus()==null)
              throw new YSException("MRC002");

        UserEntity user = dbMerchant.getUser();
        user.setVerifiedStatus(true);
        dbMerchant.setCommissionPercentage(merchantEntity.getCommissionPercentage());
        dbMerchant.setPartnershipStatus(merchantEntity.getPartnershipStatus());
        merchantDaoService.update(dbMerchant);

        //Sending Email For Merchant
        String url = getServerUrl();
        String body = EmailMsg.activateMerchant(url, user.getFullName(), " Your account has been activated");
        String subject = "Delivr: Your account has been activated ";
        sendMail(user.getUsername(), body, subject);
    }

    @Override
    public List<MerchantEntity> getMerchants() throws Exception {
        log.info("++++++++++++ Getting All Merchants +++++++++++++++");
        List<MerchantEntity> merchantEntities = new ArrayList<>();

        merchantEntities = merchantDaoService.findAll();
        for(MerchantEntity merchantEntity: merchantEntities){
            /*
            * If password is empty, then our assumption is merchant has not clicked the verification link
            * and hence status is UNVERIFIED.
            * If password is not empty and commission percentage is null, our assumption is merchant has clicked
            * on the verification link but not activated by admin/manager. Hence status is VERIFIED only.
            * If password is not empty and commission percentage is not null, then verification status checked and
            * user status is updated based on verified status(true ==> ACTIVE, false ==> INACTIVE).
            */
            if(merchantEntity.getUser().getPassword().isEmpty()){
                merchantEntity.setUserStatus(UserStatus.UNVERIFIED);
            }else if(merchantEntity.getCommissionPercentage() == null){
                merchantEntity.setUserStatus(UserStatus.VERIFIED);
            }else{
                if(merchantEntity.getUser().getVerifiedStatus()){
                    merchantEntity.setUserStatus(UserStatus.ACTIVE);
                }else{
                    merchantEntity.setUserStatus(UserStatus.INACTIVE);
                }
            }
            merchantEntity.getUser().setRole(null);
        }
        return merchantEntities;

    }


    @Override
    public void saveStore(RequestJsonDto postData, HttpHeaders headers ) throws Exception {
        log.info("++++++++++++ Saving Store "+postData.getStores().size()+" +++++++++++++++");

        HeaderDto headerDto = new HeaderDto();
        GeneralUtil.fillHeaderCredential(headers, headerDto);

        List<StoreEntity> stores = postData.getStores();
        String brandName = postData.getBrandName();
        List<Integer> categories = postData.getCategories();

        MerchantEntity dbMerchant = merchantDaoService.find(Integer.parseInt(headerDto.getId()));
        if(dbMerchant == null)
            throw new YSException("VLD011");


        for (StoreEntity store: stores){

            StoresBrandEntity storesBrand = merchantDaoService.getBrandByBrandName(brandName);
            //if brand with current name not exists add brand
            if(storesBrand == null) {
                StoresBrandEntity newStoresBrand = new StoresBrandEntity();
                newStoresBrand.setBrandName(brandName);
                store.setStoresBrand(newStoresBrand);
            }else{
                store.setStoresBrand(storesBrand);
            }

            List<BrandsCategoryEntity> brandsCategories = new ArrayList<BrandsCategoryEntity>();

            for (Integer categoryId: categories){
                BrandsCategoryEntity brandsCategory = merchantDaoService.getBrandsCategoryByBrandAndCategory(store.getStoresBrand().getId(), categoryId);

                //if brands category with current category and and brand add brandCategory
                if(brandsCategory == null){
                    BrandsCategoryEntity newBrandsCategory = new BrandsCategoryEntity();
                    CategoryEntity category = merchantDaoService.getCategoryById(categoryId);
                    newBrandsCategory.setCategory(category);
                    newBrandsCategory.setStoresBrand(store.getStoresBrand());
                    brandsCategories.add(newBrandsCategory);
                }else{
                    brandsCategory.setStoresBrand(store.getStoresBrand());
                    brandsCategories.add(brandsCategory);
                }
            }

            store.getStoresBrand().setBrandsCategory(brandsCategories);
            store.getStoresBrand().setMerchant(dbMerchant);
            merchantDaoService.saveStore(store);

        }


    }

    @Override
    public MerchantEntity getMerchantById(Integer id) throws Exception {
        MerchantEntity merchantEntity = merchantDaoService.find(id);
        if(merchantEntity == null)
            throw new YSException("VLD011");
        merchantEntity.getUser().setRole(null);
        return merchantEntity;
    }

    @Override
    public Boolean updateMerchant(MerchantEntity merchantEntity) throws Exception {
        log.info("Updating merchant with ID:"+merchantEntity.getId());
        MerchantEntity merchant = merchantDaoService.find(merchantEntity.getId());
        if(merchant == null)
            throw new YSException("VLD011");
        merchant.setWebsite(merchantEntity.getWebsite());
        merchant.getUser().setFullName(merchantEntity.getUser().getFullName());
        merchant.getUser().setMobileNumber(merchantEntity.getUser().getMobileNumber());
        return merchantDaoService.update(merchant);
    }

    @Override
    public List<CategoryEntity> getParentCategories() throws Exception {
        List<CategoryEntity> categories = merchantDaoService.findParentCategories();
        return categories;
    }
}
