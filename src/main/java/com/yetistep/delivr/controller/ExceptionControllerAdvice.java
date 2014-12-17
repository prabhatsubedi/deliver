package com.yetistep.delivr.controller;

import com.yetistep.delivr.util.GeneralUtil;
import com.yetistep.delivr.util.ServiceResponse;
import com.yetistep.delivr.util.YSException;
import org.apache.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 12/16/14
 * Time: 4:28 PM
 * To change this template use File | Settings | File Templates.
 */
@ControllerAdvice
public class ExceptionControllerAdvice {
    private Logger log = Logger.getLogger(ExceptionControllerAdvice.class);

    @ExceptionHandler({HttpMessageNotReadableException.class})
    @ResponseBody
    public ResponseEntity<ServiceResponse> resolveException(HttpMessageNotReadableException h) {
        try {
            GeneralUtil.logError(log, "Request JSON error (Either Blank or Improperly formatted)", h);

            throw new YSException("JSN001");

        } catch (Exception e) {
            GeneralUtil.logError(log, "Error occurred while parsing JSON", e);
            HttpHeaders headers = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(headers, HttpStatus.BAD_REQUEST);
        }

    }
}
