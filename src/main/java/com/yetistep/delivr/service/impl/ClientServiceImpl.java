package com.yetistep.delivr.service.impl;

import com.yetistep.delivr.dao.inf.StoreDaoService;
import com.yetistep.delivr.dao.inf.StoresBrandDaoService;
import com.yetistep.delivr.dto.RequestJsonDto;
import com.yetistep.delivr.model.StoreEntity;
import com.yetistep.delivr.model.StoresBrandEntity;
import com.yetistep.delivr.model.mobile.PageInfo;
import com.yetistep.delivr.model.mobile.StaticPagination;
import com.yetistep.delivr.service.inf.ClientService;
import com.yetistep.delivr.util.GeoCodingUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 12/9/14
 * Time: 12:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClientServiceImpl implements ClientService {
    private static final Logger log = Logger.getLogger(ClientServiceImpl.class);
    @Autowired
    StoresBrandDaoService storesBrandDaoService;

    @Autowired
    StoreDaoService storeDaoService;

    @Override
    public Map<String, Object> getBrands(RequestJsonDto requestJsonDto, Integer pageId) throws Exception {
        log.info("+++++++++ Getting all brands +++++++++++++++");

        /* Featured Brand List Only */
        List<StoresBrandEntity> featuredBrands = storesBrandDaoService.findFeaturedBrands();

        /* Priority Brands Not Featured */
        List<StoresBrandEntity> priorityBrands = null;
        boolean isBrandWitDistanceSort = false;

        if (requestJsonDto.getGpsInfo() == null) {
            //FIXME : Check Customer in Database and retrieve lat & long]
            priorityBrands = storesBrandDaoService.findPriorityBrands(null);
        } else {
            priorityBrands = storesBrandDaoService.findPriorityBrands("Not Null");
            isBrandWitDistanceSort = true;
        }

        /* Add Both Brand in One List */
        List<StoresBrandEntity> storeBrandResult = new ArrayList<>();

        if (priorityBrands.size() > 0)
            storeBrandResult.addAll(priorityBrands);

        /* Other then Featured and Priority List, Should be Sort by Customer Distance */
        if (isBrandWitDistanceSort) {
            List<Integer> list = new ArrayList<>();
            list.add(3);

            List<StoreEntity> storeEntities = storeDaoService.findStores(list);

        /* Extract Latitude and Longitude */
            String[] storeDistance = new String[storeEntities.size()];
//        String[] customerDistance = {requestJsonDto.getGpsInfo().getLatitude(), requestJsonDto.getGpsInfo().getLongitude()};
            String[] customerDistance = {"27.6991101,85.3350114"};

            for (int i = 0; i < storeEntities.size(); i++) {
                storeDistance[i] = GeoCodingUtil.getLatLong(storeEntities.get(i).getLatitude(), storeEntities.get(i).getLongitude());
            }

            List<BigDecimal> distanceList = GeoCodingUtil.getListOfDistances(customerDistance, storeDistance);

            //Set Distance to Store Entity
            for (int i = 0; i < storeEntities.size(); i++) {
                storeEntities.get(i).setCustomerToStoreDistance(distanceList.get(i));
            }

            //Store Entity List Sorted by Distance
            Collections.sort(storeEntities, new StoreDistanceComparator());

            //Now Combine all brand in one list
            for (StoreEntity storeEntity : storeEntities) {
                if (!containsBrandId(storeBrandResult, storeEntity.getStoresBrand().getId()) && !containsBrandId(featuredBrands, storeEntity.getStoresBrand().getId())) {
                    StoresBrandEntity tempBrand = new StoresBrandEntity();
                    tempBrand.setId(storeEntity.getStoresBrand().getId());
                    tempBrand.setOpeningTime(storeEntity.getStoresBrand().getOpeningTime());
                    tempBrand.setClosingTime(storeEntity.getStoresBrand().getClosingTime());
                    tempBrand.setBrandName(storeEntity.getStoresBrand().getBrandName());
                    tempBrand.setBrandLogo(storeEntity.getStoresBrand().getBrandLogo());
                    tempBrand.setBrandImage(storeEntity.getStoresBrand().getBrandImage());
                    tempBrand.setBrandUrl(storeEntity.getStoresBrand().getBrandUrl());
                    tempBrand.setFeatured(storeEntity.getStoresBrand().getFeatured());
                    tempBrand.setPriority(storeEntity.getStoresBrand().getPriority());
                    storeBrandResult.add(tempBrand);

                }

            }
        }

        // Perform sorted store pagination
        PageInfo pageInfo = null;
        if(storeBrandResult.size() > 0){
            StaticPagination staticPagination= new StaticPagination();
            staticPagination.paginate(storeBrandResult, pageId);
            pageInfo = staticPagination;
        }

        Map<String, Object> map = new HashMap<>();
        map.put("featured", featuredBrands);
        map.put("all", storeBrandResult);
        map.put("page", pageInfo);

        return map;
    }

    class StoreDistanceComparator implements Comparator<StoreEntity> {

        @Override
        public int compare(StoreEntity o1, StoreEntity o2) {
            return Integer.parseInt(o1.getCustomerToStoreDistance().subtract(o2.getCustomerToStoreDistance()).toString());
        }
    }

    private boolean containsBrandId(List<StoresBrandEntity> list, Integer id) {
        for (StoresBrandEntity object : list) {
            if (object.getId() == id) {
                return true;
            }
        }
        return false;
    }
}
