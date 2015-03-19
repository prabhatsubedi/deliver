package com.yetistep.delivr.model.mobile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 3/12/15
 * Time: 1:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class SparrowResultModel {
    private String count;
    private String responseCode;
    private String response;
    private Integer creditsAvailable;
    private String creditsConsumed;

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    @JsonIgnore
    public String getResponseCode() {
        return responseCode;
    }

    @JsonProperty("response_code")
    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    @JsonProperty("responseCode")
    public String getResponseCd() {
        return responseCode;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    @JsonIgnore
    public Integer getCreditsAvailable() {
        return creditsAvailable;
    }
    @JsonProperty("credits_available")
    public void setCreditsAvailable(Integer creditsAvailable) {
        this.creditsAvailable = creditsAvailable;
    }
    @JsonProperty("creditsAvailable")
    public Integer getCreditsAvl() {
        return creditsAvailable;
    }

    @JsonIgnore
    public String getCreditsConsumed() {
        return creditsConsumed;
    }
    @JsonProperty("creditsConsumed")
    public String getCreditsCons() {
        return creditsConsumed;
    }
    @JsonProperty("credits_consumed")
    public void setCreditsConsumed(String creditsConsumed) {
        this.creditsConsumed = creditsConsumed;
    }
}
