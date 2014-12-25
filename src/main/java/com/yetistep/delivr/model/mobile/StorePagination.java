package com.yetistep.delivr.model.mobile;

import com.yetistep.delivr.model.StoresBrandEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 12/24/14
 * Time: 12:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class StorePagination {
    private Integer totalResults;
    private Integer pageSize;
    private Integer totalPage;
    private Map<Integer, Integer[]> paging;

    public StorePagination(){
        pageSize = 20;
        paging = new HashMap<Integer, Integer[]>();
    }

   public List<StoresBrandEntity> paginateBrands(List<StoresBrandEntity> raw, Integer reserveRows) {
       List<StoresBrandEntity> storeBrands = null;
       totalResults = raw.size();

       //set first page result and return to the user
       if(totalResults > pageSize)
           storeBrands = raw.subList(0, pageSize-reserveRows);
       else
           storeBrands = raw.subList(0, totalResults);

       totalPage = (int) Math.ceil(totalResults.doubleValue()/pageSize.doubleValue());

       //divide into different pages
       for(int i=0;i<totalPage;i++) {
           int requiredSize = (i+1) * pageSize;
           List<StoresBrandEntity> storeSubList = null;

           if(totalResults >= requiredSize)
               storeSubList = raw.subList(i*pageSize, requiredSize);
           else
               storeSubList = raw.subList(i*pageSize, totalResults);

           Integer [] brandId = new Integer[storeSubList.size()];

           for(int j=0; j< storeSubList.size(); j++) {
               brandId[j] = storeSubList.get(j).getId();
           }

           paging.put(i+1, brandId);
       }

       return storeBrands;

   }

}
