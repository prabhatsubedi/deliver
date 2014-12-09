package com.yetistep.delivr.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.lang.reflect.InvocationTargetException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 11/4/14
 * Time: 5:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class ServiceResponse {
    private Boolean success;
    private String message;
    private String errorCode;
    private Map<String, Object> params;

    public ServiceResponse(String message) {
        this(true, message);
    }

    public ServiceResponse(Boolean success, String message) {
        this(success, message, null);
    }


    public ServiceResponse(Boolean success, String message, String errorCode) {
        this.success = success;
        this.message = message;
        this.errorCode = errorCode;
        this.params = new HashMap<String, Object>();
    }

    public void addParam(String key, Object value){
        params.put(key, value);
    }


    public static HttpHeaders generateApplicationErrors(YSException e){
        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.add("errorCode", e.getErrorCode());
        httpHeaders.add("errorMessage", MessageBundle.getMessage(e.errorCode, "errorCodes.properties"));
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public static HttpHeaders generateRuntimeErrors(Exception e) {
        String statusCode = "ERR001";
        String message = MessageBundle.getMessage(statusCode, "errorCodes.properties");

//        if(e instanceof JsonParseException || e instanceof JsonSyntaxException) {
//            statusCode = "RS201";
//            message = MessageBundle.getMessage(statusCode, "errorCodes.properties")+ e.getMessage();
//        }
        if(e instanceof YSException) {
            statusCode = ((YSException) e).getErrorCode();
            message =  e.getMessage();
        }  else if(e instanceof GeneralSecurityException) {
            statusCode = "DEC001";
            message = MessageBundle.getMessage(statusCode, "errorCodes.properties");
        } else if(e instanceof InvocationTargetException) {
            statusCode = "RS601";
            message = MessageBundle.getMessage(statusCode, "errorCodes.properties");
        } else if (e instanceof  NoSuchMethodException) {
            statusCode = "RS002";
            message = MessageBundle.getMessage(statusCode, "errorCodes.properties");
        } else if (e instanceof Exception) {
            //General Exception During Development Period Only
            //In Production period this catch block should be commented
            statusCode = "RUN001";
            message = e.getMessage();
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("errorCode", statusCode);
        httpHeaders.add("errorMessage", message);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        return httpHeaders;
    }
}
