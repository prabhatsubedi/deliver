package com.yetistep.delivr.util;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLEncoder;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 3/12/15
 * Time: 12:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class SparrowSMSUtil {
    private static final String SMS_TOKEN = "2L9pXlF2vCSXE4MwNHVP"; //Valid For March 11, 2016
    private static final String SMS_FROM = "Demo";
    private static final String PUSH_SMS_URL = "http://api.sparrowsms.com/v2/sms/";

    public static SparrowResultModel getSendSMS(String to, String text) {
        try{
        RestTemplate restTemplate = new RestTemplate();

        String url = PUSH_SMS_URL + prepareParameter(to, text);
        SparrowResultModel sparrowResultModel = restTemplate.getForObject(url, SparrowResultModel.class);
        System.out.println("Successfully send sms");


        } catch (Exception e){
            e.printStackTrace();
        }
        return new SparrowResultModel();
    }

    private static String prepareParameter(String to, String text) {
        String param = "?";
        param += "from=" + SMS_FROM;
        param += "&to=" + to;
        param += "&text=" + text;
        param += "&token=" + SMS_TOKEN;
        return param;
    }

    public static void main(String[] args) {

        SparrowResultModel resultModel = getSendSMS("9841531001", "This Is Test SMS from Suren");
        System.out.println(resultModel.getResponse());
    }
}
