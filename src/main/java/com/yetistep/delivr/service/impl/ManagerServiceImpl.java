package com.yetistep.delivr.service.impl;

import com.yetistep.delivr.abs.AbstractManager;
import com.yetistep.delivr.dao.inf.*;
import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.dto.PaginationDto;
import com.yetistep.delivr.dto.RequestJsonDto;
import com.yetistep.delivr.enums.NotifyTo;
import com.yetistep.delivr.enums.PushNotificationRedirect;
import com.yetistep.delivr.enums.Role;
import com.yetistep.delivr.enums.Status;
import com.yetistep.delivr.model.*;
import com.yetistep.delivr.service.inf.ManagerService;
import com.yetistep.delivr.service.inf.MerchantService;
import com.yetistep.delivr.service.inf.SystemAlgorithmService;
import com.yetistep.delivr.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.sql.Timestamp;
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

    @Autowired
    UserDeviceDaoService userDeviceDaoService;

    @Autowired
    DBoyAdvanceAmountDaoService dBoyAdvanceAmountDaoService;

    @Autowired
    OrderDaoService orderDaoService;

    @Autowired
    WalletTransactionDaoService walletTransactionDaoService;

    @Autowired
    SystemAlgorithmService systemAlgorithmService;

    @Override
    public void saveManagerOrAccountant(UserEntity user, HeaderDto headerDto) throws Exception {
        log.info("++++++++++++++++++ Creating User +++++++++++++++++");
        user.setUsername(headerDto.getUsername());
        if ((user.getUsername() == null) || (user.getUsername().isEmpty()))
            throw new YSException("VLD009");


        if(user.getEmailAddress() != null && !user.getEmailAddress().isEmpty()){
            if(userDaoService.checkIfEmailExists(user.getEmailAddress())){
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
        user.setCreatedDate(DateUtil.getCurrentTimestampSQL());
        if(user.getEmailAddress().isEmpty()){
            user.setEmailAddress(null);
        }


        String profileImage = user.getProfileImage();
        user.setProfileImage(null);

        if(user.getAddresses() != null) {
            for (AddressEntity address: user.getAddresses()){
                address.setUser(user);
            }
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
        UserEntity dbUser = userDaoService.find(Integer.parseInt(headerDto.getId()));
        if (dbUser == null) {
            throw new YSException("VLD011");
        }
        if(user.getAddresses() != null){
            List<AddressEntity> addressEntities =  user.getAddresses();
            for(AddressEntity addressEntity: addressEntities){
                for(AddressEntity address: dbUser.getAddresses()){
                    if(address.getId().equals(addressEntity.getId())){
                        address.setStreet(addressEntity.getStreet());
                        address.setCity(addressEntity.getCity());
                        address.setState(addressEntity.getState());
                        address.setCountry(addressEntity.getCountry());
                        address.setCountryCode(addressEntity.getCountryCode());
                        address.setUser(dbUser);
                        break;
                    }
                }
            }
        }

        dbUser.setFullName(user.getFullName());
        dbUser.setEmailAddress(user.getEmailAddress());
        dbUser.setMobileNumber(user.getMobileNumber());
        dbUser.setStatus(user.getStatus());
        dbUser.setUsername(user.getEmailAddress());
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
        dBoyAdvanceAmount.setType("advanceAmount");
        dBoyAdvanceAmount.setDeliveryBoy(dBoy);
        dBoyAdvanceAmount.setAdvanceDate(DateUtil.getCurrentTimestampSQL());
        if(requestJsonDto.getAdvanceAmountOrderId() != null){
            OrderEntity order = new OrderEntity();
            order.setId(requestJsonDto.getAdvanceAmountOrderId());
            dBoyAdvanceAmount.setOrder(order);
        }
        dBoyAdvanceAmounts.add(dBoyAdvanceAmount);
        dBoy.setdBoyAdvanceAmounts(dBoyAdvanceAmounts);
        deliveryBoyDaoService.update(dBoy);
        String fields = "id,availabilityStatus,averageRating,previousDue,availableAmount,bankAmount,walletAmount,advanceAmount,vehicleType,licenseNumber,vehicleNumber,user,latitude,longitude";
        Map<String, String> assoc = new HashMap<>();
        Map<String, String> subAssoc = new HashMap<>();
        assoc.put("user", "id,fullName,mobileNumber,emailAddress,profileImage,gender,status,addresses");
        subAssoc.put("addresses", "street,city,state,country,latitude,longitude");
        DeliveryBoyEntity deliveryBoy = ((DeliveryBoyEntity) ReturnJsonUtil.getJsonObject(dBoy, fields, assoc, subAssoc));
        Timestamp lastAckDate = dBoyAdvanceAmountDaoService.getLatestAckTimestamp(deliveryBoy.getId());
        BigDecimal cancelledPurchaseTotal = BigDecimal.ZERO;
        //if(lastAckDate != null){
        List<OrderEntity> cancelledPurchasedOrders =  orderDaoService.getCancelledPurchasedOrder(deliveryBoy.getId(), lastAckDate);
        for (OrderEntity order: cancelledPurchasedOrders){
            Boolean itemPurchased = false;
            List<ItemsOrderEntity> itemsOrders = order.getItemsOrder();
            for(ItemsOrderEntity itemsOrder: itemsOrders){
                if(itemsOrder.getPurchaseStatus() != null && itemsOrder.getPurchaseStatus()){
                    itemPurchased = true;
                    break;
                }
            }
            if(itemPurchased)
                cancelledPurchaseTotal =  cancelledPurchaseTotal.add(order.getGrandTotal());
        }
        //}

        deliveryBoy.setItemReturnedTotal(cancelledPurchaseTotal);

        return deliveryBoy;
    }

    @Override
    public DeliveryBoyEntity ackSubmittedAmount(HeaderDto headerDto, RequestJsonDto requestJsonDto) throws Exception {
        DeliveryBoyEntity dBoy = deliveryBoyDaoService.find(Integer.parseInt(headerDto.getId()));

        dBoy.setPreviousDue(dBoy.getPreviousDue().subtract(requestJsonDto.getSubmittedAmount()));
        dBoy.setAvailableAmount(dBoy.getAvailableAmount().subtract(requestJsonDto.getSubmittedAmount()));
        dBoy.setWalletAmount(dBoy.getWalletAmount().subtract(requestJsonDto.getSubmittedAmount()));
        List<DBoyAdvanceAmountEntity> dBoySubmittedAmounts = new ArrayList<>();
        DBoyAdvanceAmountEntity dBoySubmittedAmount = new DBoyAdvanceAmountEntity();
        dBoySubmittedAmount.setAmountAdvance(requestJsonDto.getSubmittedAmount());
        dBoySubmittedAmount.setType("acknowledgeAmount");
        dBoySubmittedAmount.setDeliveryBoy(dBoy);
        dBoySubmittedAmount.setAdvanceDate(DateUtil.getCurrentTimestampSQL());
        dBoySubmittedAmounts.add(dBoySubmittedAmount);
        dBoy.setdBoyAdvanceAmounts(dBoySubmittedAmounts);

        deliveryBoyDaoService.update(dBoy);
        String fields = "id,availabilityStatus,averageRating,previousDue,availableAmount,bankAmount,walletAmount,advanceAmount,vehicleType,licenseNumber,vehicleNumber,user,latitude,longitude";

        Map<String, String> assoc = new HashMap<>();
        Map<String, String> subAssoc = new HashMap<>();

        assoc.put("user", "id,fullName,mobileNumber,emailAddress,profileImage,gender,status,addresses");
        subAssoc.put("addresses", "street,city,state,country,latitude,longitude");

        DeliveryBoyEntity deliveryBoy = ((DeliveryBoyEntity) ReturnJsonUtil.getJsonObject(dBoy, fields, assoc, subAssoc));

        Timestamp lastAckDate = dBoyAdvanceAmountDaoService.getLatestAckTimestamp(deliveryBoy.getId());

        BigDecimal cancelledPurchaseTotal = BigDecimal.ZERO;
        //if(lastAckDate != null){
        List<OrderEntity> cancelledPurchasedOrders =  orderDaoService.getCancelledPurchasedOrder(deliveryBoy.getId(), lastAckDate);
        for (OrderEntity order: cancelledPurchasedOrders){
            Boolean itemPurchased = false;
            List<ItemsOrderEntity> itemsOrders = order.getItemsOrder();
            for(ItemsOrderEntity itemsOrder: itemsOrders){
                if(itemsOrder.getPurchaseStatus() != null && itemsOrder.getPurchaseStatus()){
                    itemPurchased = true;
                    break;
                }
            }
            if(itemPurchased)
                cancelledPurchaseTotal =  cancelledPurchaseTotal.add(order.getGrandTotal());
        }
        //}

        deliveryBoy.setItemReturnedTotal(cancelledPurchaseTotal);

        return deliveryBoy;
    }

    @Override
    public DeliveryBoyEntity walletSubmittedAmount(HeaderDto headerDto, RequestJsonDto requestJsonDto) throws Exception {
        DeliveryBoyEntity dBoy = deliveryBoyDaoService.find(Integer.parseInt(headerDto.getId()));

        dBoy.setPreviousDue(BigDecimal.ZERO);
        dBoy.setWalletAmount(dBoy.getWalletAmount().subtract(requestJsonDto.getSubmittedAmount()));
        dBoy.setAvailableAmount(dBoy.getAvailableAmount().subtract(requestJsonDto.getSubmittedAmount()));

        List<DBoyAdvanceAmountEntity> dBoySubmittedAmounts = new ArrayList<>();
        DBoyAdvanceAmountEntity dBoySubmittedAmount = new DBoyAdvanceAmountEntity();
        dBoySubmittedAmount.setAdvanceDate(DateUtil.getCurrentTimestampSQL());
        dBoySubmittedAmount.setAmountAdvance(requestJsonDto.getSubmittedAmount());
        dBoySubmittedAmount.setDeliveryBoy(dBoy);
        dBoySubmittedAmount.setType("acknowledgeAmount");
        dBoySubmittedAmounts.add(dBoySubmittedAmount);
        dBoy.setdBoyAdvanceAmounts(dBoySubmittedAmounts);

        deliveryBoyDaoService.update(dBoy);

        String fields = "id,availabilityStatus,averageRating,previousDue,availableAmount,bankAmount,walletAmount,advanceAmount,vehicleType,licenseNumber,vehicleNumber,user,latitude,longitude";

        Map<String, String> assoc = new HashMap<>();
        Map<String, String> subAssoc = new HashMap<>();

        assoc.put("user", "id,fullName,mobileNumber,emailAddress,profileImage,gender,status,addresses");
        subAssoc.put("addresses", "street,city,state,country,latitude,longitude");

        DeliveryBoyEntity deliveryBoy = ((DeliveryBoyEntity) ReturnJsonUtil.getJsonObject(dBoy, fields, assoc, subAssoc));

        Timestamp lastAckDate = dBoyAdvanceAmountDaoService.getLatestAckTimestamp(deliveryBoy.getId());

        BigDecimal cancelledPurchaseTotal = BigDecimal.ZERO;
        //if(lastAckDate != null){
        List<OrderEntity> cancelledPurchasedOrders =  orderDaoService.getCancelledPurchasedOrder(deliveryBoy.getId(), lastAckDate);
        for (OrderEntity order: cancelledPurchasedOrders){
            Boolean itemPurchased = false;
            List<ItemsOrderEntity> itemsOrders = order.getItemsOrder();
            for(ItemsOrderEntity itemsOrder: itemsOrders){
                if(itemsOrder.getPurchaseStatus() != null && itemsOrder.getPurchaseStatus()){
                    itemPurchased = true;
                    break;
                }
            }
            if(itemPurchased)
                cancelledPurchaseTotal =  cancelledPurchaseTotal.add(order.getGrandTotal());
        }
        //}

        deliveryBoy.setItemReturnedTotal(cancelledPurchaseTotal);

        return deliveryBoy;
    }

    @Override
    public List<StoresBrandEntity> findFeaturedAndPrioritizedStoreBrands() throws Exception {

        String fields = "id,brandName,brandLogo,brandImage,brandUrl,featured,priority,merchant";

        Map<String, String> assoc = new HashMap<>();
        Map<String, String> subAssoc = new HashMap<>();

        assoc.put("merchant", "id,businessTitle,user");
        subAssoc.put("user", "id,fullName,status");

        List<StoresBrandEntity> brandEntities = new ArrayList<>();
        List<StoresBrandEntity> brands = storesBrandDaoService.findFeaturedAndPriorityBrands();
        for (StoresBrandEntity brand: brands){
            brandEntities.add((StoresBrandEntity) ReturnJsonUtil.getJsonObject(brand, fields, assoc, subAssoc));
        }
        return brandEntities;

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

        String fields = "id,brandName,brandLogo,brandImage,brandUrl,featured,priority,merchant";

        Map<String, String> assoc = new HashMap<>();
        Map<String, String> subAssoc = new HashMap<>();

        assoc.put("merchant", "id,businessTitle,user");
        subAssoc.put("user", "id,fullName,status");

        List<StoresBrandEntity> brandEntities = new ArrayList<>();
        for (StoresBrandEntity brand: storesBrandEntities){
            brandEntities.add((StoresBrandEntity) ReturnJsonUtil.getJsonObject(brand, fields, assoc, subAssoc));
        }

        paginationDto.setData(brandEntities);
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

        String fields = "id,brandName,brandLogo,brandImage,brandUrl,featured,priority,merchant";

        Map<String, String> assoc = new HashMap<>();
        Map<String, String> subAssoc = new HashMap<>();

        assoc.put("merchant", "id,businessTitle,user");
        subAssoc.put("user", "id,fullName,status");

        List<StoresBrandEntity> brandEntities = new ArrayList<>();
        for (StoresBrandEntity brand: storesBrandEntities){
            brandEntities.add((StoresBrandEntity) ReturnJsonUtil.getJsonObject(brand, fields, assoc, subAssoc));
        }

        paginationDto.setData(brandEntities);
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
        List<CategoryEntity> childCategories = new ArrayList<>();
        if (headerDto.getId() != null && !headerDto.getId().equals("") ){
            CategoryEntity parentCategory = new CategoryEntity();
            parentCategory.setId(Integer.parseInt(headerDto.getId()));
            category.setParent(parentCategory);

            childCategories = merchantDaoService.findChildCategories(Integer.parseInt(headerDto.getId()), null);

            List<ItemEntity> items = merchantDaoService.getCategoriesItems(Integer.parseInt(headerDto.getId()));

            if(items.size() > 0){
                throw new YSException("VLD022");
            }

        }  else {
            childCategories = merchantDaoService.findParentCategories();
        }

        if(childCategories != null && childCategories.size()>0){
            for (CategoryEntity categoryEntity: childCategories){
                if(categoryEntity.getName().equals(category.getName())){
                    throw new YSException("VLD035");
                }
            }
        }

        String categoryImage = category.getImageUrl();
        category.setImageUrl(null);
        category.setCreatedDate(DateUtil.getCurrentTimestampSQL());
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
        List<CategoryEntity> childCategories = new ArrayList<>();

        if (dbCategory.getParent() != null){
            CategoryEntity parentCategory = dbCategory.getParent();
            childCategories = merchantDaoService.findChildCategories(parentCategory.getId(), null);
        }  else {
            childCategories = merchantDaoService.findParentCategories();
        }


        String categoryImage = category.getImageUrl();
        String dbImageUrl = dbCategory.getImageUrl();

        if(categoryImage != null){
            dbCategory.setImageUrl(null);
        }

        dbCategory.setName(category.getName());
        if(childCategories != null && childCategories.size()>0){
            for (CategoryEntity child: childCategories){
                if( child.getName() != null && !child.getId().equals(dbCategory.getId()) &&  child.getName().equals(dbCategory.getName())){
                    throw new YSException("VLD035");
                }
            }
        }

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


        String fields = "id,fullName,mobileNumber,emailAddress,profileImage,gender,status,verifiedStatus,addresses";

        Map<String, String> assoc = new HashMap<>();

        assoc.put("addresses", "id,latitude,longitude,street,city,state,country");


        return ((UserEntity) ReturnJsonUtil.getJsonObject(userEntity, fields, assoc));
    }

    @Override
    public PaginationDto findAllManagers(RequestJsonDto requestJsonDto) throws Exception {
        log.info("Retrieving list of Deliver Boys");
        PaginationDto paginationDto = new PaginationDto();


        Page page = requestJsonDto.getPage();

        Integer totalRows =  userDaoService.getTotalNumberManagers();
        paginationDto.setNumberOfRows(totalRows);

        if(page != null){
            page.setTotalRows(totalRows);
        }

        List<UserEntity> userEntities = userDaoService.findManagers(page);
        List<UserEntity> users = new ArrayList<>();

        String fields = "id,fullName,mobileNumber,emailAddress,status,verifiedStatus,addresses";

        Map<String, String> assoc = new HashMap<>();

        assoc.put("addresses", "id,latitude,longitude,street,city,state,country");


        for (UserEntity userEntity:userEntities){
            UserEntity user = (UserEntity) ReturnJsonUtil.getJsonObject(userEntity, fields, assoc);
            users.add(user);
        }

        paginationDto.setData(users);
        return paginationDto;
    }

    @Override
    public PaginationDto findAllAccountants(RequestJsonDto requestJsonDto) throws Exception {
        log.info("Retrieving list of Deliver Boys");
        PaginationDto paginationDto = new PaginationDto();

        Page page = requestJsonDto.getPage();

        Integer totalRows =  userDaoService.getTotalNumberAccountants();
        paginationDto.setNumberOfRows(totalRows);

        if(page != null){
            page.setTotalRows(totalRows);
        }

        List<UserEntity> userEntities = userDaoService.findAccountants(page);
        List<UserEntity> users = new ArrayList<>();

        String fields = "id,fullName,mobileNumber,emailAddress,status,addresses";

        Map<String, String> assoc = new HashMap<>();

        assoc.put("addresses", "id,latitude,longitude,street,city,state,country");

        for (UserEntity userEntity:userEntities){
            UserEntity user = (UserEntity) ReturnJsonUtil.getJsonObject(userEntity, fields, assoc);
            users.add(user);
        }

        paginationDto.setData(users);
        return paginationDto;
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

    @Override
    public PaginationDto getInactivatedCustomers(RequestJsonDto requestJsonDto) throws Exception {
        log.info("+++++++++++++ Getting Inactivated Customers ++++++++++");

        Page page = requestJsonDto.getPage();


        PaginationDto paginationDto = new PaginationDto();
        Integer totalRows =  userDaoService.getTotalNumberInactiveCustomers();
        paginationDto.setNumberOfRows(totalRows);

        if(page != null){
            page.setTotalRows(totalRows);
        }


        List<UserEntity> users = userDaoService.getInactivatedCustomers(page);
        paginationDto.setData(users);
        return paginationDto;
    }

    @Override
    public Boolean activateUser(HeaderDto headerDto) throws Exception {
        log.info("+++++++++++++++ Activating User +++++++++++++");
        userDaoService.activateUser(Integer.parseInt(headerDto.getId()));
        return true;
    }

    @Override
    public Boolean sendPushMessageTo(List<NotifyTo> notifyToList, String message) throws Exception {
        for(NotifyTo notifyTo: notifyToList){
            if(notifyTo.equals(NotifyTo.DELIVERY_BOY))
                sendNotification(Role.ROLE_DELIVERY_BOY, "ANDROID", notifyTo, message);
            else if(notifyTo.equals(NotifyTo.CUSTOMER_ANDROID))
                sendNotification(Role.ROLE_CUSTOMER, "ANDROID", NotifyTo.CUSTOMER, message);
            else if(notifyTo.equals(NotifyTo.CUSTOMER_IOS))
                sendNotification(Role.ROLE_CUSTOMER, "MAC_OS_X", notifyTo, message);
        }
        return true;
    }

    private void sendNotification(Role role, String family, NotifyTo notifyTo, String message) throws Exception{
        List<String> deviceTokens = userDeviceDaoService.getAllDeviceTokensForFamilyAndRole(role, family);
        PushNotification pushNotification = new PushNotification();
        pushNotification.setTokens(deviceTokens);
        pushNotification.setMessage(message);
        pushNotification.setPushNotificationRedirect(PushNotificationRedirect.INFO);
        pushNotification.setNotifyTo(notifyTo);
        PushNotificationUtil.sendNotification(pushNotification, family);
    }

    @Override
    public void updateCategoryPriority(RequestJsonDto requestJsonDto) throws Exception{
            log.info("+++++++++++++++ updating category priority +++++++++++++");
         List<CategoryEntity> categoryEntities = requestJsonDto.getCategoryList();

         categoryDaoService.updatePriority(categoryEntities);
    }

    @Override
    public List<WalletTransactionEntity> getWalletTransactionInformation() throws Exception {
        List<WalletTransactionEntity> walletTransactionEntities = walletTransactionDaoService.findAll();
        for(WalletTransactionEntity walletTransactionEntity: walletTransactionEntities){
            systemAlgorithmService.decodeWalletTransaction(walletTransactionEntity);
            CustomerEntity customerEntity = new CustomerEntity();
            customerEntity.setId(walletTransactionEntity.getCustomer().getId());
            walletTransactionEntity.setCustomer(customerEntity);
        }
        return walletTransactionEntities;
    }
}
