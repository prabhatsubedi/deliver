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
    private static final String SMS_TOKEN = "2L9pXlF2vCSXE4MwNHVP1"; //Valid For March 11, 2016
    private static final String SMS_FROM = "5455";
    private static final String PUSH_SMS_URL = "http://api.sparrowsms.com/v2/sms/";

    public static SparrowResultModel getSendSMS(String to, String text) {
        try{
//        RestTemplate restTemplate = new RestTemplate();
        //SparrowResultModel sparrowResultModel = restTemplate.getForObject(PUSH_SMS_URL + prepareParameter(to, text), SparrowResultModel.class);
        HttpClient client = HttpClientBuilder.create().build();
        String url = PUSH_SMS_URL;
        url = url+ (URLEncoder.encode(prepareParameter(to, text), "UTF-8").replace("+", "%20"));

        HttpGet request = new HttpGet(url);

        HttpResponse response = client.execute(request);

            System.out.println("Response Code : "
                    + response.getStatusLine().getStatusCode());

            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
//        System.out.println(body);
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
