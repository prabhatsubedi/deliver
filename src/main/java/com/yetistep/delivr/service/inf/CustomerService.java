package com.yetistep.delivr.service.inf;

import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.dto.PaginationDto;
import com.yetistep.delivr.dto.PaymentGatewayDto;
import com.yetistep.delivr.dto.RequestJsonDto;
import com.yetistep.delivr.enums.RatingReason;
import com.yetistep.delivr.model.*;
import com.yetistep.delivr.model.mobile.AddressDto;
import com.yetistep.delivr.model.mobile.dto.CheckOutDto;
import com.yetistep.delivr.model.mobile.dto.MyOrderDto;
import com.yetistep.delivr.model.mobile.dto.SearchDto;
import com.yetistep.delivr.model.mobile.dto.TrackOrderDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 11/21/14
 * Time: 12:15 PM
 * To change this template use File | Settings | File Templates.
 */
public interface CustomerService {
    public void login(CustomerEntity customerEntity) throws Exception;

    public void saveCustomer(CustomerEntity customer, HeaderDto headerDto) throws Exception;

    public Integer addCustomerAddress(AddressDto address) throws Exception;

    public AddressDto verifyMobile(String mobile, Long facebookId) throws Exception;

    public void saveOrder(RequestJsonDto requestJson, HeaderDto headerDto) throws Exception;

    public void registerCustomer(UserEntity user, HeaderDto headerDto) throws Exception;

    public UserEntity getDeliveredAddress(Long facebookId) throws Exception;

    public void deleteDeliveredAddress(Integer addressId) throws Exception;

    public CheckOutDto getCheckOutInfo(Long facebookId, Integer addressId) throws Exception;

    public List<CategoryEntity> getDefaultCategories() throws Exception;

    public Map<String, Object> getCategoryBrands(Integer categoryId, Integer pageNo,String lat, String lon) throws Exception;

    public List<RatingReason> getRatingReasons() throws Exception;

    public List<MyOrderDto> getMyCurrentOrders(Long facebookId) throws Exception;

    public PaginationDto getMyPastOrders(Long facebookId, Page page) throws Exception;

    public SearchDto getSearchContent(String word, RequestJsonDto requestJsonDto) throws Exception;

    public SearchDto getSearchInStore(String word, Integer brandId, RequestJsonDto requestJsonDto) throws Exception;

    public TrackOrderDto getTrackOrderInfo(Integer orderId) throws Exception;

    public CustomerEntity getCustomerProfile(Long facebookId) throws Exception;

    public Boolean rateDeliveryBoy(Integer orderId, Long facebookId, RatingEntity rating) throws Exception;

    public RatingEntity getRatingFromCustomerSide(Integer orderId) throws Exception;

    public Boolean reprocessOrder(Integer orderId) throws Exception;

    public Boolean cancelOrder(OrderEntity order) throws Exception;

    public Boolean refillWallet(CustomerEntity customer) throws Exception;

    public PaginationDto getWalletTransactions(Page page, Long facebookId) throws Exception;

    public Boolean refillCustomerWallet(Long facebookId, BigDecimal refillAmount, String remark, Boolean isTransfer) throws Exception;

    public PaymentGatewayDto requestToAddFundToWallet(Long facebookId, BigDecimal amount) throws Exception;

    public String paymentGatewaySettlement(PaymentGatewayDto paymentGatewayDto) throws Exception;

    public CustomerEntity getWalletBalance(Long facebookId) throws Exception;

    public void setReferralReward(OrderEntity orderJson) throws Exception;


    public CustomerEntity mobileLogin(RequestJsonDto requestJsonDto) throws Exception;

    public RequestJsonDto customerSignUp(UserEntity user) throws Exception;

    public void updateRewards() throws Exception;

    public void updateReferredCount() throws Exception;

    public void registerCustomerMobile(UserEntity user, HeaderDto headerDto) throws Exception;

    public void orderCanceledToInRouteToDelivery(Integer orderId) throws Exception;
}


