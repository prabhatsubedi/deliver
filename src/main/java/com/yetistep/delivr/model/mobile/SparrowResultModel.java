package com.yetistep.delivr.model.mobile;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 3/12/15
 * Time: 1:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class SparrowResultModel {
    private String count;
    @JsonProperty("response_code")
    private String responseCode;
    private String response;

    //For Credit
    @JsonProperty("credits_available")
    private String creditsAvailable;
    @JsonProperty("credits_consumed")
    private String creditsConsumed;

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }


    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getCreditsAvailable() {
        return creditsAvailable;
    }

    public void setCreditsAvailable(String creditsAvailable) {
        this.creditsAvailable = creditsAvailable;
    }

    public String getCreditsConsumed() {
        return creditsConsumed;
    }

    public void setCreditsConsumed(String creditsConsumed) {
        this.creditsConsumed = creditsConsumed;
    }
}
