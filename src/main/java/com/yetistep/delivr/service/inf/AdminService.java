package com.yetistep.delivr.service.inf;

import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.dto.RequestJsonDto;
import com.yetistep.delivr.model.CountryEntity;
import com.yetistep.delivr.model.mobile.SparrowResultModel;
import com.yetistep.delivr.model.mobile.dto.SMSDto;
import org.springframework.http.HttpHeaders;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 11/25/14
 * Time: 10:37 AM
 * To change this template use File | Settings | File Templates.
 */
public interface AdminService {

    public List<CountryEntity> findAllCountries() throws Exception;

    public List<Map<String, Map<Integer, Map<String, String>>>> getGodsView() throws Exception;

    public Map<String, Map<String, Integer>> getDeliveryGraphByDate(HeaderDto headerDto) throws Exception;

    public Map<String, Integer> getNewUserGraph(HeaderDto headerDto) throws Exception;

    public Map<String, Map<String, Integer>> getDeliverySuccessGraph(HeaderDto headerDto) throws Exception;

    public Map<String, Map<String, Integer>> getOnTimeDeliveryGraph(HeaderDto headerDto) throws Exception;

    public SparrowResultModel getSMSCredits() throws Exception;

    public List<SMSDto> customerSendableSMSList() throws Exception;

    public Boolean sendSMS(SMSDto smsDto) throws Exception;

    public Boolean updateDefaultImage(RequestJsonDto requestJsonDto) throws Exception;

}
