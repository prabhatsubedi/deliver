package com.yetistep.delivr.util;


public class YSException extends  RuntimeException {
    private static final long serialVersionUID = 1L;
    public String errorCode;
    public String message;



    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }



    public YSException(String errorCode, String exceptionCause) {
        super(errorCode);
        this.errorCode=errorCode;
        this.message= MessageBundle.getMessage(errorCode,"errorCodes.properties") + ". "+exceptionCause;
    }

    public YSException(String errorCode, Throwable e) {
        super(errorCode, e);
        this.errorCode=errorCode;
        this.message=e.getLocalizedMessage();
    }

    public YSException(String errorCode) {
        super(errorCode);
        this.errorCode=errorCode;
        this.message=MessageBundle.getMessage(errorCode,"errorCodes.properties");
    }




}
