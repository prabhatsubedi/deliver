package com.yetistep.delivr.service.impl;

import com.yetistep.delivr.abs.AbstractManager;
import com.yetistep.delivr.dao.inf.MerchantDaoService;
import com.yetistep.delivr.dao.inf.UserDaoService;
import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.dto.PaginationDto;
import com.yetistep.delivr.dto.RequestJsonDto;
import com.yetistep.delivr.enums.Role;
import com.yetistep.delivr.enums.Status;
import com.yetistep.delivr.model.*;
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
    HttpServletRequest httpServletRequest;

    @Override
    public void saveMerchant(MerchantEntity merchant, HeaderDto headerDto) throws Exception {
        log.info("++++++++++++++++++ Creating Merchant +++++++++++++++++");
        UserEntity user = merchant.getUser();


        String code = MessageBundle.generateTokenString() + "_" + System.currentTimeMillis();

        user.setUsername(headerDto.getUsername());
        user.setPassword("");
        user.setVerificationCode(code);
        user.setMobileVerificationStatus(false);
        user.setBlacklistStatus(false);
        user.setVerifiedStatus(false);
        user.setSubscribeNewsletter(false);
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
            merchant.getUser().setLastActivityDate(DateUtil.getCurrentTimestampSQL());
            merchantDaoService.update(merchant);
        }

        //Sending Email For Merchant
        String hostName = getServerUrl();
        String url = hostName + "/assistance/create_password/" + code;
        String loginUrl = hostName + "/";
        log.info("Sending mail to " + user.getUsername() + " with new registration: " + url);

        //send email
        String body = EmailMsg.createPasswordForNewUser(url, user.getFullName(), user.getUsername(), " You have been added as Merchant", getServerUrl());
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
        dbMerchant.setServiceFee(merchantEntity.getServiceFee());
        merchantDaoService.update(dbMerchant);

        //Sending Email For Merchant
        String url = getServerUrl();
        String body = EmailMsg.activateMerchant(url, user.getFullName(), " Your account has been activated", getServerUrl());
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
        }
        return merchantEntities;

    }


    @Override
    public void saveStore(RequestJsonDto requestJson, HeaderDto headerDto ) throws Exception {
        log.info("++++++++++++ Saving Store " + requestJson.getStores().size() + " +++++++++++++++");

        List<StoreEntity> stores = requestJson.getStores();
        StoresBrandEntity storesBrand = requestJson.getStoresBrand();
        List<Integer> categories = requestJson.getCategories();

        MerchantEntity dbMerchant = merchantDaoService.find(headerDto.getMerchantId());
        if(dbMerchant == null)
            throw new YSException("VLD011");

        String brandLogo = storesBrand.getBrandLogo();
        String brandImage = storesBrand.getBrandImage();

        for (StoreEntity store: stores){

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

        }
        storesBrand.setStore(stores);

        merchantDaoService.saveStoresBrand(storesBrand);

        StoresBrandEntity brand = stores.get(0).getStoresBrand();

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

        //dbStoresBrand.setBrandName(storesBrand.getBrandName());
        dbStoresBrand.setBrandUrl(storesBrand.getBrandUrl());
        dbStoresBrand.setMinOrderAmount(storesBrand.getMinOrderAmount());
        dbStoresBrand.setOpeningTime(storesBrand.getOpeningTime());
        dbStoresBrand.setClosingTime(storesBrand.getClosingTime());
        dbStoresBrand.setOpenStatus(storesBrand.getOpenStatus());


        Map<Integer, StoreEntity> storeWithId = new HashMap<Integer, StoreEntity>();
        Map<Integer, StoreEntity> dbStoreWithId = new HashMap<Integer, StoreEntity>();

        List<BrandsCategoryEntity> dbBrandsCategories = dbStoresBrand.getBrandsCategory();
        Map<Integer, Integer> brandCategoriesIdList = new HashMap<Integer, Integer>();
        //Map<Integer, BrandsCategoryEntity> brandCategoriesMap= new HashMap<Integer, BrandsCategoryEntity>();
        for (BrandsCategoryEntity brandsCategory: dbBrandsCategories){
            //if (categories.contains(brandsCategory.getCategory().getId()) && brandsCategory.getStoresBrand().getId() == storesBrand.getId()) {
                brandCategoriesIdList.put(brandsCategory.getCategory().getId(), brandsCategory.getId());
           // }
            //brandCategoriesMap.put(brandsCategory.getId(), brandsCategory);
        }

        /*for (BrandsCategoryEntity dbCategory: dbBrandsCategories){
            if(!categories.contains(dbCategory.getCategory().getId())){
                dbBrandsCategories.remove(dbCategory);
            }
        }*/
       /* Iterator it = brandCategoriesIdList.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
           if(!categories.contains(pairs.getKey())){
               dbBrandsCategories.remove(brandCategoriesMap.get(pairs.getValue()));
           }
        }*/

        //List<BrandsCategoryEntity> brandsCategories = new ArrayList<BrandsCategoryEntity>();
        for (Integer categoryId: categories){
            if(brandCategoriesIdList.get(categoryId) == null) {
                BrandsCategoryEntity newBrandsCategory = new BrandsCategoryEntity();
                //newBrandsCategory.setId(null);
                CategoryEntity category = new CategoryEntity();
                category.setId(categoryId);
                newBrandsCategory.setCategory(category);
                newBrandsCategory.setStoresBrand(dbStoresBrand);
                dbBrandsCategories.add(newBrandsCategory);
            }
        }

        dbStoresBrand.setBrandsCategory(dbBrandsCategories);
        //dbStoresBrand.setBrandLogo(null);
        //dbStoresBrand.setBrandImage(null);

        List<StoreEntity> dbStores = dbStoresBrand.getStore();
        List<Integer> storeIdList = new ArrayList<Integer>();

        for (StoreEntity store: stores){
            if(store.getId() == null){
                dbStores.add(store);
            }
            storeIdList.add(store.getId());
            store.setStoresBrand(dbStoresBrand);
            storeWithId.put(store.getId(), store);
        }

        for (StoreEntity dbStore:dbStores){
            if(dbStore.getId() != null)
                dbStoreWithId.put(dbStore.getId(), dbStore);
        }

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
        if(merchantEntity.getUser().getPassword().isEmpty()){
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
        }
        merchantEntity.getUser().setRole(null);
        return merchantEntity;
    }

    @Override
    public Boolean updateMerchant(MerchantEntity merchantEntity, HeaderDto headerDto) throws Exception {
        log.info("Updating merchant with ID:"+merchantEntity.getId());
        Integer merchant_id;
        if(merchantEntity.getId() == null){
            merchant_id = headerDto.getMerchantId();
        }else{
            merchant_id  = merchantEntity.getId();
        }

        MerchantEntity merchant = merchantDaoService.find(merchant_id);
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
        //merchant values
        merchant.setWebsite(merchantEntity.getWebsite());
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

        merchant.getUser().setLastActivityDate(DateUtil.getCurrentTimestampSQL());
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
            merchant.getUser().setLastActivityDate(DateUtil.getCurrentTimestampSQL());
            merchantDaoService.update(merchant);
        }
        return true;
    }

    @Override
    public List<CategoryEntity> getParentCategories() throws Exception {
        List<CategoryEntity> categories = merchantDaoService.findParentCategories();
        return categories;
    }


    @Override
    public List<StoresBrandEntity> findBrandList(HeaderDto headerDto) throws Exception {
        MerchantEntity dbMerchant = merchantDaoService.find(headerDto.getMerchantId());
        if(dbMerchant == null)
            throw new YSException("VLD011");
        List<StoresBrandEntity> storesBrands = merchantDaoService.findBrandListByMerchant(dbMerchant.getId());
        List<StoresBrandEntity> storesBrandEntities = new ArrayList<StoresBrandEntity>();
        for(StoresBrandEntity storesBrand: storesBrands){
           storesBrandEntities.add(getStoreBrandForJson(storesBrand));
        }
        return storesBrandEntities;
    }

    @Override
    public List<StoresBrandEntity> findBrands() throws Exception {

        List<StoresBrandEntity> storesBrands = merchantDaoService.findBrandList();

        return storesBrands;
    }



    @Override
    public StoresBrandEntity findBrandDetail(HeaderDto headerDto) throws Exception {
        StoresBrandEntity storesBrandEntity =  merchantDaoService.findBrandDetail(Integer.parseInt(headerDto.getId()));
        return getStoreBrandForJson(storesBrandEntity);
    }

    private StoresBrandEntity getStoreBrandForJson(StoresBrandEntity storesBrandEntity){
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

    @Override
    public void saveItem(RequestJsonDto requestJson, HeaderDto headerDto) throws Exception {
        log.info("++++++++++++ Saving Item +++++++++++++++");

        ItemEntity item = requestJson.getItem();
        List<CategoryEntity> itemCategories = requestJson.getItemCategories();
        List<Integer> itemStores = requestJson.getItemStores();
        List<ItemsAttributesTypeEntity> itemsAttributesTypes = requestJson.getItemAttributesTypes();
        List<String> itemsImages = requestJson.getItemImages();

        StoresBrandEntity storesBrand = merchantDaoService.findBrandDetail(Integer.parseInt(headerDto.getId()));
        if(storesBrand == null)
            throw new YSException("VLD012");

        CategoryEntity category = new CategoryEntity();
        //save items category
        if(itemCategories.size()>1){
            CategoryEntity parentCategory = merchantDaoService.getCategoryById(itemCategories.get(0).getId());
            itemCategories.set(0, parentCategory);
            int i;
               for(i = 1; i<itemCategories.size(); i++){
                   itemCategories.get(i).setParent(itemCategories.get(i-1));
                   itemCategories.get(i).setFeatured(false);
                   itemCategories.get(i).setStoresBrand(storesBrand);
               }
            merchantDaoService.saveCategories(itemCategories);
            //get items category
            category = itemCategories.get(itemCategories.size()-1);
        }else{
            category = merchantDaoService.getCategoryById(itemCategories.get(0).getId());
        }

        item.setCategory(category);
        item.setStoresBrand(storesBrand);

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

        List<String> images = new ArrayList<>();
        if(itemsImages != null){
            for (String image: itemsImages){
                images.add(image);
            }
        }

        merchantDaoService.saveItem(item);

        if (images != null && images.size()>0) {
            log.info("Uploading item images to S3 Bucket ");

            String dir = MessageBundle.separateString("/", "Merchant_"+storesBrand.getMerchant().getId(), "Brand_"+ storesBrand.getId(), "item" + item.getId());
            boolean isLocal = MessageBundle.isLocalHost();

            List<ItemsImageEntity> itemsImageEntities = new ArrayList<>();
            int i = 0;
            for(String image: images){
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
        log.info("++++++++++++ Saving Item +++++++++++++++");

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
        dbItem.setUnitPrice(item.getUnitPrice());


        StoresBrandEntity storesBrand = dbItem.getStoresBrand();
        CategoryEntity category = new CategoryEntity();
        //update items category
        if(itemCategories.size()>1){
            //get items category
            category = itemCategories.get(itemCategories.size()-1);
            int i;
            for(i = 1; i<itemCategories.size(); i++){
                itemCategories.get(i).setParent(itemCategories.get(i-1));
                itemCategories.get(i).setFeatured(false);
                itemCategories.get(i).setStoresBrand(storesBrand);
            }
            //save categories
            itemCategories.remove(itemCategories.get(0));
            merchantDaoService.saveCategories(itemCategories);
        }else{
            category = itemCategories.get(0);
        }

        dbItem.setCategory(category);

        List<ItemsStoreEntity> dbItemStoreEntities = dbItem.getItemsStores();
        List<ItemsAttributesTypeEntity> dbItemsAttributesTypes = dbItem.getAttributesTypes();
        List<Integer> dbItemsAttributesTypeIdList = new ArrayList<Integer>();

        List<Integer> dbItemsStoreIdList = new ArrayList<Integer>();
        Map<Integer, ItemsAttributesTypeEntity> dbItemAttributesTypesMap = new HashMap<Integer, ItemsAttributesTypeEntity>();
        Map<Integer, ItemsAttributeEntity> dbItemAttributesMap = new HashMap<Integer, ItemsAttributeEntity>();

        for (ItemsStoreEntity itemsStore: dbItemStoreEntities){
            dbItemsStoreIdList.add(itemsStore.getId());
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


        /*//add new images
        List<Integer> itemsImagesIdList = new ArrayList<Integer>();
        Map<Integer, ItemsImageEntity> itemsImagesIdMap = new HashMap<>();
        if(itemsImages != null){
            for (ItemsImageEntity image: itemsImages){
                if(image.getId() == null){
                    dbImages.add(image);
                }   else {
                    itemsImagesIdMap.put(image.getId(), image);
                }
                itemsImagesIdList.add(image.getId());
            }

            //remove old images
            Iterator it = dbItemsImagesIdMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry)it.next();
                if(!itemsImagesIdList.contains(pairs.getKey())){
                    dbImages.remove(dbItemsImagesIdMap.get(pairs.getValue()));
                }
            }
        }*/





        dbItem.setModifiedDate(DateUtil.getCurrentTimestampSQL());
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
            if(dbImages.size() > 0) {
                int i = 0;
                for(ItemsImageEntity image: dbImages){
                    image.setItem(dbItem);
                    if(image.getId() != null && !image.getUrl().contains("https://")) { //has url  and is updated
                        //if(image.getId() != null){     //not new
                            if(image.getUrl() != null && dbItemsImagesUrlMap.get(image.getId()).contains("https://")){
                                AmazonUtil.deleteFileFromBucket(AmazonUtil.getAmazonS3Key(dbItemsImagesUrlMap.get(image.getId())));
                            }

                            String itemImageUrl = "itemsImage"+ i + (isLocal ? "_tmp_" : "_") + item.getId()+System.currentTimeMillis();
                            String s3PathImage = GeneralUtil.saveImageToBucket(image.getUrl(), itemImageUrl, dir, true);
                            image.setUrl(s3PathImage);
                            image.setItem(dbItem);
                            i++;

                        }else if(image.getId() == null){
                            String itemImageUrl = "itemsImage"+ i + (isLocal ? "_tmp_" : "_") + item.getId()+System.currentTimeMillis();
                            String s3PathImage = GeneralUtil.saveImageToBucket(image.getUrl(), itemImageUrl, dir, true);
                            image.setUrl(s3PathImage);
                            image.setItem(dbItem);
                            i++;
                        }
                }
                merchantDaoService.updateItemImages(dbImages);
            }
        }

    }

    @Override
    public List<StoreEntity> findStoresByBrand(HeaderDto headerDto) throws Exception {
        return merchantDaoService.findStoreByBrand(Integer.parseInt(headerDto.getId()));
    }

    @Override
    public List<CategoryEntity> findCategoriesByBrand(HeaderDto headerDto) throws Exception {
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
                if(items.size() > 0){
                    newCategory.setItem(new ArrayList<ItemEntity>());
                } else {
                    newCategory.setItem(null);
                }
                newCategory.setChild(null);
                newCategory.setChild(newCategories);
                categories.add(newCategory);
            }

        return categories;

    }

    public List<CategoryEntity> getCategoryTree(List<CategoryEntity> categories, Integer parent_id, Integer storeId){
          List<CategoryEntity> newCategories = new ArrayList<CategoryEntity>();
          for(CategoryEntity newCategory:  categories)
          {
            if(newCategory.getParent().getId()==parent_id && (newCategory.getStoresBrand()== null || newCategory.getStoresBrand().getId() == storeId ))
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
        Integer parentId = requestJson.getParentCategoryId();
        Integer storeId = requestJson.getCategoryStoreId();
        List<CategoryEntity> categories;
        List<CategoryEntity> newCategories = new ArrayList<CategoryEntity>();

        categories = merchantDaoService.getCategories();

        if(categories.size() > 0){
            newCategories =  getCategoryTree(categories, parentId, storeId);
        }
        return  newCategories;
    }

    @Override
    public PaginationDto findCategoriesItems(RequestJsonDto requestJson) throws Exception {
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
           if(items.size()>0){
               for (ItemEntity item: items){
                   item.getCategory().setItem(null);
                   item.setItemsStores(null);
                   item.setStoresBrand(null);
                   item.setCarts(null);
                   item.setItemsOrder(null);
               }
           }
        paginationDto.setData(items);
        return paginationDto;
    }

    @Override
    public ItemEntity getItemDetail(HeaderDto headerDto) throws Exception {
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
        return item;
    }

    @Override
    public List<ItemEntity> findStoresItems(HeaderDto headerDto) throws Exception {
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
        Integer parentId = requestJson.getParentCategoryId();
        Integer storeId = requestJson.getCategoryStoreId();
        Integer itemCount = requestJson.getParentCategoriesItemsCount();
        if (itemCount == null){
            itemCount = 4;
        }

        List<CategoryEntity> childCategories =  merchantDaoService.findChildCategories(parentId, storeId);
        List<CategoryEntity> finalCategories =  merchantDaoService.findFinalCategoryList(storeId);


        //set child of each childCategory empty so that final categories can be added as the childs of the child category
        for (CategoryEntity childCategory: childCategories){
            childCategory.setChild(new ArrayList<CategoryEntity>());
        }

        //add final categories as the childs of the child category
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
                //fixme : get the dao contact out out the loop
                List<ItemEntity> categoriesItems = merchantDaoService.findItemByCategory(childsChildId, storeId, itemCount);
                if(categoriesItems.size() > 0){
                    for(ItemEntity item: categoriesItems){
                        item.setCategory(null);
                        item.setStoresBrand(null);
                        item.setItemsStores(null);
                    }
                }
                childCategory.setItem(categoriesItems);
            }else{
                List<ItemEntity> categoriesItems = merchantDaoService.getCategoriesItems(childCategory.getId(), storeId, itemCount);
                if(categoriesItems.size() > 0){
                    for(ItemEntity item: categoriesItems){
                        item.setCategory(null);
                        item.setStoresBrand(null);
                        item.setItemsStores(null);
                    }
                }
                childCategory.setItem(categoriesItems);
            }



            childCategory.setChild(null);
            childCategory.setParent(null);
        }

        return  childCategories;
    }

    @Override
    public List<CategoryEntity> findParentCategoriesByBrand(HeaderDto headerDto) throws Exception {
        List<BrandsCategoryEntity> brandsCategories =  merchantDaoService.getBrandsCategory(Integer.parseInt(headerDto.getId()));
        List<CategoryEntity> categories = new ArrayList<>();

        for (BrandsCategoryEntity brandsCategory: brandsCategories){
            brandsCategory.getCategory().setChild(null);
            categories.add(brandsCategory.getCategory());
        }

        return categories;

    }


    @Override
    public boolean changeStatus(RequestJsonDto requestJsonDto, HeaderDto headerDto) throws Exception{
        String type = requestJsonDto.getClassName();
        Integer statusId = requestJsonDto.getStatusId();
        if(type.equals("User")){
            UserEntity user =   userDaoService.find(Integer.parseInt(headerDto.getId()));
            if(statusId==2){
                user.setStatus(Status.ACTIVE);
            }else if(statusId==3){
                user.setStatus(Status.INACTIVE);
            }
            user.setLastActivityDate(DateUtil.getCurrentTimestampSQL());
            userDaoService.update(user);
        }else if(type.equals("Brand")) {
            StoresBrandEntity storesBrand =   merchantDaoService.findBrandDetail(Integer.parseInt(headerDto.getId()));
            if(statusId==2){
                storesBrand.setStatus(Status.ACTIVE);
            }else if(statusId==3){
                storesBrand.setStatus(Status.INACTIVE);
            }
            merchantDaoService.updateStoresBrand(storesBrand);
        }  else if(type.equals("Store")){
            StoreEntity store = merchantDaoService.getStoreById(Integer.parseInt(headerDto.getId()));
            List<StoreEntity> stores = new ArrayList<StoreEntity>();
            if(statusId==2){
                store.setStatus(Status.ACTIVE);
            }else if(statusId==3){
                store.setStatus(Status.INACTIVE);
            }
            stores.add(store);
            merchantDaoService.updateStores(stores);
        }  else if(type.equals("Item")){
            ItemEntity item = merchantDaoService.getItemDetail(Integer.parseInt(headerDto.getId()));
            if(statusId==2){
                item.setStatus(Status.ACTIVE);
            }else if(statusId==3){
                item.setStatus(Status.INACTIVE);
            }
            item.setModifiedDate(DateUtil.getCurrentTimestampSQL());
            merchantDaoService.updateItem(item);
        }
        return  true;
    }

    @Override
    public PaginationDto getWebItemSearch(RequestJsonDto requestJsonDto, HeaderDto headerDto) throws Exception{

        List<Integer> parentCategoryId = requestJsonDto.getCategories();
        Integer storeId = Integer.parseInt(headerDto.getId());
        String searchString = requestJsonDto.getSearchString();
        Page page = requestJsonDto.getPage();

        if(parentCategoryId == null){
            List<CategoryEntity> categories = new ArrayList<>();
            categories =  merchantDaoService.findParentCategories();
            for (CategoryEntity category: categories){
                parentCategoryId.add(category.getId());
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
        if(items.size()>0){
             for (ItemEntity item: items){
                 item.setCategory(null);
                 item.setItemsStores(null);
                 item.setStoresBrand(null);
                 item.setAttributesTypes(null);
                 item.setCarts(null);
                 item.setItemsOrder(null);
             }
        }

        paginationDto.setData(items);
        return paginationDto;
    }


}

