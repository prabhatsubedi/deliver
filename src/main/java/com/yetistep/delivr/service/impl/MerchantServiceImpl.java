package com.yetistep.delivr.service.impl;

import com.yetistep.delivr.abs.AbstractManager;
import com.yetistep.delivr.dao.inf.*;
import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.dto.PaginationDto;
import com.yetistep.delivr.dto.RequestJsonDto;
import com.yetistep.delivr.enums.Role;
import com.yetistep.delivr.enums.Status;
import com.yetistep.delivr.model.*;
import com.yetistep.delivr.model.mobile.dto.ItemDto;
import com.yetistep.delivr.service.inf.MerchantService;
import com.yetistep.delivr.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

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
    StoreDaoService storeDaoService;

    @Autowired
    InvoiceDaoService invoiceDaoService;

    @Autowired
    HttpServletRequest httpServletRequest;

    @Autowired
    CategoryDaoService categoryDaoService;

    @Autowired
    ItemDaoService itemDaoService;

    @Override
    public void saveMerchant(MerchantEntity merchant, HeaderDto headerDto) throws Exception {
        log.info("++++++++++++++++++ Creating Merchant +++++++++++++++++");
        UserEntity user = merchant.getUser();
        if(!user.getEmailAddress().equals(headerDto.getUsername()))
            throw new YSException("VLD030");

        /* Check Merchant Email Address */
        if(merchantDaoService.checkEmailExistence(headerDto.getUsername()))
            throw new YSException("VLD026");

        List<AddressEntity> addressEntities = user.getAddresses();
        for (AddressEntity address: addressEntities){
            address.setUser(user);
        }



        String code = MessageBundle.generateTokenString() + "_" + System.currentTimeMillis();
        user.setStatus(Status.UNVERIFIED);
        user.setUsername(headerDto.getUsername());
        user.setPassword("");
        user.setVerificationCode(code);
        user.setMobileVerificationStatus(false);
        user.setBlacklistStatus(false);
        user.setVerifiedStatus(false);
        user.setSubscribeNewsletter(false);
        user.setCreatedDate(DateUtil.getCurrentTimestampSQL());
        user.setLastActivityDate(null);
        merchant.setPartnershipStatus(false);
        merchant.setUser(user);
        /*By default set to null during sign up*/
        merchant.setCommissionPercentage(null);
        RoleEntity userRole = userDaoService.getRoleByRole(Role.ROLE_MERCHANT);
        merchant.getUser().setRole(userRole);

        String base64encoded = merchant.getBusinessLogo();
        merchant.setBusinessLogo("");

        merchantDaoService.save(merchant);
        /*Uploading Business Logo To S3 Bucket*/

        if(base64encoded!=null && !base64encoded.isEmpty()) {
            log.info("Uploading Business Logo to S3 Bucket ");

            String dir = MessageBundle.separateString("/", "Merchant_" + merchant.getId(), "B_Logo");
            boolean isLocal =  MessageBundle.isLocalHost();
            String logoName = "logo" + (isLocal ? "_tmp_" : "_") + merchant.getId();
            String s3Path = GeneralUtil.saveImageToBucket(base64encoded, logoName, dir, true);
            merchant.setBusinessLogo(s3Path);

             /* Update S3 Location to the Database */
            //merchant.getUser().setLastActivityDate(DateUtil.getCurrentTimestampSQL());
            merchantDaoService.update(merchant);
        }

        //Sending Email For Merchant
        String hostName = getServerUrl();
        String url = hostName + "/assistance/create_password/" + code;
        String loginUrl = hostName + "/";
        log.info("Sending mail to " + user.getUsername() + " with new registration: " + url);

        //send email
        String body = EmailMsg.createPasswordForNewUser(url, user.getFullName(), user.getUsername(), getServerUrl());
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
        //user.setVerifiedStatus(true);
        user.setStatus(Status.ACTIVE);
        dbMerchant.setCommissionPercentage(merchantEntity.getCommissionPercentage());
        dbMerchant.setPartnershipStatus(merchantEntity.getPartnershipStatus());
        dbMerchant.setServiceFee(merchantEntity.getServiceFee());
        merchantDaoService.update(dbMerchant);

        //Sending Email For Merchant
        String url = getServerUrl();
        String body = EmailMsg.activateMerchant(url, user.getFullName(), getServerUrl());
        String subject = "Delivr: Your account has been activated ";
        sendMail(user.getUsername(), body, subject);
    }

    @Override
    public PaginationDto getMerchants(RequestJsonDto requestJsonDto) throws Exception {
        log.info("++++++++++++ Getting All Merchants +++++++++++++++");
        List<MerchantEntity> merchantEntities = new ArrayList<>();
        Page page = requestJsonDto.getPage();

        if(page != null && page.getSearchFor() != null){
            Map<String, String> fieldsMap = new HashMap<>();
            fieldsMap.put("self", "businessTitle");
            fieldsMap.put("user", "fullName,emailAddress,mobileNumber");
            page.setSearchFields(fieldsMap);
        }

        PaginationDto paginationDto = new PaginationDto();
        Integer totalRows =  merchantDaoService.getTotalNumberOfMerchants();
        paginationDto.setNumberOfRows(totalRows);

        if(page != null){
            page.setTotalRows(totalRows);
        }

        merchantEntities = merchantDaoService.findAll(page);
        /*for(MerchantEntity merchantEntity: merchantEntities){
            *//*
            * If password is empty, then our assumption is merchant has not clicked the verification link
            * and hence status is UNVERIFIED.
            * If password is not empty and commission percentage is null, our assumption is merchant has clicked
            * on the verification link but not activated by admin/manager. Hence status is VERIFIED only.
            * If password is not empty and commission percentage is not null, then verification status checked and
            * user status is updated based on verified status(true ==> ACTIVE, false ==> INACTIVE).
            *//*
            if(merchantEntity.getUser().getPassword().isEmpty()){
                if(merchantEntity.getUser().getVerifiedStatus() != null && merchantEntity.getCommissionPercentage()!=null){
                    if (merchantEntity.getUser().getVerifiedStatus()) {
                        merchantEntity.setStatus(Status.ACTIVE);
                    }else{
                        merchantEntity.setStatus(Status.INACTIVE);
                    }
                }else {
                    merchantEntity.setStatus(Status.UNVERIFIED);
                }
            }else if(merchantEntity.getCommissionPercentage() == null){
                merchantEntity.setStatus(Status.VERIFIED);
            }else{
                if(merchantEntity.getUser().getVerifiedStatus()){
                    merchantEntity.setStatus(Status.ACTIVE);
                }else{
                    merchantEntity.setStatus(Status.INACTIVE);
                }
            }
            merchantEntity.getUser().setRole(null);
        }*/
        List<MerchantEntity> objects = new ArrayList<>();

        String fields = "id,businessTitle,partnershipStatus,user";

        Map<String, String> assoc = new HashMap<>();

        assoc.put("user", "id,fullName,emailAddress,mobileNumber,verifiedStatus,status");

        for (MerchantEntity merchant:merchantEntities){
            objects.add((MerchantEntity) ReturnJsonUtil.getJsonObject(merchant, fields, assoc));
        }

        paginationDto.setData(objects);
        return paginationDto;
    }

    @Override
    public List<MerchantEntity> getAllMerchants() throws Exception {
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
           /* if(merchantEntity.getUser().getPassword().isEmpty()){
                if(merchantEntity.getUser().getVerifiedStatus() != null && merchantEntity.getCommissionPercentage()!=null){
                    if (merchantEntity.getUser().getVerifiedStatus()) {
                        merchantEntity.setStatus(Status.ACTIVE);
                    }else{
                        merchantEntity.setStatus(Status.INACTIVE);
                    }
                }else {
                    merchantEntity.setStatus(Status.UNVERIFIED);
                }
            }else if(merchantEntity.getCommissionPercentage() == null){
                merchantEntity.setStatus(Status.VERIFIED);
            }else{
                if(merchantEntity.getUser().getVerifiedStatus()){
                    merchantEntity.setStatus(Status.ACTIVE);
                }else{
                    merchantEntity.setStatus(Status.INACTIVE);
                }
            }
            merchantEntity.getUser().setRole(null);*/

            merchantEntity.setStatus(merchantEntity.getUser().getStatus());
        }

        List<MerchantEntity> objects = new ArrayList<>();
        String fields = "id,businessTitle,partnershipStatus,status";

        for (MerchantEntity merchant:merchantEntities){
            objects.add((MerchantEntity) ReturnJsonUtil.getJsonObject(merchant, fields));
        }

        return objects;
    }


    private void checkUniqueBrand(String name) throws Exception{
        log.info("++++++++++++ checking unique brands +++++++++++++++");
        StoresBrandEntity brand = merchantDaoService.getBrandByBrandName(name);
        if (brand != null)
            throw new YSException("VLD023");
    }


    @Override
    public void saveStore(RequestJsonDto requestJson, HeaderDto headerDto ) throws Exception {
        log.info("++++++++++++ Saving Store " + requestJson.getStores().size() + " +++++++++++++++");

        List<StoreEntity> stores = requestJson.getStores();
        StoresBrandEntity storesBrand = requestJson.getStoresBrand();
        List<Integer> categories = requestJson.getCategories();

        MerchantEntity dbMerchant = merchantDaoService.find(headerDto.getMerchantId());
        if((dbMerchant == null) || (dbMerchant.getUser().getStatus() != Status.ACTIVE))
            throw new YSException("VLD011");

        checkUniqueBrand( storesBrand.getBrandName().trim());

        String brandLogo = storesBrand.getBrandLogo();
        String brandImage = storesBrand.getBrandImage();

        List<BrandsCategoryEntity> brandsCategories = new ArrayList<BrandsCategoryEntity>();
        for (Integer categoryId: categories){
            BrandsCategoryEntity newBrandsCategory = new BrandsCategoryEntity();
            CategoryEntity category = new CategoryEntity();
            category.setId(categoryId);
            newBrandsCategory.setCategory(category);
            newBrandsCategory.setStoresBrand(storesBrand);
            brandsCategories.add(newBrandsCategory);
        }

        for (StoreEntity store: stores) {
            store.setStoresBrand(storesBrand);
            store.setCreatedDate(DateUtil.getCurrentTimestampSQL());
        }


        storesBrand.setBrandsCategory(brandsCategories);
        storesBrand.setMerchant(dbMerchant);
        storesBrand.setCreatedDate(DateUtil.getCurrentTimestampSQL());
        storesBrand.setBrandLogo(null);
        storesBrand.setBrandImage(null);

        /*for (StoreEntity store: stores){
            store.setStoresBrand(storesBrand);
            List<BrandsCategoryEntity> brandsCategories = new ArrayList<BrandsCategoryEntity>();
            for (Integer categoryId: categories){
                BrandsCategoryEntity newBrandsCategory = new BrandsCategoryEntity();
                CategoryEntity category = new CategoryEntity();
                category.setId(categoryId);
                newBrandsCategory.setCategory(category);
                newBrandsCategory.setStoresBrand(store.getStoresBrand());
                brandsCategories.add(newBrandsCategory);
            }
            store.getStoresBrand().setBrandsCategory(brandsCategories);
            store.getStoresBrand().setMerchant(dbMerchant);
            store.getStoresBrand().setBrandLogo(null);
            store.getStoresBrand().setBrandImage(null);
        }*/
        storesBrand.setStore(stores);

        merchantDaoService.saveStoresBrand(storesBrand);

        StoresBrandEntity brand = storesBrand;

        if (brandLogo != null && !brandLogo.isEmpty() && brandImage != null && !brandImage.isEmpty()) {
            log.info("Uploading brand logo and image to S3 Bucket ");
            String dir = MessageBundle.separateString("/", "Merchant_"+dbMerchant.getId(), "Brand_"+ brand.getId());
            boolean isLocal = MessageBundle.isLocalHost();
            String brandLogoName = "brand_logo" + (isLocal ? "_tmp_" : "_") + brand.getId()+System.currentTimeMillis();
            String brandImageName = "brand_image" + (isLocal ? "_tmp_" : "_") + brand.getId()+System.currentTimeMillis();
            String s3PathLogo = GeneralUtil.saveImageToBucket(brandLogo, brandLogoName, dir, true);
            String s3PathImage = GeneralUtil.saveImageToBucket(brandImage, brandImageName, dir, true);
            brand.setBrandLogo(s3PathLogo);
            brand.setBrandImage(s3PathImage);
            merchantDaoService.updateStoresBrand(brand);
        }
    }

    @Override
    public void updateStore(RequestJsonDto requestJson) throws Exception {
        log.info("++++++++++++ updating store +++++++++++++++");
        List<StoreEntity> stores = requestJson.getStores();
        StoresBrandEntity storesBrand = requestJson.getStoresBrand();
        List<Integer> categories = requestJson.getCategories();

        StoresBrandEntity dbStoresBrand = merchantDaoService.findBrandDetail(storesBrand.getId());

        if(dbStoresBrand == null)
            throw new YSException("VLD011");

        String brandLogo = storesBrand.getBrandLogo();
        String brandImage = storesBrand.getBrandImage();

        String brandLogoUrl = dbStoresBrand.getBrandLogo();
        String brandImageUrl = dbStoresBrand.getBrandImage();

        dbStoresBrand.setBrandUrl(storesBrand.getBrandUrl());
        dbStoresBrand.setMinOrderAmount(storesBrand.getMinOrderAmount());
        dbStoresBrand.setOpeningTime(storesBrand.getOpeningTime());
        dbStoresBrand.setClosingTime(storesBrand.getClosingTime());
        dbStoresBrand.setOpenStatus(storesBrand.getOpenStatus());

        List<BrandsCategoryEntity> dbBrandsCategories = dbStoresBrand.getBrandsCategory();
        Map<Integer, Integer> brandCategoriesIdList = new HashMap<Integer, Integer>();
        for (BrandsCategoryEntity brandsCategory: dbBrandsCategories){
            brandCategoriesIdList.put(brandsCategory.getCategory().getId(), brandsCategory.getId());
        }

        for (Integer categoryId: categories){
            if(brandCategoriesIdList.get(categoryId) == null) {
                BrandsCategoryEntity newBrandsCategory = new BrandsCategoryEntity();
                CategoryEntity category = new CategoryEntity();
                category.setId(categoryId);
                newBrandsCategory.setCategory(category);
                newBrandsCategory.setStoresBrand(dbStoresBrand);
                dbBrandsCategories.add(newBrandsCategory);
            }
        }

        dbStoresBrand.setBrandsCategory(dbBrandsCategories);

        Map<Integer, StoreEntity> storeWithId = new HashMap<Integer, StoreEntity>();
        Map<Integer, StoreEntity> dbStoreWithId = new HashMap<Integer, StoreEntity>();

        List<StoreEntity> dbStores = dbStoresBrand.getStore();
        List<Integer> storeIdList = new ArrayList<Integer>();

        for (StoreEntity dbStore:dbStores){
            //if(dbStore.getId() != null)
            dbStoreWithId.put(dbStore.getId(), dbStore);
        }

        for (StoreEntity store: stores){
            if(store.getId() == null){
                store.setStoresBrand(dbStoresBrand);
                dbStores.add(store);
            } else {
                storeIdList.add(store.getId());
                storeWithId.put(store.getId(), store);
            }
            //store.setStoresBrand(dbStoresBrand);
        }

        /*for (StoreEntity dbStore:dbStores){
            if(dbStore.getId() != null)
                dbStoreWithId.put(dbStore.getId(), dbStore);
        }*/

        Iterator itS = dbStoreWithId.entrySet().iterator();
        while (itS.hasNext()) {
            Map.Entry pairs = (Map.Entry)itS.next();
            if(!storeIdList.contains(pairs.getKey())){
                StoreEntity dbStore =  dbStoreWithId.get(pairs.getKey());
                dbStore.setStatus(Status.INACTIVE);
            }else{
                StoreEntity newStore =  storeWithId.get(pairs.getKey());
                StoreEntity dbStore =  dbStoreWithId.get(pairs.getKey());
                dbStore.setName(newStore.getName());
                dbStore.setContactNo(newStore.getContactNo());
                dbStore.setStreet(newStore.getStreet());
                dbStore.setCity(newStore.getCity());
                dbStore.setState(newStore.getState());
                dbStore.setCountry(newStore.getCountry());
                dbStore.setLatitude(newStore.getLatitude());
                dbStore.setLongitude(newStore.getLongitude());
                dbStore.setContactPerson(newStore.getContactPerson());
                dbStore.setEmail(newStore.getEmail());
                dbStore.setSendEmail(newStore.getSendEmail());
            }
        }
        dbStoresBrand.setStore(dbStores);
        merchantDaoService.updateStoresBrand(dbStoresBrand);

        String dir = MessageBundle.separateString("/", "Merchant_"+dbStoresBrand.getMerchant().getId(), "Brand_"+ dbStoresBrand.getId());
        boolean isLocal = MessageBundle.isLocalHost();

        if (brandLogo != null && !brandLogo.isEmpty()) {
            log.info("Deleting brand log to S3 Bucket ");
            AmazonUtil.deleteFileFromBucket(AmazonUtil.getAmazonS3Key(brandLogoUrl));
            log.info("Uploading brand log to S3 Bucket ");
            String brandLogoName = "brand_logo" + (isLocal ? "_tmp_" : "_") + dbStoresBrand.getId()+System.currentTimeMillis();;
            String s3PathLogo = GeneralUtil.saveImageToBucket(brandLogo, brandLogoName, dir, true);
            dbStoresBrand.setBrandLogo(s3PathLogo);
            merchantDaoService.updateStoresBrand(dbStoresBrand);
        }

        if (brandImage != null && !brandImage.isEmpty()) {
            log.info("Deleting brand log to S3 Bucket ");
            AmazonUtil.deleteFileFromBucket(AmazonUtil.getAmazonS3Key(brandImageUrl));
            log.info("Uploading brand image to S3 Bucket ");
            String brandImageName = "brand_image" + (isLocal ? "_tmp_" : "_") + dbStoresBrand.getId()+System.currentTimeMillis();;
            String s3PathImage = GeneralUtil.saveImageToBucket(brandImage, brandImageName, dir, true);
            dbStoresBrand.setBrandImage(s3PathImage);
            merchantDaoService.updateStoresBrand(dbStoresBrand);
        }
    }




    @Override
    public MerchantEntity getMerchantById(HeaderDto headerDto) throws Exception {
        log.info("++++++++++++ getting merchant +++++++++++++++");
        MerchantEntity merchantEntity = merchantDaoService.find(headerDto.getMerchantId());
        if(merchantEntity == null)
            throw new YSException("VLD011");
         /*
            * If password is empty, then our assumption is merchant has not clicked the verification link
            * and hence status is UNVERIFIED.
            * If password is not empty and commission percentage is null, our assumption is merchant has clicked
            * on the verification link but not activated by admin/manager. Hence status is VERIFIED only.
            * If password is not empty and commission percentage is not null, then verification status checked and
            * user status is updated based on verified status(true ==> ACTIVE, false ==> INACTIVE).
            */
        /*if(merchantEntity.getUser().getPassword().isEmpty()){
            if(merchantEntity.getUser().getVerifiedStatus() != null  && merchantEntity.getCommissionPercentage()!=null){
                if (merchantEntity.getUser().getVerifiedStatus()) {
                    merchantEntity.setStatus(Status.ACTIVE);
                }else{
                    merchantEntity.setStatus(Status.INACTIVE);
                }
            }else {
                merchantEntity.setStatus(Status.UNVERIFIED);
            }
        }else if(merchantEntity.getCommissionPercentage() == null){
            merchantEntity.setStatus(Status.VERIFIED);
        }else{
            if(merchantEntity.getUser().getVerifiedStatus()){
                merchantEntity.setStatus(Status.ACTIVE);
            }else{
                merchantEntity.setStatus(Status.INACTIVE);
            }
        }*/


        String fields = "id,partnershipStatus,commissionPercentage,serviceFee,website,agreementDetail,businessTitle,businessLogo,companyRegistrationNo,vatNo,panNo,status,user";

        Map<String, String> assoc = new HashMap<>();
        Map<String, String> subAssoc = new HashMap<>();

        assoc.put("user", "id,fullName,mobileNumber,emailAddress,profileImage,addresses,verifiedStatus,status");
        subAssoc.put("addresses", "id,street,city,state,country,latitude,longitude");

        return ((MerchantEntity) ReturnJsonUtil.getJsonObject(merchantEntity, fields, assoc, subAssoc));

        //merchantEntity.getUser().setRole(null);
        //return merchantEntity;
    }

    @Override
    public Boolean updateMerchant(MerchantEntity merchantEntity, HeaderDto headerDto) throws Exception {
        log.info("Updating merchant with ID:"+headerDto.getId());

        MerchantEntity merchant = merchantDaoService.find(headerDto.getMerchantId());
        if(merchant == null)
            throw new YSException("VLD011");
        //set user values
//        merchant.getUser().setUsername(headerDto.getUsername());
//        if(headerDto.getPassword() != null)
//            merchant.getUser().setPassword(GeneralUtil.encryptPassword(headerDto.getPassword()));


        merchant.getUser().setFullName(merchantEntity.getUser().getFullName());
        merchant.getUser().setMobileNumber(merchantEntity.getUser().getMobileNumber());
        merchant.getUser().setStatus(merchantEntity.getUser().getStatus());

        //merchant.getUser().setEmailAddress(merchantEntity.getUser().getEmailAddress());
        //merchant.getUser().setGender(merchantEntity.getUser().getGender());
        //set address value
        merchant.getUser().getAddresses().get(0).setCity(merchantEntity.getUser().getAddresses().get(0).getCity());
        merchant.getUser().getAddresses().get(0).setStreet(merchantEntity.getUser().getAddresses().get(0).getStreet());
        merchant.getUser().getAddresses().get(0).setState(merchantEntity.getUser().getAddresses().get(0).getState());
        merchant.getUser().getAddresses().get(0).setCountry(merchantEntity.getUser().getAddresses().get(0).getCountry());
        merchant.getUser().getAddresses().get(0).setCountryCode(merchantEntity.getUser().getAddresses().get(0).getCountryCode());
        merchant.getUser().getAddresses().get(0).setLatitude(merchantEntity.getUser().getAddresses().get(0).getLatitude());
        merchant.getUser().getAddresses().get(0).setLongitude(merchantEntity.getUser().getAddresses().get(0).getLongitude());
        merchant.getUser().getAddresses().get(0).setUser(merchant.getUser());
        //merchant values
        merchant.setWebsite(merchantEntity.getWebsite());
        merchant.setPanNo(merchantEntity.getPanNo());
        //merchant.setBusinessTitle(merchantEntity.getBusinessTitle());
        merchant.setVatNo(merchantEntity.getVatNo());
        merchant.setCompanyRegistrationNo(merchantEntity.getCompanyRegistrationNo());
        if(merchantEntity.getCommissionPercentage() != null){
            merchant.setCommissionPercentage(merchantEntity.getCommissionPercentage());
        }
        if(merchantEntity.getServiceFee() != null){
            merchant.setServiceFee(merchantEntity.getServiceFee());
        }
        if(merchantEntity.getPartnershipStatus() != null){
            merchant.setPartnershipStatus(merchantEntity.getPartnershipStatus());
        }


        String businessLogo =  merchantEntity.getBusinessLogo();


        /*if (profileImage != null && !profileImage.isEmpty()) {

            log.info("Deleting Profile Image of merchant to S3 Bucket ");
            AmazonUtil.deleteFileFromBucket(AmazonUtil.getAmazonS3Key(merchant.getUser().getProfileImage()));

            log.info("Uploading Profile Image of merchant to S3 Bucket ");

            String dir = MessageBundle.separateString("/", "merchant", "merchant" + merchant.getId());
            boolean isLocal = MessageBundle.isLocalHost();
            String imageName = "pimg" + (isLocal ? "_tmp_" : "_") + merchant.getId();
            String s3Path = GeneralUtil.saveImageToBucket(profileImage, imageName, dir, true);
            merchant.getUser().setProfileImage(s3Path);
        }*/

        //merchant.getUser().setLastActivityDate(DateUtil.getCurrentTimestampSQL());
        merchant.getUser().setMerchant(merchant);
        merchantDaoService.update(merchant);


        if(businessLogo!=null && !businessLogo.isEmpty()) {
            log.info("Deleting Business Logo from S3 Bucket ");
            AmazonUtil.deleteFileFromBucket(AmazonUtil.getAmazonS3Key(merchant.getBusinessLogo()));

            log.info("Uploading Business Logo to S3 Bucket ");
            String dir = MessageBundle.separateString("/", "Merchant_" + merchant.getId(), "B_Logo");
            boolean isLocal =  MessageBundle.isLocalHost();
            String logoName = "logo" + (isLocal ? "_tmp_" : "_") + merchant.getId()+System.currentTimeMillis();
            String s3Path = GeneralUtil.saveImageToBucket(businessLogo, logoName, dir, true);
            merchant.setBusinessLogo(s3Path);

             /* Update S3 Location to the Database */
            //merchant.getUser().setLastActivityDate(DateUtil.getCurrentTimestampSQL());
            merchantDaoService.update(merchant);
        }
        return true;
    }

    @Override
    public List<CategoryEntity> getParentCategories() throws Exception {
        log.info("++++++++++++ getting parent categories +++++++++++++++");
        List<CategoryEntity> categories = merchantDaoService.findParentCategories();
        return categories;
    }


    @Override
    public List<Object> findBrandList(HeaderDto headerDto) throws Exception {
        log.info("++++++++++++ finding brand list +++++++++++++++");
        MerchantEntity dbMerchant = merchantDaoService.find(headerDto.getMerchantId());
        if(dbMerchant == null)
            throw new YSException("VLD011");
        List<StoresBrandEntity> storesBrands = merchantDaoService.findBrandListByMerchant(dbMerchant.getId());
        List<Object> storesBrandEntities = new ArrayList<Object>();

        String fields = "id,brandName,brandLogo,brandImage,status,openingTime,closingTime,minOrderAmount,featured,priority,merchant,store";

        Map<String, String> assoc = new HashMap<>();
        Map<String, String> subAssoc = new HashMap<>();

        assoc.put("merchant", "id,businessTitle");
        assoc.put("store", "id,name,street,city,state,country,latitude,longitude");

        if(storesBrands.size()>0){
            for(StoresBrandEntity storesBrand: storesBrands){
               storesBrandEntities.add(ReturnJsonUtil.getJsonObject(storesBrand, fields, assoc, subAssoc));
                //storesBrandEntities.add(getStoreBrandForJson(storesBrand));
            }
        }
        return storesBrandEntities;
    }

    @Override
    public PaginationDto findBrands(HeaderDto headerDto, RequestJsonDto requestJsonDto) throws Exception {
        log.info("++++++++++++ finding brands +++++++++++++++");
        List<StoresBrandEntity> storesBrands;
        PaginationDto paginationDto = new PaginationDto();

        Page page = requestJsonDto.getPage();

        Integer totalRows;
        if (headerDto.getMerchantId() != null){
             totalRows =  merchantDaoService.getTotalNumberOfBrandByMerchant(headerDto.getMerchantId());
        }else{
             totalRows =  merchantDaoService.getTotalNumberOfBrand();
        }
        paginationDto.setNumberOfRows(totalRows);

        if(page != null){
            page.setTotalRows(totalRows);
        }

        if (headerDto.getMerchantId() != null){
            storesBrands = merchantDaoService.findBrandListByMerchant(headerDto.getMerchantId(), page);
        } else {
            storesBrands = merchantDaoService.findBrandList(page);
        }

        List<StoresBrandEntity> brandList = new ArrayList<>();

        String fields = "id,brandName,brandLogo,brandImage,status,openingTime,closingTime,minOrderAmount,featured,priority,store,merchant";

        Map<String, String> assoc = new HashMap<>();
        Map<String, String> subAssoc = new HashMap<>();

        assoc.put("store", "id");
        assoc.put("merchant", "id,businessTitle,user");
        subAssoc.put("user", "fullName");


        if(storesBrands.size()>0){
            for (StoresBrandEntity storesBrandEntity: storesBrands) {
                StoresBrandEntity brand = (StoresBrandEntity) ReturnJsonUtil.getJsonObject(storesBrandEntity, fields, assoc, subAssoc);
                brand.setCountStore(brand.getStore().size());
                brand.setMerchantId(brand.getMerchant().getId());
                brand.setStore(null);
                brandList.add (brand);
            }
        }
        paginationDto.setData(brandList);
        return paginationDto;
    }

    @Override
    public List<StoresBrandEntity> findSearchBrands(HeaderDto headerDto) throws Exception {
        log.info("++++++++++++ find search brands +++++++++++++++");
        List<StoresBrandEntity> storesBrands;
        if (headerDto.getMerchantId() != null){
            storesBrands = merchantDaoService.findBrandListByMerchant(headerDto.getMerchantId());
        } else {
            storesBrands = merchantDaoService.findBrandList();
        }
        List<StoresBrandEntity> brandList = new ArrayList<>();

        String fields = "id,brandName";
        if(storesBrands.size()>0){
            for (StoresBrandEntity storesBrandEntity: storesBrands) {
                StoresBrandEntity brand = (StoresBrandEntity) ReturnJsonUtil.getJsonObject(storesBrandEntity, fields);
                brandList.add(brand);
            }
        }
        return brandList;
    }

    @Override
    public StoresBrandEntity findBrandDetail(HeaderDto headerDto) throws Exception {
        log.info("++++++++++++ getting brand detail +++++++++++++++");
        StoresBrandEntity storesBrandEntity =  merchantDaoService.findBrandDetail(Integer.parseInt(headerDto.getId()));

        String fields = "id,brandName,brandLogo,brandImage,status,openingTime,closingTime,minOrderAmount";

        Map<String, String> assoc = new HashMap<>();
        Map<String, String> subAssoc = new HashMap<>();

        assoc.put("store", "id,street,city,state,country,latitude,longitude,status,contactNo,contactPerson,email,sendEmail");
        assoc.put("brandsCategory", "id,category");

        subAssoc.put("category", "id,name,featured");
        return (StoresBrandEntity) ReturnJsonUtil.getJsonObject(storesBrandEntity, fields, assoc, subAssoc);

        //return getStoreBrandForJson(storesBrandEntity);
    }

    /*private StoresBrandEntity getStoreBrandForJson(StoresBrandEntity storesBrandEntity){
        StoresBrandEntity storesBrand = new StoresBrandEntity();
        storesBrand.setId(storesBrandEntity.getId());
        storesBrand.setBrandName(storesBrandEntity.getBrandName());
        storesBrand.setOpeningTime(storesBrandEntity.getOpeningTime());
        storesBrand.setClosingTime(storesBrandEntity.getClosingTime());
        storesBrand.setBrandLogo(storesBrandEntity.getBrandLogo());
        storesBrand.setBrandImage(storesBrandEntity.getBrandImage());
        storesBrand.setBrandUrl(storesBrandEntity.getBrandUrl());
        storesBrand.setStatus(storesBrandEntity.getStatus());
        storesBrand.setMinOrderAmount(storesBrandEntity.getMinOrderAmount());
        storesBrand.setCreatedDate(storesBrandEntity.getCreatedDate());

        List<BrandsCategoryEntity> brandCategories = new ArrayList<BrandsCategoryEntity>();
        for(BrandsCategoryEntity brandsCategoryEntity : storesBrandEntity.getBrandsCategory()){
            BrandsCategoryEntity brandsCategory = new BrandsCategoryEntity();
            brandsCategory.setId(brandsCategoryEntity.getId());
            brandCategories.add(brandsCategory);
        }
        storesBrand.setBrandsCategory(brandCategories);

        List<StoreEntity> stores = new ArrayList<StoreEntity>();
        for(StoreEntity storeEntity: storesBrandEntity.getStore()){
            StoreEntity store = new StoreEntity();
            store.setId(storeEntity.getId());
            store.setName(storeEntity.getName());
            store.setStreet(storeEntity.getStreet());
            store.setCity(storeEntity.getCity());
            store.setState(storeEntity.getState());
            store.setCountry(storeEntity.getCountry());
            store.setContactNo(storeEntity.getContactNo());
            store.setContactPerson(storeEntity.getContactPerson());
            store.setLatitude(storeEntity.getLatitude());
            store.setLongitude(storeEntity.getLongitude());
            store.setStatus(storeEntity.getStatus());
            store.setCreatedDate(storeEntity.getCreatedDate());
            stores.add(store);
        }
        storesBrand.setStore(stores);
        return storesBrand;
    }
*/
    @Override
    public void saveItem(RequestJsonDto requestJson, HeaderDto headerDto) throws Exception {
        log.info("++++++++++++ Saving Item +++++++++++++++");

        ItemEntity item = requestJson.getItem();
        List<CategoryEntity> itemCategories = requestJson.getItemCategories();
        List<Integer> itemStores = requestJson.getItemStores();
        List<ItemsAttributesTypeEntity> itemsAttributesTypes = requestJson.getItemAttributesTypes();
        List<String> itemsImages = requestJson.getItemImages();

        StoresBrandEntity storesBrand = merchantDaoService.findBrandDetail(Integer.parseInt(headerDto.getId()));
        if((storesBrand == null) || (storesBrand.getStatus() != Status.ACTIVE) )
            throw new YSException("VLD012");

        CategoryEntity category = new CategoryEntity();
        //save items category
        if(itemCategories.size()>1){
            CategoryEntity parentCategory = merchantDaoService.getCategoryById(itemCategories.get(0).getId());
            if(parentCategory.getItem().size() > 0)
                throw new YSException("VLD031");
            int i;
            for(i = 1; i<itemCategories.size(); i++){
               //if category with same same and same parent exists then just update the category
               CategoryEntity catExist = categoryDaoService.getCategory(itemCategories.get(i).getName(), itemCategories.get(i - 1).getId());
               if(catExist != null){
                    itemCategories.get(i).setId(catExist.getId());
               }
               itemCategories.get(i).setParent(itemCategories.get(i-1));
               itemCategories.get(i).setFeatured(false);
               itemCategories.get(i).setStoresBrand(storesBrand);
               itemCategories.get(i).setCreatedDate(DateUtil.getCurrentTimestampSQL());
            }
            itemCategories.set(0, parentCategory);
            merchantDaoService.saveCategories(itemCategories);
            //get items category
            category = itemCategories.get(itemCategories.size()-1);
        }else{
            category = merchantDaoService.getCategoryById(itemCategories.get(0).getId());
            if (category.getChild().size() > 0){
                throw new YSException("VLD032");
            }
        }

        item.setCategory(category);
        item.setStoresBrand(storesBrand);

        List<ItemEntity> itemExists =  itemDaoService.findItems(item.getStoresBrand().getId(), item.getCategory().getId(), item.getName());
        if(itemExists.size()>0){
            throw new YSException("ITM005");
        }

        item.setCreatedDate(DateUtil.getCurrentTimestampSQL());
        item.setModifiedDate(DateUtil.getCurrentTimestampSQL());

        List<ItemsStoreEntity> itemsStoreEntities = new ArrayList<>();
        for(Integer itemsStore: itemStores){
            ItemsStoreEntity itemsStoreEntity = new ItemsStoreEntity();
            StoreEntity store = new StoreEntity();
            store.setId(itemsStore);
            itemsStoreEntity.setStore(store);
            itemsStoreEntity.setItem(item);
            itemsStoreEntities.add(itemsStoreEntity);
        }

        for(ItemsAttributesTypeEntity itemsAttributeType: itemsAttributesTypes){
            itemsAttributeType.setItem(item);
            for (ItemsAttributeEntity itemsAttribute: itemsAttributeType.getItemsAttribute()) {
                itemsAttribute.setType(itemsAttributeType);
            }
        }

        item.setItemsStores(itemsStoreEntities);
        item.setAttributesTypes(itemsAttributesTypes);

       /* List<String> images = new ArrayList<>();
        if(itemsImages != null){
            for (String image: itemsImages){
                images.add(image);
            }
        }*/

        merchantDaoService.saveItem(item);

        if (itemsImages != null && itemsImages.size()>0) {
            log.info("Uploading item images to S3 Bucket ");

            String dir = MessageBundle.separateString("/", "Merchant_"+storesBrand.getMerchant().getId(), "Brand_"+ storesBrand.getId(), "item" + item.getId());
            boolean isLocal = MessageBundle.isLocalHost();

            List<ItemsImageEntity> itemsImageEntities = new ArrayList<>();
            int i = 0;
            for(String image: itemsImages){
                ItemsImageEntity itemsImage = new ItemsImageEntity();
                String itemImageUrl = "itemsImage"+ i + (isLocal ? "_tmp_" : "_") + item.getId()+System.currentTimeMillis();
                String s3PathImage = GeneralUtil.saveImageToBucket(image, itemImageUrl, dir, true);
                itemsImage.setUrl(s3PathImage);
                itemsImage.setItem(item);
                itemsImageEntities.add(itemsImage);
                i++;
            }
             merchantDaoService.saveItemImages(itemsImageEntities);
        }

    }

    @Override
    public void updateItem(RequestJsonDto requestJson) throws Exception {
        log.info("++++++++++++ updating Item +++++++++++++++");

        ItemEntity item = requestJson.getItem();
        List<CategoryEntity> itemCategories = requestJson.getItemCategories();
        List<Integer> itemStores = requestJson.getItemStores();
        List<ItemsAttributesTypeEntity> itemsAttributesTypes = requestJson.getItemAttributesTypes();
        List<ItemsImageEntity> itemsImages = requestJson.getEditItemImages();

        ItemEntity dbItem = merchantDaoService.getItemDetail(item.getId());

        if(dbItem == null){
            throw new YSException("VLD020");
        }

        dbItem.setName(item.getName());
        dbItem.setDescription(item.getDescription());
        dbItem.setAdditionalOffer(item.getAdditionalOffer());
        dbItem.setStatus(item.getStatus());
        dbItem.setAvailableEndTime(item.getAvailableEndTime());
        dbItem.setAvailableStartTime(item.getAvailableStartTime());
        dbItem.setCurrencyType(item.getCurrencyType());
        dbItem.setMaxOrderQuantity(item.getMaxOrderQuantity());
        dbItem.setMinOrderQuantity(item.getMinOrderQuantity());
        dbItem.setVat(item.getVat());
        dbItem.setServiceCharge(item.getServiceCharge());
        dbItem.setUnitPrice(item.getUnitPrice());
        dbItem.setModifiedDate(DateUtil.getCurrentTimestampSQL());


        StoresBrandEntity storesBrand = dbItem.getStoresBrand();
        List<ItemsStoreEntity> dbItemStoreEntities = dbItem.getItemsStores();
        List<ItemsAttributesTypeEntity> dbItemsAttributesTypes = dbItem.getAttributesTypes();
        List<Integer> dbItemsAttributesTypeIdList = new ArrayList<Integer>();

        List<Integer> dbItemsStoreIdList = new ArrayList<Integer>();
        Map<Integer, ItemsAttributesTypeEntity> dbItemAttributesTypesMap = new HashMap<Integer, ItemsAttributesTypeEntity>();
        Map<Integer, ItemsAttributeEntity> dbItemAttributesMap = new HashMap<Integer, ItemsAttributeEntity>();

        for (ItemsStoreEntity dbItemsStore: dbItemStoreEntities){
            dbItemsStoreIdList.add(dbItemsStore.getStore().getId());
        }

        for (ItemsAttributesTypeEntity itemsAttributesType: dbItemsAttributesTypes){
            //dbItemAttributesTypesMap.put(itemsAttributesType.getId(), itemsAttributesType);
            for (ItemsAttributeEntity attribute: itemsAttributesType.getItemsAttribute()){
                dbItemAttributesMap.put(attribute.getId(), attribute);
            }
        }

        for(Integer itemsStore: itemStores){
            if(!dbItemsStoreIdList.contains(itemsStore)){
                ItemsStoreEntity itemsStoreEntity = new ItemsStoreEntity();
                StoreEntity store = new StoreEntity();
                store.setId(itemsStore);
                itemsStoreEntity.setStore(store);
                itemsStoreEntity.setItem(dbItem);
                dbItemStoreEntities.add(itemsStoreEntity);
            }
        }


        for (ItemsAttributesTypeEntity itemsAttributesTypeEntity: dbItemsAttributesTypes)  {
            dbItemAttributesTypesMap.put(itemsAttributesTypeEntity.getId(), itemsAttributesTypeEntity);
            dbItemsAttributesTypeIdList.add(itemsAttributesTypeEntity.getId());
        }

        List<ItemsAttributesTypeEntity> dbItemAttributesTypes = dbItem.getAttributesTypes();
        for(ItemsAttributesTypeEntity itemsAttributeType: itemsAttributesTypes){
            if(itemsAttributeType.getId() != null){
                ItemsAttributesTypeEntity dbItemAttributesType =  dbItemAttributesTypesMap.get(itemsAttributeType.getId());
                itemsAttributeType.setItem(dbItem);
                dbItemAttributesType.setMultiSelect(itemsAttributeType.getMultiSelect());
                dbItemAttributesType.setType(itemsAttributeType.getType());
                for (ItemsAttributeEntity itemsAttribute: itemsAttributeType.getItemsAttribute()) {
                    if(itemsAttribute.getId() != null){
                        ItemsAttributeEntity dbItemAttribute =  dbItemAttributesMap.get(itemsAttribute.getId());
                        dbItemAttribute.setType(dbItemAttributesType);
                        dbItemAttribute.setAttribute(itemsAttribute.getAttribute());
                        dbItemAttribute.setUnitPrice(itemsAttribute.getUnitPrice());
                    }else{
                        dbItemAttributesType.getItemsAttribute().add(itemsAttribute);
                    }
                }
            }else{
                itemsAttributeType.setItem(dbItem);
                dbItemAttributesTypes.add(itemsAttributeType);
                for (ItemsAttributeEntity itemsAttribute: itemsAttributeType.getItemsAttribute()) {
                    itemsAttribute.setType(itemsAttributeType);
                }
            }
        }

        dbItem.setItemsStores(dbItemStoreEntities);
        dbItem.setAttributesTypes(dbItemAttributesTypes);

        List<ItemsImageEntity> dbImages = dbItem.getItemsImage();
        Map<Integer, ItemsImageEntity> dbItemsImagesIdMap = new HashMap<>();
        Map<Integer, String> dbItemsImagesUrlMap = new HashMap<>();
        for (ItemsImageEntity itemsImage: dbImages){
            if(itemsImage.getId() != null){
                dbItemsImagesIdMap.put(itemsImage.getId(), itemsImage);
                dbItemsImagesUrlMap.put(itemsImage.getId(), itemsImage.getUrl());
            }
        }

        dbItem.setModifiedDate(DateUtil.getCurrentTimestampSQL());

        CategoryEntity category = new CategoryEntity();
        //update items category
        if(itemCategories.size()>1){
            CategoryEntity parentCategory = merchantDaoService.getCategoryById(itemCategories.get(0).getId());
            if(parentCategory.getItem().size() > 0)
                throw new YSException("VLD031");

            int i;
            for(i = 1; i<itemCategories.size(); i++){
                itemCategories.get(i).setParent(itemCategories.get(i-1));
                itemCategories.get(i).setFeatured(false);
                itemCategories.get(i).setStoresBrand(storesBrand);
                itemCategories.get(i).setCreatedDate(DateUtil.getCurrentTimestampSQL());
            }
            itemCategories.set(0, parentCategory);
            merchantDaoService.saveCategories(itemCategories);
            //get items category
            category = itemCategories.get(itemCategories.size()-1);
        }else{
            category = merchantDaoService.getCategoryById(itemCategories.get(0).getId());
            if (category.getChild().size() > 0){
                throw new YSException("VLD032");
            }
        }

        dbItem.setCategory(category);

        merchantDaoService.updateItem(dbItem);

        //add new images
        List<Integer> itemsImagesIdList = new ArrayList<Integer>();
        //Map<Integer, ItemsImageEntity> itemsImagesIdMap = new HashMap<>();
        if(itemsImages != null){
            for (ItemsImageEntity image: itemsImages){
                if(image.getId() == null){
                    dbImages.add(image);
                } else if(image.getUrl() == null){
                    dbImages.remove(dbItemsImagesIdMap.get(image.getId()));
                    AmazonUtil.deleteFileFromBucket(AmazonUtil.getAmazonS3Key(dbItemsImagesUrlMap.get(image.getId())));
                } else if(image.getId() != null && image.getUrl() != null) {
                    dbItemsImagesIdMap.get(image.getId()).setUrl(image.getUrl());
                }
            }
        }

        if (dbImages != null && dbImages.size()>0) {
            log.info("Uploading item images to S3 Bucket ");
            String dir = MessageBundle.separateString("/", "Merchant_"+storesBrand.getMerchant().getId(), "Brand_"+ storesBrand.getId(), "item" + dbItem.getId());
            boolean isLocal = MessageBundle.isLocalHost();
                int i = 0;
                for(ItemsImageEntity image: dbImages){
                    image.setItem(dbItem);
                    if(image.getId() != null && !image.getUrl().contains("https://")) { //has url  and is updated
                        //if(image.getId() != null){     //not new
                            if(image.getUrl() != null && dbItemsImagesUrlMap.get(image.getId()).contains("https://")){
                                AmazonUtil.deleteFileFromBucket(AmazonUtil.getAmazonS3Key(dbItemsImagesUrlMap.get(image.getId())));
                            }

                            if(image.getUrl() != null){
                                String itemImageUrl = "itemsImage"+ i + (isLocal ? "_tmp_" : "_") + item.getId()+System.currentTimeMillis();
                                String s3PathImage = GeneralUtil.saveImageToBucket(image.getUrl(), itemImageUrl, dir, true);
                                image.setUrl(s3PathImage);
                                image.setItem(dbItem);
                                i++;
                            }
                        }else if(image.getId() == null){
                            if(image.getUrl()!=null){
                                String itemImageUrl = "itemsImage"+ i + (isLocal ? "_tmp_" : "_") + item.getId()+System.currentTimeMillis();
                                String s3PathImage = GeneralUtil.saveImageToBucket(image.getUrl(), itemImageUrl, dir, true);
                                image.setUrl(s3PathImage);
                                image.setItem(dbItem);
                                i++;
                            }
                        }
                }
                merchantDaoService.updateItemImages(dbImages);
        }

    }

    @Override
    public List<StoreEntity> findStoresByBrand(HeaderDto headerDto) throws Exception {
        log.info("++++++++++++ getting stores of the brand +++++++++++++++");
        return merchantDaoService.findStoreByBrand(Integer.parseInt(headerDto.getId()));
    }

    @Override
    public List<CategoryEntity> findCategoriesByBrand(HeaderDto headerDto) throws Exception {
        log.info("++++++++++++ getting categories of brand +++++++++++++++");
        List<BrandsCategoryEntity> brandsCategories =  merchantDaoService.getBrandsCategory(Integer.parseInt(headerDto.getId()));
        List<CategoryEntity> categories = new ArrayList<>();
        List<CategoryEntity> allCategories;
        allCategories = merchantDaoService.getCategories();

            for (BrandsCategoryEntity brandsCategory: brandsCategories){
                List<CategoryEntity> newCategories = new ArrayList<CategoryEntity>();
                CategoryEntity newCategory;
                newCategory = brandsCategory.getCategory();
                newCategories =  getCategoryTree(allCategories, brandsCategory.getCategory().getId(), brandsCategory.getStoresBrand().getId());
                List<ItemEntity> items = newCategory.getItem();
               /* if(items.size() > 0){
                    newCategory.setItem(new ArrayList<ItemEntity>());
                } else {
                    newCategory.setItem(null);
                }*/
                newCategory.setChild(null);
                newCategory.setChild(newCategories);
                categories.add(newCategory);
            }

        List<CategoryEntity> objects = new ArrayList<>();

        String fields = "id,name,child,item";

        Map<String, String> assoc = new HashMap<>();
        Map<String, String> subAssoc = new HashMap<>();

        assoc.put("child", "id,name,child,item");
        assoc.put("item", "");

        subAssoc.put("child", "id,name,child,item");

        for (CategoryEntity category:categories){
            objects.add((CategoryEntity) ReturnJsonUtil.getJsonObject(category, fields, assoc, subAssoc));
        }

        return objects;

    }

    @Override
    public List<CategoryEntity> findBrandCategoryList(HeaderDto headerDto) throws Exception {
        log.info("++++++++++++ getting brand category list +++++++++++++++");
        List<Integer> brandId = new ArrayList<Integer>();
        List<CategoryEntity> categories = new ArrayList<>();
        if(headerDto.getId() != null){
                String[] brands = headerDto.getId().split(",");
                for (String brand: brands){
                    brandId.add(Integer.parseInt(brand));
                }


            List<BrandsCategoryEntity> brandsCategories =  merchantDaoService.getBrandsCategory(brandId);

            for (BrandsCategoryEntity brandsCategory: brandsCategories){
                brandsCategory.getCategory().setChild(null);
                brandsCategory.getCategory().setParent(null);
                brandsCategory.getCategory().setStoresBrand(null);
                brandsCategory.getCategory().setItem(null);
                brandsCategory.getCategory().setBrandsCategory(null);
                if(!categories.contains(brandsCategory.getCategory())) {
                    categories.add(brandsCategory.getCategory());
                }
            }
        }else{
            categories = merchantDaoService.findParentCategories();
        }
        return categories;
    }

    public List<CategoryEntity> getCategoryTree(List<CategoryEntity> categories, Integer parent_id, Integer storeId){
        log.info("++++++++++++ getting category tree +++++++++++++++");
          List<CategoryEntity> newCategories = new ArrayList<CategoryEntity>();
          for(CategoryEntity newCategory:  categories)
          {
            if(newCategory.getParent().getId().equals(parent_id) && (newCategory.getStoresBrand()== null || newCategory.getStoresBrand().getId() == storeId ))
            {
                newCategory.setChild(getCategoryTree(categories, newCategory.getId(), storeId));
                List<ItemEntity> items = newCategory.getItem();
                if(items.size() > 0){
                    newCategory.setItem(new ArrayList<ItemEntity>());
                } else {
                    newCategory.setItem(null);
                }
                newCategories.add(newCategory);
            }
          }
          return newCategories;
    }


    //findChildCategories
    @Override
    public List<CategoryEntity> findChildCategories(RequestJsonDto requestJson) throws Exception {
        log.info("++++++++++++ getting child categories +++++++++++++++");
        Integer parentId = requestJson.getParentCategoryId();
        Integer storeId = requestJson.getCategoryStoreId();
        List<CategoryEntity> categories;
        List<CategoryEntity> newCategories = new ArrayList<CategoryEntity>();

        categories = merchantDaoService.getCategories();

        if(categories.size() > 0){
            newCategories =  getCategoryTree(categories, parentId, storeId);
        }

        List<CategoryEntity> objects = new ArrayList<>();

        String fields = "id,name,child,item";

        Map<String, String> assoc = new HashMap<>();
        Map<String, String> subAssoc = new HashMap<>();

        assoc.put("child", "id,name,child,item");
        assoc.put("item", "");

        subAssoc.put("child", "id,name,child,item");

        for (CategoryEntity category:newCategories){
            objects.add((CategoryEntity) ReturnJsonUtil.getJsonObject(category, fields, assoc, subAssoc));
        }


        return  objects;
    }

    @Override
    public PaginationDto findCategoriesItems(RequestJsonDto requestJson) throws Exception {
        log.info("++++++++++++ finding items of the categories +++++++++++++++");
        Integer categoryId = requestJson.getParentCategoryId();
        Integer brandId = requestJson.getCategoryStoreId();
        Page page = requestJson.getPage();


        PaginationDto paginationDto = new PaginationDto();
        Integer totalRows =  merchantDaoService.getTotalNumberOfItems(categoryId, brandId);
        paginationDto.setNumberOfRows(totalRows);

        if(page != null){
            page.setTotalRows(totalRows);
        }

        List<ItemEntity> items = merchantDaoService.getCategoriesItems(categoryId, brandId, page);
           /*if(items.size()>0){
               for (ItemEntity item: items){
                   item.getCategory().setItem(null);
                   item.setItemsStores(null);
                   item.setStoresBrand(null);
                   item.setCarts(null);
                   item.setItemsOrder(null);
               }
           }
*/
        List<ItemEntity> objects = new ArrayList<>();

        String fields = "id,name,unitPrice,status,currencyType,additionalOffer,itemsImage";

        Map<String, String> assoc = new HashMap<>();
        Map<String, String> subAssoc = new HashMap<>();

        assoc.put("itemsImage", "url");

        for (ItemEntity item:items){
            objects.add((ItemEntity) ReturnJsonUtil.getJsonObject(item, fields, assoc));
        }


        paginationDto.setData(objects);
        return paginationDto;
    }

    @Override
    public ItemEntity getItemDetail(HeaderDto headerDto) throws Exception {
        log.info("++++++++++++ getting item detail +++++++++++++++");
        ItemEntity item = merchantDaoService.getItemDetail(Integer.parseInt(headerDto.getId()));
        CategoryEntity category = item.getCategory();
        category.setItem(null);
        while(category.getParent() != null){
            List<CategoryEntity> childCategories = new ArrayList<CategoryEntity>();
            childCategories.add(category);
            category = category.getParent();
            category.setChild(childCategories);
        }
        StoresBrandEntity storesBrand = item.getStoresBrand();
        storesBrand.setBrandsCategory(null);
        List<StoreEntity> stores = storesBrand.getStore();
        if(stores.size() > 0){
            for (StoreEntity store: stores){
                store.setItemsStore(null);
            }
        }
        item.setCategory(category);

        String fields = "id,name,description,availableQuantity,availableStartTime,availableEndTime,maxOrderQuantity,minOrderQuantity,unitPrice,currencyType,additionalOffer,status,vat,serviceCharge,deliveryFee,itemsImage,itemsStores,attributesTypes,category,storesBrand";

        Map<String, String> assoc = new HashMap<>();
        Map<String, String> subAssoc = new HashMap<>();

        assoc.put("category", "id,name,child");
        assoc.put("storesBrand", "id,brandName");
        assoc.put("itemsImage", "id,url");
        assoc.put("itemsStores", "id,store");
        assoc.put("attributesTypes", "id,type,multiSelect,itemsAttribute");

        subAssoc.put("itemsAttribute", "id,attribute,unitPrice,selected");
        subAssoc.put("store", "id,street,city,state,country");
        subAssoc.put("child", "id,name,child");
        return (ItemEntity) ReturnJsonUtil.getJsonObject(item, fields, assoc, subAssoc);

        //return item;
    }

    @Override
    public List<ItemEntity> findStoresItems(HeaderDto headerDto) throws Exception {
        log.info("++++++++++++ find stores items +++++++++++++++");
        List<ItemsStoreEntity> itemsStores = merchantDaoService.findItemsStores(Integer.parseInt(headerDto.getId()));
        if(itemsStores.size() == 0)
            throw new YSException("VLD016");

        List<ItemEntity> items = new ArrayList<ItemEntity>();
        for (ItemsStoreEntity itemsStore:itemsStores){
              ItemEntity item = merchantDaoService.getItemDetail(itemsStore.getItem().getId());
              items.add(item);
        }
        return items;
    }

    @Override
    public List<CategoryEntity> findMerchantsDefaultCategory(HeaderDto headerDto) throws Exception {
        log.info("++++++++++++ getting default categories of the merchant +++++++++++++++");
        List<StoresBrandEntity> storesBrands = merchantDaoService.findBrandListByMerchant(headerDto.getMerchantId());
        List<BrandsCategoryEntity> brandsCategories =  merchantDaoService.getBrandsCategory(storesBrands.get(0).getId());
        List<CategoryEntity> categories = new ArrayList<>();

        for (BrandsCategoryEntity brandsCategory: brandsCategories){
            categories.add(brandsCategory.getCategory());
        }

        return categories;
    }


    @Override
    public List<CategoryEntity> findParentCategoriesItems(RequestJsonDto requestJson) throws Exception {
        log.info("++++++++++++ getting items of the parent categories +++++++++++++++");
        Integer parentId = requestJson.getParentCategoryId();
        Integer storeId = requestJson.getCategoryStoreId();
        Integer itemCount = requestJson.getParentCategoriesItemsCount();
        if (itemCount == null){
            itemCount = 4;
        }

        Page page = requestJson.getPage();

        PaginationDto paginationDto = new PaginationDto();
        Integer totalRows =  merchantDaoService.getTotalNumberOfChildCategory(parentId, storeId);
        paginationDto.setNumberOfRows(totalRows);

        if(page != null){
            page.setTotalRows(totalRows);
        }

        List<CategoryEntity> childCategories =  merchantDaoService.findChildCategories(parentId, storeId, page);
        List<CategoryEntity> finalCategories =  merchantDaoService.findFinalCategoryList(storeId);

        //set child of each childCategory empty so that final categories can be added as the childs of the child category
        for (CategoryEntity childCategory: childCategories){
            childCategory.setChild(new ArrayList<CategoryEntity>());
        }

        //add final categories as the childs of the child category
        Integer childCnt = finalCategories.size();
        for (CategoryEntity finalCategory: finalCategories){

           CategoryEntity finalCat = finalCategory;

            //check if the final category is child category itself
           if(!childCategories.contains(finalCat.getId())){
               while (finalCategory.getParent() != null && !childCategories.contains(finalCategory.getParent())){
                   finalCategory = finalCategory.getParent();
               }

               if(childCategories.contains(finalCategory.getParent())) {
                   //get all the childs of the child category
                   List<CategoryEntity> parentsChild = finalCategory.getParent().getChild();
                   parentsChild.add(finalCat);
                   //set new child element of the child category
                   finalCategory.getParent().setChild(parentsChild);
               }
           }
        }

        //get items for each child category
        for (CategoryEntity childCategory: childCategories){
            //get the list of childs id of the child category
            List<Integer> childsChildId = new ArrayList<Integer>();
            if(childCategory.getChild() != null && childCategory.getChild().size() >0 ){
                for(CategoryEntity childsChildCategory: childCategory.getChild()){
                    childsChildId.add(childsChildCategory.getId());
                }
            }

            /*if childs of child category exits get the items of the childs of the child category(final category)
              else get the items of child category itself as the final category
             */
            if(childsChildId.size() > 0){
                List<ItemEntity> categoriesItems = merchantDaoService.findItemByCategory(childsChildId, storeId, itemCount);
                /*if(categoriesItems.size() > 0){
                    for(ItemEntity item: categoriesItems){
                        item.setCategory(null);
                        item.setStoresBrand(null);
                        item.setItemsStores(null);
                    }
                }*/
                childCategory.setItem(categoriesItems);
            }else{
                List<ItemEntity> categoriesItems = merchantDaoService.getCategoriesItems(childCategory.getId(), storeId, itemCount);
               /* if(categoriesItems.size() > 0){
                    for(ItemEntity item: categoriesItems){
                        item.setCategory(null);
                        item.setStoresBrand(null);
                        item.setItemsStores(null);
                    }
                }*/
                childCategory.setItem(categoriesItems);
            }



            childCategory.setChild(null);
            childCategory.setParent(null);
        }

        List<CategoryEntity> objects = new ArrayList<>();

        String fields = "id,name,item";

        Map<String, String> assoc = new HashMap<>();
        Map<String, String> subAssoc = new HashMap<>();

        assoc.put("item", "id,name,unitPrice,currencyType,status,additionalOffer,itemsImage");
        subAssoc.put("itemsImage", "url");

        for (CategoryEntity childCategory:childCategories){
            objects.add((CategoryEntity) ReturnJsonUtil.getJsonObject(childCategory, fields, assoc, subAssoc));
        }

        return  objects;


    }

    @Override
    public List<CategoryEntity> findParentCategoriesByBrand(HeaderDto headerDto) throws Exception {
        log.info("++++++++++++ getting parent categories of the brand +++++++++++++++");
        List<BrandsCategoryEntity> brandsCategories =  merchantDaoService.getBrandsCategory(Integer.parseInt(headerDto.getId()));
        List<CategoryEntity> categories = new ArrayList<>();

        for (BrandsCategoryEntity brandsCategory: brandsCategories){
            categories.add(brandsCategory.getCategory());
        }

        List<CategoryEntity> objects = new ArrayList<>();

        String fields = "id,name";

        for (CategoryEntity category:categories){
             objects.add((CategoryEntity) ReturnJsonUtil.getJsonObject(category, fields));
        }

        return objects;
    }


    @Override
    public boolean changeStatus(RequestJsonDto requestJsonDto, HeaderDto headerDto) throws Exception{
        log.info("++++++++++++ changing status of "+requestJsonDto.getClassName()+" +++++++++++++++");
        String type = requestJsonDto.getClassName();
        Integer statusId = requestJsonDto.getStatusId();
        String[] ids =  headerDto.getId().split(",");

        if(type.equals("User")){
            for(String id: ids) {
                UserEntity user =   userDaoService.find(Integer.parseInt(id));
                user.setStatus(Status.fromInt(statusId));
                //user.setLastActivityDate(DateUtil.getCurrentTimestampSQL());
                userDaoService.update(user);
            }
        }else if(type.equals("Brand")) {
            for(String id: ids) {
                StoresBrandEntity storesBrand =   merchantDaoService.findBrandDetail(Integer.parseInt(id));
                storesBrand.setStatus(Status.fromInt(statusId));
                if(statusId==3){
                    storesBrand.setFeatured(null);
                    storesBrand.setPriority(null);
                }
                merchantDaoService.updateStoresBrand(storesBrand);
            }
        }  else if(type.equals("Store")){
            for(String id: ids) {
                StoreEntity store = merchantDaoService.getStoreById(Integer.parseInt(id));
                List<StoreEntity> stores = new ArrayList<StoreEntity>();
                store.setStatus(Status.fromInt(statusId));
                stores.add(store);
                merchantDaoService.updateStores(stores);
            }
        }  else if(type.equals("Item")){
            for(String id: ids) {
                ItemEntity item = merchantDaoService.getItemDetail(Integer.parseInt(id));
                item.setStatus(Status.fromInt(statusId));
                item.setModifiedDate(DateUtil.getCurrentTimestampSQL());
                merchantDaoService.updateItem(item);
            }
        }
        return  true;
    }

    @Override
    public PaginationDto getWebItemSearch(RequestJsonDto requestJsonDto) throws Exception{
        log.info("++++++++++++ getting search items +++++++++++++++");
        List<Integer> parentCategoryId = requestJsonDto.getCategories();
        List<Integer> storeId = requestJsonDto.getBrands();

        String searchString = requestJsonDto.getSearchString();
        Page page = requestJsonDto.getPage();

        if(parentCategoryId == null){
            parentCategoryId = new ArrayList<>();
            List<CategoryEntity> categories = merchantDaoService.findParentCategories();
            for (CategoryEntity category: categories){
                parentCategoryId.add(category.getId());
            }
        }

        if (storeId == null){
            storeId = new ArrayList<>();
            List<StoresBrandEntity> storesBrands = merchantDaoService.findBrandList();
            for (StoresBrandEntity storesBrand: storesBrands){
                storeId.add(storesBrand.getId());
            }
        }

        List<CategoryEntity> finalCategories = merchantDaoService.findFinalCategoryList(storeId);
        List<Integer> categoryId = new ArrayList<>();

        for (CategoryEntity finalCategory: finalCategories){
             CategoryEntity finalCat = finalCategory;
             if(finalCategory.getParent() == null && !parentCategoryId.contains(finalCategory.getId())){
                 continue;
             }
             if(parentCategoryId.contains(finalCategory.getId())){
                  categoryId.add(finalCategory.getId());
             }else{
                    while (finalCategory.getParent() != null && !parentCategoryId.contains(finalCategory.getId())){
                        finalCategory = finalCategory.getParent();
                    }
                    if(parentCategoryId.contains(finalCategory.getId())) {
                        categoryId.add(finalCat.getId());
                    }
             }
        }

        PaginationDto paginationDto = new PaginationDto();
        Integer totalRows =  merchantDaoService.getTotalNumberOfItems(searchString, categoryId, storeId);
        paginationDto.setNumberOfRows(totalRows);

        if(page != null){
            page.setTotalRows(totalRows);
        }

        List<ItemEntity> items = merchantDaoService.getWebSearchItem(searchString, categoryId, storeId, page);
        /*if(items.size()>0){
             for (ItemEntity item: items){
                 item.setCategory(null);
                 item.setItemsStores(null);
                 item.setStoresBrand(null);
                 item.setAttributesTypes(null);
                 item.setCarts(null);
                 item.setItemsOrder(null);
             }
        }*/

        List<ItemEntity> ItemList = new ArrayList<ItemEntity>();

        String fields = "id,name,status,unitPrice,currencyType,itemsImage";

        Map<String, String> assoc = new HashMap<>();

        assoc.put("itemsImage", "url");

        for(ItemEntity item: items){
            ItemList.add((ItemEntity) ReturnJsonUtil.getJsonObject(item, fields, assoc));
        }

        paginationDto.setData(ItemList);
        return paginationDto;
    }

    @Override
    public PaginationDto getOrders(HeaderDto headerDto, RequestJsonDto requestJson) throws Exception {
        log.info("++++++++++++ getting orders +++++++++++++++");
        Page page = requestJson.getPage();
        List<Integer> storeIdList = new ArrayList<Integer>();
        if(headerDto.getMerchantId() == null){
            List<Integer> brandIdList = merchantDaoService.getBrandIdList();
            storeIdList = merchantDaoService.getStoreIdList(brandIdList);
        }else{

            MerchantEntity dbMerchant = merchantDaoService.find(headerDto.getMerchantId());
            if(dbMerchant == null)
                throw new YSException("VLD011");
            List<Integer> brandIdList = merchantDaoService.getBrandIdList(headerDto.getMerchantId());

            if(brandIdList.size() == 0)
                throw new YSException("VLD012");

            storeIdList = merchantDaoService.getStoreIdList(brandIdList);

            if(storeIdList.size() == 0)
                throw new YSException("VLD012");


        }

        PaginationDto paginationDto = new PaginationDto();
        Integer totalRows;
        if(requestJson.getDeliveryStatus() != null){
            totalRows =  merchantDaoService.getTotalNumbersOfOrders(storeIdList, requestJson.getDeliveryStatus());
        }else{
            totalRows =  merchantDaoService.getTotalNumbersOfOrders(storeIdList);
        }
        paginationDto.setNumberOfRows(totalRows);

        if(page != null){
            page.setTotalRows(totalRows);
        }
        List<OrderEntity> orders;
        if(requestJson.getDeliveryStatus() != null){
            orders = merchantDaoService.getOrders(storeIdList, page, requestJson.getDeliveryStatus());
        } else{
            orders = merchantDaoService.getOrders(storeIdList, page);
        }

        List<Object> objects = new ArrayList<>();

        String fields = "id,orderName,orderStatus,deliveryStatus,orderDate,customer,orderVerificationCode,store,deliveryBoy,attachments,grandTotal,rating";

        Map<String, String> assoc = new HashMap<>();
        Map<String, String> subAssoc = new HashMap<>();

        assoc.put("customer", "id,user");
        assoc.put("deliveryBoy", "id,user,averageRating");
        assoc.put("store", "id,name,street,contactPerson,contactNo");
        assoc.put("attachments", "url");
        assoc.put("rating", "id,customerRating,deliveryBoyRating,deliveryBoyComment,customerComment");
        assoc.put("orderCancel", "id,reasonDetails,reason");

        subAssoc.put("user", "id,fullName,mobileNumber,profileImage");
        subAssoc.put("reasonDetails", "id,cancelReason");

        for (OrderEntity order:orders){
            objects.add(ReturnJsonUtil.getJsonObject(order, fields, assoc, subAssoc));
        }

        paginationDto.setData(objects);
        return paginationDto;
    }

    @Override
    public PaginationDto getPurchaseHistory(HeaderDto headerDto, RequestJsonDto requestJson) throws Exception {
        log.info("++++++++++++ getting purchase history +++++++++++++++");
        Page page = requestJson.getPage();
        List<Integer> storeIdList = new ArrayList<Integer>();
        if(headerDto.getMerchantId() == null){
            List<Integer> brandIdList = merchantDaoService.getBrandIdList();
            storeIdList = merchantDaoService.getStoreIdList(brandIdList);
        }else{

            MerchantEntity dbMerchant = merchantDaoService.find(headerDto.getMerchantId());
            if(dbMerchant == null)
                throw new YSException("VLD011");
            List<Integer> brandIdList = merchantDaoService.getBrandIdList(headerDto.getMerchantId());

            if(brandIdList.size() == 0)
                throw new YSException("VLD012");

            storeIdList = merchantDaoService.getStoreIdList(brandIdList);

            if(storeIdList.size() == 0)
                throw new YSException("VLD012");
        }

        PaginationDto paginationDto = new PaginationDto();
        Integer totalRows =  merchantDaoService.getTotalNumbersOfPurchases(storeIdList);
        paginationDto.setNumberOfRows(totalRows);

        if(page != null){
            page.setTotalRows(totalRows);
        }

        List<OrderEntity> orders = merchantDaoService.getPurchaseHistory(storeIdList, page);

        List<Object> objects = new ArrayList<>();

        String fields = "id,orderName,deliveryStatus,orderStatus,attachments,orderDate,customer,store,deliveryBoy,totalCost";

        Map<String, String> assoc = new HashMap<>();
        Map<String, String> subAssoc = new HashMap<>();

        assoc.put("customer", "id,user");
        assoc.put("deliveryBoy", "id,user,averageRating");
        assoc.put("store", "id,name,street,contactPerson,contactNo");
        assoc.put("attachments", "url");

        subAssoc.put("user", "id,fullName,mobileNumber,profileImage");

        for (OrderEntity order:orders){
            objects.add(ReturnJsonUtil.getJsonObject(order, fields, assoc, subAssoc));
        }

        paginationDto.setData(objects);
        return paginationDto;
    }

    @Override
    public PaginationDto getOrderItems(HeaderDto headerDto, RequestJsonDto requestJsonDto) throws Exception {
        log.info("++++++++++++ getting items of order: "+headerDto.getId()+" +++++++++++++++");
        Page page = requestJsonDto.getPage();

        PaginationDto paginationDto = new PaginationDto();
        Integer totalRows =  merchantDaoService.getTotalNumbersItems(Integer.parseInt(headerDto.getId()));
        paginationDto.setNumberOfRows(totalRows);

        if(page != null){
            page.setTotalRows(totalRows);
        }

        List<ItemsOrderEntity> items = merchantDaoService.getOrdersItems(Integer.parseInt(headerDto.getId()), page);

        for(ItemsOrderEntity item: items){
            if(item.getItem() == null && item.getCustomItem() != null){
                CustomItemEntity customItem = item.getCustomItem();
                ItemEntity new_item = new ItemEntity();
                new_item.setName(customItem.getName());
                item.setItem(new_item);
            }
        }

        List<Object> objects = new ArrayList<>();

        String fields = "id,quantity,itemTotal,availabilityStatus,serviceCharge,vat,item,order";

        Map<String, String> assoc = new HashMap<>();
        assoc.put("item", "id,name");
        assoc.put("order", "id");

        for (ItemsOrderEntity item:items){
            objects.add(ReturnJsonUtil.getJsonObject(item, fields, assoc));
        }

        paginationDto.setData(objects);
        return paginationDto;
    }



    @Override
    public void addItemsImages(HeaderDto headerDto, RequestJsonDto requestJsonDto) throws Exception{
        log.info("++++++++++++ saving items image +++++++++++++++");
        Integer itemId = Integer.parseInt(headerDto.getId());
        List<String> itemsImages = requestJsonDto.getItemImages();
        ItemEntity item = merchantDaoService.getItemDetail(itemId);

        List<ItemsImageEntity> dbImages = item.getItemsImage();
        if (dbImages == null || dbImages.size()==0){
            if (itemsImages != null && itemsImages.size()>0) {
                log.info("Uploading item images to S3 Bucket ");

                String dir = MessageBundle.separateString("/", "Merchant_"+item.getStoresBrand().getMerchant().getId(), "Brand_"+ item.getStoresBrand().getId(), "item" + item.getId());
                boolean isLocal = MessageBundle.isLocalHost();

                List<ItemsImageEntity> itemsImageEntities = new ArrayList<>();
                int i = 0;
                for(String image: itemsImages){
                    ItemsImageEntity itemsImage = new ItemsImageEntity();
                    String itemImageUrl = "itemsImage"+ i + (isLocal ? "_tmp_" : "_") + item.getId()+System.currentTimeMillis();
                    String s3PathImage = GeneralUtil.saveImageToBucket(image, itemImageUrl, dir, true);
                    itemsImage.setUrl(s3PathImage);
                    itemsImage.setItem(item);
                    itemsImageEntities.add(itemsImage);
                    i++;
                }
                merchantDaoService.saveItemImages(itemsImageEntities);
            }
        }else{
               String dir = MessageBundle.separateString("/", "Merchant_"+item.getStoresBrand().getMerchant().getId(), "Brand_"+ item.getStoresBrand().getId(), "item" + item.getId());
               boolean isLocal = MessageBundle.isLocalHost();

               List<ItemsImageEntity> itemsImageEntities = new ArrayList<>();
               ItemsImageEntity itemsImage = dbImages.get(0);
               log.info("deleting items image from S3 Bucket ");
               AmazonUtil.deleteFileFromBucket(AmazonUtil.getAmazonS3Key(itemsImage.getUrl()));
               String itemImageUrl = "itemsImage"+ 0 + (isLocal ? "_tmp_" : "_") + item.getId()+System.currentTimeMillis();
               String s3PathImage = GeneralUtil.saveImageToBucket(itemsImages.get(0), itemImageUrl, dir, true);
               itemsImage.setUrl(s3PathImage);
               itemsImageEntities.add(itemsImage);
               merchantDaoService.updateItemImages(itemsImageEntities);

        }

    }

    @Override
    public PaginationDto getInvoices(HeaderDto headerDto, RequestJsonDto requestJsonDto) throws Exception{
        log.info("++++++++++++ getting invoices +++++++++++++++");
        Map<String, Date> dateRange = requestJsonDto.getDateRange();
        Date fromDate = null;
        Date toDate = null;
        if(dateRange != null){
            if(dateRange.get("fromDate") != null){
                fromDate = dateRange.get("fromDate");
            }
            if(dateRange.get("toDate") != null){
                toDate = dateRange.get("toDate");
            }
        }


        Page page = requestJsonDto.getPage();


        PaginationDto paginationDto = new PaginationDto();
        Integer totalRows =  invoiceDaoService.getTotalNumberOfInvoices(headerDto.getMerchantId());
        paginationDto.setNumberOfRows(totalRows);

        if(page != null){
            page.setTotalRows(totalRows);
        }

        List<InvoiceEntity> invoices = new ArrayList<>();

        List<InvoiceEntity> invoiceList = new ArrayList<>();

        if(fromDate != null && toDate != null){
            invoiceList = invoiceDaoService.findInvoicesByMerchant(headerDto.getMerchantId(), page, fromDate, toDate);
        }else{
            invoiceList = invoiceDaoService.findInvoicesByMerchant(headerDto.getMerchantId(), page);
        }




        List<StoreEntity> storeEntityList = new ArrayList<>();
        String fields = "id,generatedDate,path,amount,fromDate,toDate,invoicePaid,paidDate,store";
        Map<String, String> assoc = new HashMap<>();
        Map<String, String> subAssoc = new HashMap<>();

        assoc.put("store", "id,street,storesBrand");
        subAssoc.put("storesBrand", "id,brandName");

        for (InvoiceEntity invoiceEntity: invoiceList){
            invoices.add((InvoiceEntity) ReturnJsonUtil.getJsonObject(invoiceEntity, fields, assoc, subAssoc));
        }

        paginationDto.setData(invoices);
        return paginationDto;
    }


}

