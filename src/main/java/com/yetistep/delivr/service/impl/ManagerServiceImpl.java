package com.yetistep.delivr.service.impl;

import com.yetistep.delivr.abs.AbstractManager;
import com.yetistep.delivr.dao.inf.*;
import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.dto.PaginationDto;
import com.yetistep.delivr.dto.RequestJsonDto;
import com.yetistep.delivr.enums.Role;
import com.yetistep.delivr.enums.Status;
import com.yetistep.delivr.model.*;
import com.yetistep.delivr.service.inf.ManagerService;
import com.yetistep.delivr.service.inf.MerchantService;
import com.yetistep.delivr.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 11/25/14
 * Time: 10:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class ManagerServiceImpl extends AbstractManager implements ManagerService {
    private static final Logger log = Logger.getLogger(ManagerServiceImpl.class);
    @Autowired
    ActionLogDaoService actionLogDaoService;

    @Autowired
    DeliveryBoyDaoService deliveryBoyDaoService;

    @Autowired
    StoresBrandDaoService storesBrandDaoService;

    @Autowired
    CategoryDaoService categoryDaoService;

    @Autowired
    MerchantService merchantService;

    @Autowired
    MerchantDaoService merchantDaoService;

    @Autowired
    UserDaoService userDaoService;


    @Override
    public void saveManagerOrAccountant(UserEntity user, HeaderDto headerDto) throws Exception {
        log.info("++++++++++++++++++ Creating User +++++++++++++++++");
        user.setUsername(headerDto.getUsername());
        if ((user.getUsername() == null) || (user.getUsername().isEmpty()))
            throw new YSException("VLD009");


        if(user.getEmailAddress() != null && !user.getEmailAddress().isEmpty()){
            if(userDaoService.checkIfEmailExists(user.getEmailAddress(), user.getRole().getRole().toInt())){
                throw new YSException("VLD026");
            }
        }
        if(userDaoService.checkIfMobileNumberExists(user.getUsername())){
            throw new YSException("VLD027");
        }

        String code = MessageBundle.generateTokenString() + "_" + System.currentTimeMillis();

        RoleEntity   userRole = userDaoService.getRoleByRole(user.getRole().getRole());

        /** Setting Default values **/
        user.setRole(userRole);
        user.setBlacklistStatus(false);
        user.setMobileVerificationStatus(true);
        user.setVerificationCode(code);
        user.setSubscribeNewsletter(false);
        user.setStatus(Status.ACTIVE);
        if(user.getEmailAddress().isEmpty()){
            user.setEmailAddress(null);
        }


        String profileImage = user.getProfileImage();
        user.setProfileImage(null);

        for (AddressEntity address: user.getAddresses()){
            address.setUser(user);
        }

        userDaoService.save(user);
        if (profileImage != null && !profileImage.isEmpty()) {
            log.info("Uploading Profile Image of User to S3 Bucket ");

            String dir = MessageBundle.separateString("/", "Users", "user_" + user.getId());
            boolean isLocal = MessageBundle.isLocalHost();
            String imageName = "pimg" + (isLocal ? "_tmp_" : "_") + user.getId()+System.currentTimeMillis();
            String s3Path = GeneralUtil.saveImageToBucket(profileImage, imageName, dir, true);
            user.setProfileImage(s3Path);
            userDaoService.update(user);
        }


        //Sending Email For Merchant
        String hostName = getServerUrl();
        String url = hostName + "/assistance/create_password/" + code;
        String loginUrl = hostName + "/";
        log.info("Sending mail to " + user.getUsername() + " with new registration: " + url);

        //send email
        String body = EmailMsg.createPasswordForNewUser(url, user.getFullName(), user.getUsername(), getServerUrl());
        String subject = "";
        if(user.getRole().getRole().equals(Role.ROLE_ACCOUNTANT)){
            subject = "Delivr: You have been added as Accountant ";
        }else if(user.getRole().getRole().equals(Role.ROLE_MANAGER)) {
            subject = "Delivr: You have been added as Manager ";
        }

        sendMail(user.getEmailAddress(), body, subject);

    }


    @Override
    public Boolean updateManagerOrAccountant(UserEntity user, HeaderDto headerDto) throws Exception {
        UserEntity dbUser = userDaoService.find(user.getId());
        if (dbUser == null) {
            throw new YSException("VLD011");
        }
        dbUser.setUsername(headerDto.getUsername());
        if(headerDto.getPassword() != null)
            dbUser.setPassword(GeneralUtil.encryptPassword(headerDto.getPassword()));

        dbUser.setMobileNumber(headerDto.getUsername());
        List<AddressEntity> addressEntities =  user.getAddresses();
        for(AddressEntity addressEntity: addressEntities){
            for(AddressEntity address: dbUser.getAddresses()){
                if(address.getId().equals(addressEntity.getId())){
                    address.setStreet(addressEntity.getStreet());
                    address.setCity(addressEntity.getCity());
                    address.setState(addressEntity.getState());
                    address.setCountry(addressEntity.getCountry());
                    address.setCountryCode(addressEntity.getCountryCode());
                    address.setUser(user);
                    break;
                }
            }
        }

        dbUser.setUsername(user.getMobileNumber());
        dbUser.setFullName(user.getFullName());
        dbUser.setEmailAddress(user.getEmailAddress());
        dbUser.setMobileNumber(user.getMobileNumber());
        dbUser.setGender(user.getGender());
        dbUser.setStatus(user.getStatus());

        String profileImage = user.getProfileImage();

        userDaoService.update(dbUser);

        if (profileImage != null && !profileImage.isEmpty()) {

            log.info("Deleting Profile Image of User to S3 Bucket ");
            AmazonUtil.deleteFileFromBucket(AmazonUtil.getAmazonS3Key(dbUser.getProfileImage()));

            log.info("Uploading profile Image of User to S3 Bucket ");

            String dir = MessageBundle.separateString("/", "Users", "user_" + dbUser.getId());
            boolean isLocal = MessageBundle.isLocalHost();
            String imageName = "pimg" + (isLocal ? "_tmp_" : "_") + dbUser.getId()+System.currentTimeMillis();
            String s3Path = GeneralUtil.saveImageToBucket(profileImage, imageName, dir, true);
            dbUser.setProfileImage(s3Path);
            userDaoService.update(dbUser);
        }

        return true;
    }


    @Override
    public PaginationDto getActionLog(Page page) throws Exception {
        log.info("Retrieving list of action logs:");
        PaginationDto paginationDto = new PaginationDto();
        Integer totalRows = actionLogDaoService.getTotalNumberOfActionLogs();
        paginationDto.setNumberOfRows(totalRows);
        List<ActionLogEntity> actionLogEntities;
        if(page != null){
            page.setTotalRows(totalRows);
            actionLogEntities = actionLogDaoService.findActionLogPaginated(page);
        }else{
            actionLogEntities = actionLogDaoService.findAll();
        }
        paginationDto.setData(actionLogEntities);
        return paginationDto;
    }

    @Override
    public DeliveryBoyEntity updateDboyAccount(HeaderDto headerDto, RequestJsonDto requestJsonDto) throws Exception {

        DeliveryBoyEntity dBoy = deliveryBoyDaoService.find(Integer.parseInt(headerDto.getId()));

        /*if(dBoy.getPreviousDue().compareTo(BigDecimal.ZERO) != 0)
            throw new YSException("ERR015");*/

        dBoy.setAdvanceAmount(requestJsonDto.getAdvanceAmount());
        dBoy.setBankAmount(dBoy.getBankAmount().add(requestJsonDto.getAdvanceAmount()));
        dBoy.setAvailableAmount(dBoy.getAvailableAmount().add(requestJsonDto.getAdvanceAmount()));

        List<DBoyAdvanceAmountEntity> dBoyAdvanceAmounts = new ArrayList<DBoyAdvanceAmountEntity>();
        DBoyAdvanceAmountEntity dBoyAdvanceAmount = new DBoyAdvanceAmountEntity();
        dBoyAdvanceAmount.setAmountAdvance(requestJsonDto.getAdvanceAmount());
        dBoyAdvanceAmount.setDeliveryBoy(dBoy);
        dBoyAdvanceAmounts.add(dBoyAdvanceAmount);

        dBoy.setdBoyAdvanceAmounts(dBoyAdvanceAmounts);

        deliveryBoyDaoService.update(dBoy);

        String fields = "id,availabilityStatus,averageRating,bankAmount,walletAmount,advanceAmount,vehicleType,licenseNumber,vehicleNumber,user,latitude,longitude";

        Map<String, String> assoc = new HashMap<>();
        Map<String, String> subAssoc = new HashMap<>();

        assoc.put("user", "id,fullName,mobileNumber,emailAddress,profileImage,gender,status,addresses");
        subAssoc.put("addresses", "street,city,state,country,latitude,longitude");

        return ((DeliveryBoyEntity) ReturnJsonUtil.getJsonObject(dBoy, fields, assoc, subAssoc));
    }

    @Override
    public DeliveryBoyEntity ackSubmittedAmount(HeaderDto headerDto, RequestJsonDto requestJsonDto) throws Exception {
        DeliveryBoyEntity dBoy = deliveryBoyDaoService.find(Integer.parseInt(headerDto.getId()));

        dBoy.setPreviousDue(dBoy.getPreviousDue().subtract(requestJsonDto.getSubmittedAmount()));
        List<DBoySubmittedAmountEntity> dBoySubmittedAmounts = new ArrayList<DBoySubmittedAmountEntity>();
        DBoySubmittedAmountEntity dBoySubmittedAmount = new DBoySubmittedAmountEntity();
        dBoySubmittedAmount.setAmountReceived(requestJsonDto.getSubmittedAmount());
        dBoySubmittedAmount.setDeliveryBoy(dBoy);
        dBoySubmittedAmounts.add(dBoySubmittedAmount);
        dBoy.setdBoySubmittedAmount(dBoySubmittedAmounts);

        deliveryBoyDaoService.update(dBoy);
        String fields = "id,availabilityStatus,averageRating,bankAmount,walletAmount,advanceAmount,vehicleType,licenseNumber,vehicleNumber,user,latitude,longitude";

        Map<String, String> assoc = new HashMap<>();
        Map<String, String> subAssoc = new HashMap<>();

        assoc.put("user", "id,fullName,mobileNumber,emailAddress,profileImage,gender,status,addresses");
        subAssoc.put("addresses", "street,city,state,country,latitude,longitude");

        return ((DeliveryBoyEntity) ReturnJsonUtil.getJsonObject(dBoy, fields, assoc, subAssoc));
    }

    @Override
    public DeliveryBoyEntity walletSubmittedAmount(HeaderDto headerDto, RequestJsonDto requestJsonDto) throws Exception {
        DeliveryBoyEntity dBoy = deliveryBoyDaoService.find(Integer.parseInt(headerDto.getId()));

        dBoy.setWalletAmount(dBoy.getWalletAmount().subtract(requestJsonDto.getSubmittedAmount()));

        List<DBoySubmittedAmountEntity> dBoySubmittedAmounts = new ArrayList<DBoySubmittedAmountEntity>();
        DBoySubmittedAmountEntity dBoySubmittedAmount = new DBoySubmittedAmountEntity();

        dBoySubmittedAmount.setAmountReceived(requestJsonDto.getSubmittedAmount());
        dBoySubmittedAmount.setDeliveryBoy(dBoy);
        dBoySubmittedAmounts.add(dBoySubmittedAmount);
        dBoy.setdBoySubmittedAmount(dBoySubmittedAmounts);

        deliveryBoyDaoService.update(dBoy);

        String fields = "id,availabilityStatus,averageRating,bankAmount,walletAmount,advanceAmount,vehicleType,licenseNumber,vehicleNumber,user,latitude,longitude";

        Map<String, String> assoc = new HashMap<>();
        Map<String, String> subAssoc = new HashMap<>();

        assoc.put("user", "id,fullName,mobileNumber,emailAddress,profileImage,gender,status,addresses");
        subAssoc.put("addresses", "street,city,state,country,latitude,longitude");

        return ((DeliveryBoyEntity) ReturnJsonUtil.getJsonObject(dBoy, fields, assoc, subAssoc));
    }

    @Override
    public List<StoresBrandEntity> findFeaturedAndPrioritizedStoreBrands() throws Exception {
        return storesBrandDaoService.findFeaturedAndPriorityBrands();
    }

    @Override
    public PaginationDto findNonFeaturedAndPrioritizedStoreBrands(Page page) throws Exception {
        log.info("Retrieving list of Non Featured & prioritized store brands");
        PaginationDto paginationDto = new PaginationDto();
        Integer totalRows = storesBrandDaoService.getTotalNumberOfStoreBrands();
        paginationDto.setNumberOfRows(totalRows);
        List<StoresBrandEntity> storesBrandEntities;
        if(page != null){
            page.setTotalRows(totalRows);
        }
        storesBrandEntities = storesBrandDaoService.findExceptFeaturedAndPriorityBrands(page);
        paginationDto.setData(storesBrandEntities);
        return paginationDto;
    }

    @Override
    public PaginationDto findInactiveStoreBrands(Page page) throws Exception {
        log.info("Retrieving list of Inactive store brands");
        PaginationDto paginationDto = new PaginationDto();
        Integer totalRows = storesBrandDaoService.getTotalNumberOfInactiveStoreBrands();
        paginationDto.setNumberOfRows(totalRows);
        List<StoresBrandEntity> storesBrandEntities;
        if(page != null){
            page.setTotalRows(totalRows);
        }
        storesBrandEntities = storesBrandDaoService.findInactiveStoreBrands(page);
        paginationDto.setData(storesBrandEntities);
        return paginationDto;
    }


    @Override
    public Boolean updateFeatureAndPriorityOfStoreBrands(List<StoresBrandEntity> storesBrands) throws Exception {
        checkDuplicatePriorities(storesBrands);
        return storesBrandDaoService.updateFeatureAndPriorityOfStoreBrands(storesBrands);
    }

    /**
     * checks duplicate priority as well as checks both featured and prioritized restrictions
     */
    private Boolean checkDuplicatePriorities(List<StoresBrandEntity> storesBrands) throws Exception{
        Set<Integer> priorities = new HashSet<Integer>();
        for(StoresBrandEntity storesBrand: storesBrands){
            if(storesBrand.getPriority() != null){
                if(storesBrand.getFeatured() != null && storesBrand.getFeatured())
                    throw new YSException("VLD019");
                if(!priorities.add(storesBrand.getPriority()))
                    throw new YSException("VLD018");
            }
        }
        return true;
    }


    @Override
    public Object saveCategory(CategoryEntity category, HeaderDto headerDto) throws Exception{
        log.info("****************************saving category **************************");
        if (headerDto.getId() != null && !headerDto.getId().equals("") ){
            CategoryEntity parentCategory = new CategoryEntity();
            parentCategory.setId(Integer.parseInt(headerDto.getId()));
            category.setParent(parentCategory);

            List<ItemEntity> items = merchantDaoService.getCategoriesItems(Integer.parseInt(headerDto.getId()));

            if(items.size() > 0){
                throw new YSException("VLD022");
            }

        }

        String categoryImage = category.getImageUrl();
        category.setImageUrl(null);
        if (category.getFeatured() == null) {
            category.setFeatured(false);
        }

        categoryDaoService.save(category);

        if(categoryImage != null){
            log.info("Uploading category image to S3 Bucket ");
            String dir = MessageBundle.separateString("/", "categories");
            boolean isLocal = MessageBundle.isLocalHost();
            String categoryImageUrl = "category"+(isLocal ? "_tmp_" : "_") + category.getId()+System.currentTimeMillis();
            String s3PathImage = GeneralUtil.saveImageToBucket(categoryImage, categoryImageUrl, dir, true);
            category.setImageUrl(s3PathImage);
            categoryDaoService.update(category);
        }
        String fields = "id,name,imageUrl";
        return ReturnJsonUtil.getJsonObject(category, fields);
    }

    @Override
    public Object updateCategory(CategoryEntity category, HeaderDto headerDto) throws Exception{
        log.info("****************************updating category "+headerDto.getId()+"**************************");
        CategoryEntity dbCategory = categoryDaoService.find(Integer.parseInt(headerDto.getId()));

        String categoryImage = category.getImageUrl();
        String dbImageUrl = dbCategory.getImageUrl();

        if(categoryImage != null){
            dbCategory.setImageUrl(null);
        }

        dbCategory.setName(category.getName());
        if(category.getParent() != null){
            CategoryEntity parentCategory = new CategoryEntity();
            parentCategory.setId(category.getParent().getId());
            dbCategory.setParent(parentCategory);
        }

        categoryDaoService.update(dbCategory);

        if(categoryImage != null){
            log.info("Uploading category image to S3 Bucket ");
            String dir = MessageBundle.separateString("/", "categories");
            boolean isLocal = MessageBundle.isLocalHost();

            if(dbImageUrl != null){
                log.info("deleting category image from S3 Bucket ");
                AmazonUtil.deleteFileFromBucket(AmazonUtil.getAmazonS3Key(dbImageUrl));
            }

            String categoryImageUrl = "category"+(isLocal ? "_tmp_" : "_") + dbCategory.getId()+System.currentTimeMillis();
            String s3PathImage = GeneralUtil.saveImageToBucket(categoryImage, categoryImageUrl, dir, true);
            dbCategory.setImageUrl(s3PathImage);
            categoryDaoService.update(dbCategory);
        }

        String fields = "id,parent,name,imageUrl";

        Map<String, String> assoc = new HashMap<>();
        assoc.put("parent", "name");

        return ReturnJsonUtil.getJsonObject(dbCategory, fields, assoc);
    }


    @Override
    public UserEntity findUserById(HeaderDto headerDto) throws Exception {
        Integer id = Integer.parseInt(headerDto.getId());
        log.info("Retrieving Deliver Boy With ID:" + id);
        UserEntity userEntity = userDaoService.find(id);
        if (userEntity == null) {
            throw new YSException("VLD011");
        }


        String fields = "id,fullName,mobileNumber,emailAddress,profileImage,gender,status,addresses";

        Map<String, String> assoc = new HashMap<>();

        assoc.put("addresses", "id,latitude,longitude,street,city,state,country");


        UserEntity user = ((UserEntity) ReturnJsonUtil.getJsonObject(userEntity, fields, assoc));

        return user;
    }

    @Override
    public List<UserEntity> findAllManagers() throws Exception {
        log.info("Retrieving list of Deliver Boys");
        List<UserEntity> userEntities = userDaoService.findManagers();
        /*For filtering role -- set to null as all delivery boy has same role*/

        List<UserEntity> users = new ArrayList<>();

        String fields = "id,fullName,mobileNumber,emailAddress,status,addresses";

        Map<String, String> assoc = new HashMap<>();

        assoc.put("addresses", "id,latitude,longitude,street,city,state,country");


        for (UserEntity userEntity:userEntities){
            UserEntity user = (UserEntity) ReturnJsonUtil.getJsonObject(userEntity, fields, assoc);
            users.add(user);
        }


        return users;
    }

    @Override
    public List<UserEntity> findAllAccountants() throws Exception {
        log.info("Retrieving list of Deliver Boys");
        List<UserEntity> userEntities = userDaoService.findAccountants();
        /*For filtering role -- set to null as all delivery boy has same role*/

        List<UserEntity> users = new ArrayList<>();

        String fields = "id,fullName,mobileNumber,emailAddress,status,addresses";

        Map<String, String> assoc = new HashMap<>();

        assoc.put("addresses", "id,latitude,longitude,street,city,state,country");


        for (UserEntity userEntity:userEntities){
            UserEntity user = (UserEntity) ReturnJsonUtil.getJsonObject(userEntity, fields, assoc);
            users.add(user);
        }


        return users;
    }


    public List<CategoryEntity> getCategoryTree(List<CategoryEntity> categories, Integer parent_id){
        List<CategoryEntity> newCategories = new ArrayList<CategoryEntity>();
        for(CategoryEntity newCategory:  categories)
        {
            if(newCategory.getParent().getId().equals(parent_id) && (newCategory.getStoresBrand()== null))
            {
                newCategory.setChild(getCategoryTree(categories, newCategory.getId()));
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


    public List<CategoryEntity> findChildCategories(List<CategoryEntity> parentCategories) throws Exception {
        List<CategoryEntity> defaultCategories = merchantDaoService.getDefaultCategories();

        if(defaultCategories.size() > 0){
            for (CategoryEntity category: parentCategories){
               List<CategoryEntity> newCategories = new ArrayList<>();
               newCategories =  getCategoryTree(defaultCategories, category.getId());
               category.setChild(newCategories);
            }
        }

        return  parentCategories;
    }

    @Override
    public List<CategoryEntity> getDefaultCategories() throws Exception {
        List<CategoryEntity> parentCategories = merchantService.getParentCategories();
        List<CategoryEntity> defaultCategories =  findChildCategories(parentCategories);

        List<CategoryEntity> categories = new ArrayList<>();

        String fields = "id,name,child";

        Map<String, String> assoc = new HashMap<>();
        Map<String, String> subAssoc = new HashMap<>();

        assoc.put("child", "id,name,child");

        subAssoc.put("child", "id,name,child");

        for (CategoryEntity category:defaultCategories){
            categories.add((CategoryEntity) ReturnJsonUtil.getJsonObject(category, fields, assoc, subAssoc));
        }

        return categories;
    }

    @Override
    public CategoryEntity getCategory(HeaderDto headerDto) throws Exception{
        CategoryEntity category = categoryDaoService.find(Integer.parseInt(headerDto.getId()));
       /* if(category != null){
            category.setChild(null);
            category.setItem(null);
            category.setStoresBrand(null);
        }*/
        String fields = "id,name,parent,imageUrl";
        Map<String, String> assoc = new HashMap<>();
        assoc.put("parent", "id,name");

        return (CategoryEntity) ReturnJsonUtil.getJsonObject(category, fields, assoc);
    }

    @Override
    public Boolean changeOrderSettlement(HeaderDto headerDto) throws Exception {
        return true;
    }
}
