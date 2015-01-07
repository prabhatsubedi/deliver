package com.yetistep.delivr.service.impl;

import com.yetistep.delivr.abs.AbstractManager;
import com.yetistep.delivr.dao.inf.MerchantDaoService;
import com.yetistep.delivr.dao.inf.UserDaoService;
import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.dto.RequestJsonDto;
import com.yetistep.delivr.enums.Role;
import com.yetistep.delivr.enums.Status;
import com.yetistep.delivr.model.*;
import com.yetistep.delivr.service.inf.MerchantService;
import com.yetistep.delivr.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

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
        dbMerchant.setServiceFee(merchantEntity.getServiceFee());
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
                merchantEntity.setStatus(Status.UNVERIFIED);
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
                    CategoryEntity category = merchantDaoService.getCategoryById(categoryId);
                    newBrandsCategory.setCategory(category);
                    newBrandsCategory.setStoresBrand(store.getStoresBrand());
                    brandsCategories.add(newBrandsCategory);
            }

            store.getStoresBrand().setBrandsCategory(brandsCategories);
            store.getStoresBrand().setMerchant(dbMerchant);

            store.getStoresBrand().setBrandLogo(null);
            store.getStoresBrand().setBrandImage(null);

        }

        merchantDaoService.saveStore(stores);

        StoresBrandEntity brand = stores.get(0).getStoresBrand();

        if (brandLogo != null && !brandLogo.isEmpty() && brandImage != null && !brandImage.isEmpty()) {
            log.info("Uploading brand logo and image to S3 Bucket ");
            String dir = MessageBundle.separateString("/", "Merchant_"+dbMerchant.getId(), "Brand_"+ brand.getId());
            boolean isLocal = MessageBundle.isLocalHost();
            String brandLogoName = "brand_logo" + (isLocal ? "_tmp_" : "_") + brand.getId();
            String brandImageName = "brand_image" + (isLocal ? "_tmp_" : "_") + brand.getId();
            String s3PathLogo = GeneralUtil.saveImageToBucket(brandLogo, brandLogoName, dir, true);
            String s3PathImage = GeneralUtil.saveImageToBucket(brandImage, brandImageName, dir, true);
            brand.setBrandLogo(s3PathLogo);
            brand.setBrandImage(s3PathImage);
            merchantDaoService.updateStoresBrand(brand);
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
            merchantEntity.setStatus(Status.UNVERIFIED);
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


    @Override
    public List<StoresBrandEntity> findBrandList(HeaderDto headerDto) throws Exception {
        MerchantEntity dbMerchant = merchantDaoService.find(headerDto.getMerchantId());
        if(dbMerchant == null)
            throw new YSException("VLD011");
        List<StoresBrandEntity> storesBrands = merchantDaoService.findBrandListByMerchant(dbMerchant.getId());
        for(StoresBrandEntity storesBrand: storesBrands){
           storesBrand.setStore(merchantDaoService.findStoreByBrand(storesBrand.getId()));
        }
        return storesBrands;
    }

    @Override
    public StoresBrandEntity findBrandDetail(HeaderDto headerDto) throws Exception {
        return merchantDaoService.findBrandDetail(Integer.parseInt(headerDto.getId()));
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

        CategoryEntity category;
        //save stores category
        if(itemCategories.size()>0){
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
            StoreEntity store = merchantDaoService.getStoreById(itemsStore);
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
        for (String image: itemsImages){
            images.add(image);
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
                String itemImageUrl = "itemsImage"+ i + (isLocal ? "_tmp_" : "_") + item.getId();
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
                newCategories =  ordered_menu(allCategories, brandsCategory.getCategory().getId(), brandsCategory.getStoresBrand().getId());
                newCategory.setChild(null);
                newCategory.setChild(newCategories);
                categories.add(newCategory);
            }

        return categories;

    }

    public List<CategoryEntity> ordered_menu(List<CategoryEntity> categories, Integer parent_id, Integer storeId){
          List<CategoryEntity> newCategories = new ArrayList<CategoryEntity>();
          for(CategoryEntity newCategory:  categories)
          {
            if(newCategory.getParent().getId()==parent_id && (newCategory.getStoresBrand()== null || newCategory.getStoresBrand().getId() == storeId ))
            {
                newCategory.setChild(ordered_menu(categories, newCategory.getId(), storeId));
                newCategory.setItem(null);
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
            newCategories =  ordered_menu(categories, parentId, storeId);
        }
        return  newCategories;
    }

    @Override
    public List<ItemEntity> findCategoriesItems(RequestJsonDto requestJson) throws Exception {
        Integer categoryId = requestJson.getParentCategoryId();
        Integer brandId = requestJson.getCategoryStoreId();
        return merchantDaoService.getCategoriesItems(categoryId, brandId);
    }

    @Override
    public ItemEntity getItemDetail(HeaderDto headerDto) throws Exception {
        return merchantDaoService.getItemDetail(Integer.parseInt(headerDto.getId()));
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

        List<CategoryEntity> childCategories =  merchantDaoService.findChildCategories(parentId, storeId);



        List<CategoryEntity> finalCategories =  merchantDaoService.findFinalCategoryList(storeId);

       for (CategoryEntity finalCategory: finalCategories){

           CategoryEntity finalCat = finalCategory;

           if(!childCategories.contains(finalCat.getId())){
               while (finalCategory.getParent() != null && !childCategories.contains(finalCategory.getParent())){
                   finalCategory = finalCategory.getParent();
               }

               if(childCategories.contains(finalCategory.getParent())) {
                   List<CategoryEntity> parentsChild = finalCategory.getParent().getChild();
                   parentsChild.add(finalCat);
                   finalCategory.getParent().setChild(parentsChild);
               }
           }

       }

        for (CategoryEntity childCategory: childCategories){
            List<Integer> childsChildId = new ArrayList<Integer>();
            if(childCategory.getChild() != null && childCategory.getChild().size() >0 ){
                for(CategoryEntity childsChildCategory: childCategory.getChild()){
                    childsChildId.add(childsChildCategory.getId());
                }
            }

            if(childsChildId.size() > 0){
                List<ItemEntity> categoriesItems = merchantDaoService.findItemByCategory(childsChildId);
                for(ItemEntity item: categoriesItems){
                    item.setCategory(null);
                    item.setStoresBrand(null);
                }
                childCategory.setItem(categoriesItems);
            }else{
                List<ItemEntity> categoriesItems = merchantDaoService.getCategoriesItems(childCategory.getId(), childCategory.getStoresBrand().getId());
                for(ItemEntity item: categoriesItems){
                    item.setCategory(null);
                    item.setStoresBrand(null);
                }
                childCategory.setItem(categoriesItems);
            }
            childCategory.setChild(null);
            childCategory.setParent(null);
        }

        return  childCategories;
    }
}
