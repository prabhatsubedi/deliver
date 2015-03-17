package com.yetistep.delivr.util;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.yetistep.delivr.model.mobile.SparrowResultModel;
import org.apache.log4j.Logger;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 3/12/15
 * Time: 12:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class SparrowSMSUtil {
    private static final String PUSH_SMS_URL = "http://api.sparrowsms.com/v2/sms/";

    private static final String CREDITS_URL = "http://api.sparrowsms.com/v2/credit/";

    private static final Logger log = Logger.getLogger(SparrowSMSUtil.class);


    public static SparrowResultModel sendSMS(String text, String to) throws Exception {
        log.info("++++++ Sending SMS to " + to + " +++++++++++");
        //RestTemplate restTemplate = new RestTemplate();
        String url = PUSH_SMS_URL + prepareParameter(to, text);
        SparrowResultModel sparrowResultModel = new SparrowResultModel();
        try{

            RestTemplate restTemplate = new RestTemplate();
            sparrowResultModel = restTemplate.getForObject(url, SparrowResultModel.class);

        } catch (RestClientException re) {
            if (re instanceof HttpClientErrorException) {
                HttpClientErrorException he = (HttpClientErrorException) re;
                log.info(he.getResponseBodyAsString());
                ObjectMapper objectMapper  = new ObjectMapper();
                sparrowResultModel = objectMapper.readValue(he.getResponseBodyAsString(), SparrowResultModel.class);
                throw new RuntimeException(sparrowResultModel.getResponse());

            } else if (re instanceof HttpServerErrorException) {
                HttpServerErrorException he = (HttpServerErrorException) re;
                ObjectMapper objectMapper  = new ObjectMapper();
                sparrowResultModel = objectMapper.readValue(he.getResponseBodyAsString(), SparrowResultModel.class);
                log.info(he.getResponseBodyAsString());
                throw new RuntimeException(sparrowResultModel.getResponse());

            }
        } catch (Exception e) {

            throw new YSException("SEC013");
        }


        return sparrowResultModel;
    }

    private static String prepareParameter(String to, String text) {
        String param = "?";
        param += "from=" + MessageBundle.getSMSFrom();
        param += "&to=" + to;
        param += "&text=" + text;
        param += "&token=" + MessageBundle.getSMSToken();
        return param;
    }

    public static SparrowResultModel getSMSCredits() throws Exception{
        log.info("++++++++ Getting Sparrow SMS Credits +++++++++");
        //RestTemplate restTemplate = new RestTemplate();
        String url = CREDITS_URL + "?token="+ MessageBundle.getSMSToken();

        SparrowResultModel sparrowResultModel = new SparrowResultModel();
        try{
            RestTemplate restTemplate = new RestTemplate();
            sparrowResultModel = restTemplate.getForObject(url, SparrowResultModel.class);
        } catch (RestClientException re) {
            if (re instanceof HttpClientErrorException) {
                HttpClientErrorException he = (HttpClientErrorException) re;
                log.info(he.getResponseBodyAsString());
                ObjectMapper objectMapper  = new ObjectMapper();
                sparrowResultModel = objectMapper.readValue(he.getResponseBodyAsString(), SparrowResultModel.class);
                throw new RuntimeException(sparrowResultModel.getResponse());

            } else if (re instanceof HttpServerErrorException) {
                HttpServerErrorException he = (HttpServerErrorException) re;
                ObjectMapper objectMapper  = new ObjectMapper();
                sparrowResultModel = objectMapper.readValue(he.getResponseBodyAsString(), SparrowResultModel.class);
                log.info(he.getResponseBodyAsString());
                throw new RuntimeException(sparrowResultModel.getResponse());

            }
        } catch (Exception e) {

            throw new YSException("SEC013");
        }
        return sparrowResultModel;
    }



    public static void main(String[] args) {
        try {
            SparrowResultModel resultModel = sendSMS("9841531001", "This Is Test SMS from Suren");
            System.out.println(resultModel.getResponse());

        } catch (Exception e){
             e.printStackTrace();
        }

    }
}
