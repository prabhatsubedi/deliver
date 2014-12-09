package com.yetistep.delivr.service.inf;

import com.yetistep.delivr.model.MerchantEntity;
import org.springframework.http.HttpHeaders;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 11/21/14
 * Time: 12:15 PM
 * To change this template use File | Settings | File Templates.
 */
public interface MerchantService {
    public void saveMerchant(MerchantEntity merchant, HttpHeaders headers) throws Exception;

    public void activateMerchant(MerchantEntity merchantEntity) throws Exception;

    public List<MerchantEntity> getMerchants() throws Exception;
}
