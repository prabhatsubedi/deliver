package com.yetistep.delivr.service.impl;

import com.yetistep.delivr.dao.inf.*;
import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.dto.PaginationDto;
import com.yetistep.delivr.dto.RequestJsonDto;
import com.yetistep.delivr.model.*;
import com.yetistep.delivr.service.inf.ManagerService;
import com.yetistep.delivr.service.inf.MerchantService;
import com.yetistep.delivr.util.AmazonUtil;
import com.yetistep.delivr.util.GeneralUtil;
import com.yetistep.delivr.util.MessageBundle;
import com.yetistep.delivr.util.YSException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 11/25/14
 * Time: 10:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class ManagerServiceImpl implements ManagerService {
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

        if(dBoy.getPreviousDue().compareTo(BigDecimal.ZERO) != 0)
            throw new YSException("ERR015");

        dBoy.setAdvanceAmount(requestJsonDto.getAdvanceAmount());
        dBoy.setBankAmount(dBoy.getBankAmount().add(requestJsonDto.getAdvanceAmount()));
        dBoy.setAvailableAmount(dBoy.getAvailableAmount().add(requestJsonDto.getAdvanceAmount()));

        List<DboyAdvanceAmountEntity> dBoyAdvanceAmounts = new ArrayList<DboyAdvanceAmountEntity>();
        DboyAdvanceAmountEntity dBoyAdvanceAmount = new DboyAdvanceAmountEntity();
        dBoyAdvanceAmount.setAmountAdvance(requestJsonDto.getAdvanceAmount());
        dBoyAdvanceAmount.setDeliveryBoy(dBoy);
        dBoyAdvanceAmounts.add(dBoyAdvanceAmount);

        dBoy.setdBoyAdvanceAmounts(dBoyAdvanceAmounts);

        deliveryBoyDaoService.update(dBoy);

        return dBoy;
    }

    @Override
    public DeliveryBoyEntity ackSubmittedAmount(HeaderDto headerDto, RequestJsonDto requestJsonDto) throws Exception {
        DeliveryBoyEntity dBoy = deliveryBoyDaoService.find(Integer.parseInt(headerDto.getId()));

        dBoy.setPreviousDue(BigDecimal.ZERO);

        List<DboySubmittedAmountEntity> dBoySubmittedAmounts = new ArrayList<DboySubmittedAmountEntity>();
        DboySubmittedAmountEntity dBoySubmittedAmount = new DboySubmittedAmountEntity();
        dBoySubmittedAmount.setAmountReceived(requestJsonDto.getSubmittedAmount());
        dBoySubmittedAmount.setDeliveryBoy(dBoy);
        dBoySubmittedAmounts.add(dBoySubmittedAmount);
        dBoy.setdBoySubmittedAmount(dBoySubmittedAmounts);

        deliveryBoyDaoService.update(dBoy);

        return dBoy;
    }

    @Override
    public DeliveryBoyEntity walletSubmittedAmount(HeaderDto headerDto, RequestJsonDto requestJsonDto) throws Exception {
        DeliveryBoyEntity dBoy = deliveryBoyDaoService.find(Integer.parseInt(headerDto.getId()));

        dBoy.setWalletAmount(BigDecimal.ZERO);

        List<DboySubmittedAmountEntity> dBoySubmittedAmounts = new ArrayList<DboySubmittedAmountEntity>();
        DboySubmittedAmountEntity dBoySubmittedAmount = new DboySubmittedAmountEntity();

        dBoySubmittedAmount.setAmountReceived(requestJsonDto.getSubmittedAmount());
        dBoySubmittedAmount.setDeliveryBoy(dBoy);
        dBoySubmittedAmounts.add(dBoySubmittedAmount);
        dBoy.setdBoySubmittedAmount(dBoySubmittedAmounts);

        deliveryBoyDaoService.update(dBoy);

        return dBoy;
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
    public boolean saveCategory(CategoryEntity category, HeaderDto headerDto) throws Exception{

        if (headerDto.getId() != null){
            CategoryEntity parentCategory = new CategoryEntity();
            parentCategory.setId(Integer.parseInt(headerDto.getId()));
            category.setParent(parentCategory);
        }

        String categoryImage = category.getImageUrl();
        category.setImageUrl(null);
        if (category.getFeatured() == null) {
            category.setFeatured(false);
        }

        categoryDaoService.save(category);

        if(categoryImage != null){
            log.info("Uploading category image to S3 Bucket ");
            String dir = MessageBundle.separateString("/", "category" + category.getId());
            boolean isLocal = MessageBundle.isLocalHost();
            String categoryImageUrl = "categoryImage"+(isLocal ? "_tmp_" : "_") + category.getId()+System.currentTimeMillis();
            String s3PathImage = GeneralUtil.saveImageToBucket(categoryImage, categoryImageUrl, dir, true);
            category.setImageUrl(s3PathImage);
            categoryDaoService.update(category);
        }

        return true;
    }

    @Override
    public boolean updateCategory(CategoryEntity category, HeaderDto headerDto) throws Exception{

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
            String dir = MessageBundle.separateString("/", "category" + category.getId());
            boolean isLocal = MessageBundle.isLocalHost();

            if(dbImageUrl != null){
                log.info("deleting category image from S3 Bucket ");
                AmazonUtil.deleteFileFromBucket(AmazonUtil.getAmazonS3Key(dbImageUrl));
            }

            String categoryImageUrl = "categoryImage"+(isLocal ? "_tmp_" : "_") + category.getId()+System.currentTimeMillis();
            String s3PathImage = GeneralUtil.saveImageToBucket(categoryImage, categoryImageUrl, dir, true);
            category.setImageUrl(s3PathImage);
            categoryDaoService.update(category);
        }

        return true;
    }


    public List<CategoryEntity> getCategoryTree(List<CategoryEntity> categories, Integer parent_id){
        List<CategoryEntity> newCategories = new ArrayList<CategoryEntity>();
        for(CategoryEntity newCategory:  categories)
        {
            if(newCategory.getParent().getId()==parent_id && (newCategory.getStoresBrand()== null))
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
        return  findChildCategories(parentCategories);
    }

    @Override
    public CategoryEntity getCategory(HeaderDto headerDto) throws Exception{
        CategoryEntity category = categoryDaoService.findCategory(Integer.parseInt(headerDto.getId()));
        if(category != null){
            category.setChild(null);
            category.setItem(null);
            category.setStoresBrand(null);
        }
        return category;
    }
}
