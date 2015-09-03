package com.yetistep.delivr.controller;

import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.dto.PaginationDto;
import com.yetistep.delivr.dto.RequestJsonDto;
import com.yetistep.delivr.model.DeliveryBoyEntity;
import com.yetistep.delivr.model.MerchantEntity;
import com.yetistep.delivr.model.OrderEntity;
import com.yetistep.delivr.service.inf.AccountService;
import com.yetistep.delivr.service.inf.DeliveryBoyService;
import com.yetistep.delivr.service.inf.ManagerService;
import com.yetistep.delivr.service.inf.MerchantService;
import com.yetistep.delivr.util.GeneralUtil;
import com.yetistep.delivr.util.ServiceResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 11/21/14
 * Time: 3:26 PM
 * To change this template use File | Settings | File Templates.
 */
@RestController
@RequestMapping(value = "accountant")
public class AccountantController {

    private static final Logger log = Logger.getLogger(AccountantController.class);

    @Autowired
    ManagerService managerService;

    @Autowired
    AccountService accountService;

    @Autowired
    DeliveryBoyService deliveryBoyService;

    @Autowired
    MerchantService merchantService;


    @RequestMapping(value = "/get_all_merchants", method = RequestMethod.GET)
    public ResponseEntity<ServiceResponse> getAllMerchants() {
        try{
            List<MerchantEntity> merchantEntities = merchantService.getAllMerchants();
            ServiceResponse serviceResponse = new ServiceResponse("Merchant retrieved successfully");
            serviceResponse.addParam("merchants", merchantEntities);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while getting merchants", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/get_merchants", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponse> getMerchants(@RequestBody RequestJsonDto requestJsonDto) {
        try{
            PaginationDto merchantEntities = merchantService.getMerchants(requestJsonDto);
            ServiceResponse serviceResponse = new ServiceResponse("Merchant retrieved successfully");
            serviceResponse.addParam("merchants", merchantEntities);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while getting merchants", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }


    @RequestMapping(value = "/get_dboy", method = RequestMethod.GET)
    public ResponseEntity<ServiceResponse> getDeliveryBoy(@RequestHeader HttpHeaders headers) {
        try {
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID);
            DeliveryBoyEntity deliveryBoy = deliveryBoyService.findDeliveryBoyById(headerDto);

            ServiceResponse serviceResponse = new ServiceResponse("Details of shopper with ID: "+headerDto.getId());
            serviceResponse.addParam("deliveryBoy", deliveryBoy);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while retrieving shopper: ", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/get_dboys", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponse> getAllDeliveryBoy(@RequestBody RequestJsonDto requestJsonDto) {
        try {
            PaginationDto deliveryBoyEntities = deliveryBoyService.findAllDeliverBoy(requestJsonDto);

            ServiceResponse serviceResponse = new ServiceResponse("List of shoppers");
            serviceResponse.addParam("deliveryBoys", deliveryBoyEntities);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while retrieving shoppers", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/update_dboy_account", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponse> updateDboyAccount(@RequestHeader HttpHeaders headers, @RequestBody RequestJsonDto requestJsonDto) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID);
            DeliveryBoyEntity deliveryBoy = managerService.updateDboyAccount(headerDto, requestJsonDto);

            ServiceResponse serviceResponse = new ServiceResponse("Shopper account updated successfully with ID: "+headerDto.getId());
            serviceResponse.addParam("deliveryBoy", deliveryBoy);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while updating shopper account: ", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/dboy_ack_payment", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponse> dBoyAckPayment(@RequestHeader HttpHeaders headers, @RequestBody RequestJsonDto requestJsonDto) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID);
            DeliveryBoyEntity deliveryBoy = managerService.ackSubmittedAmount(headerDto, requestJsonDto);

            ServiceResponse serviceResponse = new ServiceResponse("Shopper payment acknowledged successfully with ID: "+headerDto.getId());
            serviceResponse.addParam("deliveryBoy", deliveryBoy);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred on the process of acknowledgement: ", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }


    @RequestMapping(value = "/dboy_wallet_payment", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponse> dBoyWalletPayment(@RequestHeader HttpHeaders headers, @RequestBody RequestJsonDto requestJsonDto) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID);
            DeliveryBoyEntity deliveryBoy = managerService.walletSubmittedAmount(headerDto, requestJsonDto);

            ServiceResponse serviceResponse = new ServiceResponse("Shopper payment submitted successfully with ID: "+headerDto.getId());
            serviceResponse.addParam("deliveryBoy", deliveryBoy);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred on the process of payment submission: ", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/generate_invoice", method = RequestMethod.GET)
    public ResponseEntity<ServiceResponse> getGenerateInvoice(@RequestHeader HttpHeaders headers) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID);
            //String invoicePath = accountService.generateInvoice(Integer.parseInt(headerDto.getId()), "2015-02-18", "2015-03-06");

            ServiceResponse serviceResponse = new ServiceResponse("Invoice has been generated successfully for store: "+headerDto.getId());
            //serviceResponse.addParam("path", invoicePath);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while generating invoice: ", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }


    @RequestMapping(value = "/send_bill_and_receipt", method = RequestMethod.GET)
    public ResponseEntity<ServiceResponse> sendBillAndReceipt(@RequestHeader HttpHeaders headers) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID);
            //String invoicePath = accountService.generateBillAndReceiptAndSendEmail(headerDto);

            ServiceResponse serviceResponse = new ServiceResponse("Invoice has been generated successfully: "+headerDto.getId());
            //serviceResponse.addParam("path", invoicePath);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while generating invoice: ", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/pay_dboy", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponse> payDboy(@RequestHeader HttpHeaders headers) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID);
            accountService.payDboy(headerDto);

            ServiceResponse serviceResponse = new ServiceResponse("Shopper has been paid successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while paying Shopper: ", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/pay_invoice", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponse> payInvoice(@RequestHeader HttpHeaders headers) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID);
            accountService.payInvoice(headerDto);

            ServiceResponse serviceResponse = new ServiceResponse("Invoice(s) has been paid successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while paying invoice: ", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }


    @RequestMapping(value = "/settle_merchants_order", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponse> settleMerchantsOrder(@RequestHeader HttpHeaders headers) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID);
            accountService.settleMerchantsOrder(headerDto);

            ServiceResponse serviceResponse = new ServiceResponse("Shopper has been paid successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while paying Shopper: ", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

   /* @RequestMapping(value = "/get_acknowledgements", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponse> getAcknowledgements(@RequestHeader HttpHeaders headers, @RequestBody RequestJsonDto requestJsonDto) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID);
            PaginationDto acknowledgedAmounts = deliveryBoyService.getAcknowledgements(headerDto, requestJsonDto);

            ServiceResponse serviceResponse = new ServiceResponse("Acknowledgements retrieved successfully");
            serviceResponse.addParam("acknowledgedAmounts", acknowledgedAmounts);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred retrieving acknowledgements: ", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }*/

    @RequestMapping(value = "/get_advance_amounts", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponse> getAdvanceAmounts(@RequestHeader HttpHeaders headers, @RequestBody RequestJsonDto requestJsonDto) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID);
            PaginationDto advanceAmounts = deliveryBoyService.getAdvanceAmounts(headerDto, requestJsonDto);
            ServiceResponse serviceResponse = new ServiceResponse("Advance amounts retrieved successfully");
            serviceResponse.addParam("advanceAmounts", advanceAmounts);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while retrieving advance amounts: ", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/get_dboy_account", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponse> getDBoyAccount(@RequestHeader HttpHeaders headers, @RequestBody RequestJsonDto requestJsonDto) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID);
            PaginationDto advanceAmounts = accountService.getDBoyAccount(headerDto, requestJsonDto);
            ServiceResponse serviceResponse = new ServiceResponse("DBoy transactions retrieved successfully");
            serviceResponse.addParam("advanceAmounts", advanceAmounts);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while retrieving DBoy transactions: ", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/get_shoppers_transaction_account", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponse> getShoppersTransactionAccount(@RequestHeader HttpHeaders headers, @RequestBody RequestJsonDto requestJsonDto) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID);
            PaginationDto shoppersTransactionAccounts = accountService.getShoppersTransactionAccount(headerDto, requestJsonDto);
            ServiceResponse serviceResponse = new ServiceResponse("DBoy transactions retrieved successfully");
            serviceResponse.addParam("shoppersTransactionAccounts", shoppersTransactionAccounts);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while retrieving DBoy transactions: ", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }


    @RequestMapping(value = "/add_accountant_note", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponse> addAccountantNote(@RequestHeader HttpHeaders headers, @RequestBody RequestJsonDto requestJsonDto) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID);

            accountService.saveDBoyTransactionNote(headerDto, requestJsonDto);

            ServiceResponse serviceResponse = new ServiceResponse("Accountant note saved successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while saving accountant note: ", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }


    @RequestMapping(value = "/get_dboy_pay_statement", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponse> getDBoyPayStatement(@RequestHeader HttpHeaders headers, @RequestBody RequestJsonDto requestJsonDto) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID);
            PaginationDto payStatement = accountService.getDBoyPayStatement(headerDto, requestJsonDto);
            ServiceResponse serviceResponse = new ServiceResponse("DBoy pay statements retrieved successfully");
            serviceResponse.addParam("payStatement", payStatement);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while retrieving DBoy pay statements: ", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }


    @RequestMapping(value = "/pay_dboy_statement", method = RequestMethod.GET)
    public ResponseEntity<ServiceResponse> payDBoyPayStatement(@RequestHeader HttpHeaders headers) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID);
            accountService.payDBoyPayStatement(headerDto);
            ServiceResponse serviceResponse = new ServiceResponse("DBoy has been paid successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while paying DBoy : ", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }


    @RequestMapping(value = "/get_order", method = RequestMethod.GET)
    public ResponseEntity<ServiceResponse> getOrder(@RequestHeader HttpHeaders headers) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID);
            OrderEntity order = accountService.getOrder(headerDto);
            ServiceResponse serviceResponse = new ServiceResponse("Order has been retrieved successfully");
            serviceResponse.addParam("order", order);
            return new ResponseEntity<>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while retrieving order : ", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/get_invoices", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponse> getInvoices(@RequestBody RequestJsonDto requestJsonDto) {
        try{
            PaginationDto invoices = merchantService.getInvoices(requestJsonDto);

            ServiceResponse serviceResponse = new ServiceResponse("Invoices retrieved successfully");
            serviceResponse.addParam("invoices", invoices);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            GeneralUtil.logError(log, "Error Occurred while retrieving Invoices: ", e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/get_orders_amount_transferred", method = RequestMethod.GET)
    public ResponseEntity<ServiceResponse> getOrdersAmountTransferred() {
        try{
            List<OrderEntity> orders = accountService.getOrdersAmountTransferred();
            ServiceResponse serviceResponse = new ServiceResponse("orders retrieved successfully");
            serviceResponse.addParam("orders", orders);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            GeneralUtil.logError(log, "Error Occurred while retrieving Invoices: ", e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/clear_dboy_account", method = RequestMethod.GET)
    public ResponseEntity<ServiceResponse> clearDBoyAccount(@RequestHeader HttpHeaders headers) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID);
            DeliveryBoyEntity deliveryBoy = managerService.clearDBoyAccount(headerDto);
            ServiceResponse serviceResponse = new ServiceResponse("Account has been cleared successfully");
            serviceResponse.addParam("deliveryBoy", deliveryBoy);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            GeneralUtil.logError(log, "Error Occurred while clearing shopper account: ", e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/system_merchant_account", method = RequestMethod.GET)
    public ResponseEntity<ServiceResponse> systemMerchantAccount(@RequestHeader HttpHeaders headers, RequestJsonDto requestJsonDto) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.MERCHANT_ID);
            List<OrderEntity> orders = accountService.getSystemMerchantAccount(headerDto, requestJsonDto);
            ServiceResponse serviceResponse = new ServiceResponse("Account has been cleared successfully");
            serviceResponse.addParam("orders", orders);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            GeneralUtil.logError(log, "Error Occurred while clearing shopper account: ", e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }


}
