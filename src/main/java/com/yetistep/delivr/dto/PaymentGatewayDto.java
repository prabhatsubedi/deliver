package com.yetistep.delivr.dto;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 4/20/15
 * Time: 12:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class PaymentGatewayDto {
    private String Data;
    private String Seal;
    private String InterfaceVersion;
    private String PGRequestURL;

    public String getData() {
        return Data;
    }

    public void setData(String data) {
        Data = data;
    }

    public String getSeal() {
        return Seal;
    }

    public void setSeal(String seal) {
        Seal = seal;
    }

    public String getInterfaceVersion() {
        return InterfaceVersion;
    }

    public void setInterfaceVersion(String interfaceVersion) {
        InterfaceVersion = interfaceVersion;
    }

    public String getPGRequestURL() {
        return PGRequestURL;
    }

    public void setPGRequestURL(String PGRequestURL) {
        this.PGRequestURL = PGRequestURL;
    }
}
