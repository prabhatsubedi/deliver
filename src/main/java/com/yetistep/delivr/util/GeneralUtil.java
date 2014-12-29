package com.yetistep.delivr.util;

import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.enums.PreferenceType;
import com.yetistep.delivr.enums.Role;
import com.yetistep.delivr.enums.VehicleType;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 11/19/14
 * Time: 11:37 AM
 * To change this template use File | Settings | File Templates.
 */
public class GeneralUtil {
    @Autowired
    HttpServletRequest httpServletRequest;

    private static Logger log = Logger.getLogger(GeneralUtil.class);

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String VERIFICATION_CODE = "verificationCode";
    public static final String NEW_PASSWORD = "newPassword";
    public static final String ID = "id";
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String MERCHANT_ID = "merchantId";

    public static void logError(Logger log, String message, Exception e) {
        if (e instanceof YSException)
            log.info(e.getMessage());
        else
            log.error(message, e);
    }

    public static String saveImageToBucket(String encodedString, String imageName, String dir, boolean doCompress) throws Exception {
        if (encodedString == null || encodedString.isEmpty())
            throw new YSException("VLD008");

        String imgType = encodedString.split(";")[0].split("/")[1];
        String tmpDir = System.getProperty("catalina.home") + File.separator + "temp";
        File tmpImg = new File(tmpDir, imageName + "." + imgType);

        //decode the string and save the image
        ImageConverter.decodeToImage(encodedString, tmpImg);
        log.info("Image Saved locally in: " + tmpImg.getPath());

        String imgUrl = null;

        if (doCompress) {
            String compressImgName = imageName;
            ImageConverter.compressImage(tmpImg, imgType, compressImgName, 0.7f);

            compressImgName += ".jpg";
            File compressedFile = new File(tmpDir + File.separator + compressImgName);
            log.info("Compressed and saved in " + compressedFile.getPath());

            //upload compressed image to the bucket
            log.info("Uploading coupon compress image to bucket");
            String compressUrl = AmazonUtil.uploadFileToS3(compressedFile, dir, compressedFile.getName(), true);
            imgUrl = AmazonUtil.cacheImage(compressUrl);
            return imgUrl;
        } else {
            //upload the original image to bucket
            String s3Url = AmazonUtil.uploadFileToS3(tmpImg, dir, tmpImg.getName(), true);
            return imgUrl == null ? AmazonUtil.cacheImage(s3Url) : imgUrl;
        }

    }

    /**
     * Util method to get data from HttpHeaders
     * @param headers
     * @param headerDto
     */
    public static void fillHeaderCredential(HttpHeaders headers, HeaderDto headerDto, String... expectedFields) throws Exception {
        for(String field : expectedFields){
            if (field.equals(USERNAME)){
               headerDto.setUsername(extractHeader(headers, USERNAME));
            }else if(field.equals(PASSWORD)){
               headerDto.setPassword(extractHeader(headers, PASSWORD));
            }else if(field.equals(VERIFICATION_CODE)){
                headerDto.setVerificationCode(extractHeader(headers, VERIFICATION_CODE));
            }else if(field.equals(NEW_PASSWORD)){
                headerDto.setNewPassword(extractHeader(headers, NEW_PASSWORD));
            }else if(field.equals(ID)){
                headerDto.setId(extractHeader(headers, ID));
            }else if(field.equals(ACCESS_TOKEN)){
                headerDto.setAccessToken(extractHeader(headers, ACCESS_TOKEN));
            }else if(field.equals(MERCHANT_ID)){
                if(SessionManager.getRole().toString().equals(Role.ROLE_MERCHANT.toString())){
                    headerDto.setMerchantId(SessionManager.getMerchantId());
                } else{
                    headerDto.setMerchantId(Integer.parseInt(extractHeader(headers, MERCHANT_ID)));
                }
            }
        }
    }

    public static String extractHeader(HttpHeaders headers, String field) throws Exception{
        List<String> hd = headers.get(field);
        if (hd != null && hd.size() > 0)
            return hd.get(0);
        else
            throw new YSException("VLD009");
    }

    /**
     * Util method to encrypt the password.
     * @param password
     * @return
     */
    public static String encryptPassword(String password){
        if(password != null && !password.isEmpty()){
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            return passwordEncoder.encode(password);
        }
        return null;
    }

    public static Boolean matchDBPassword(String rawPassword, String dbEncryptedPassword) throws Exception {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if(!passwordEncoder.matches(rawPassword, dbEncryptedPassword))
            throw new YSException("ERR014");

        return true;
    }



    public static String generateAccessToken(String osFamily) throws Exception {
        String tokenstr = null;
        if (osFamily.toUpperCase().equals("IOS")) {
            tokenstr = RNCryptoEncDec.generateResponseAccessToken();
        } else {
            tokenstr = EncDecUtil.generateResponseAccessToken(MessageBundle.getSecretKey());
        }
        return tokenstr;
    }

    /*   Generates 4 digit code   */
    public static String generateMobileCode(){
        int code = (int) (Math.random()*9000+1000);
        return String.valueOf(code);
    }

    public static PreferenceType getTimeTakenFor(VehicleType vehicleType){
        PreferenceType preferenceType = null;
        if(VehicleType.BICYCLE.equals(vehicleType)){
            preferenceType = PreferenceType.TIME_TO_TRAVEL_ONE_KM_ON_BICYCLE;
        }else if(VehicleType.ON_FOOT.equals(vehicleType)){
            preferenceType = PreferenceType.TIME_TO_TRAVEL_ONE_KM_ON_FOOT;
        }else if(VehicleType.MOTORBIKE.equals(vehicleType)){
            preferenceType = PreferenceType.TIME_TO_TRAVEL_ONE_KM_ON_MOTORBIKE;
        }else if(VehicleType.CAR.equals(vehicleType)){
            preferenceType = PreferenceType.TIME_TO_TRAVEL_ONE_KM_ON_CAR;
        }else if(VehicleType.TRUCK.equals(vehicleType)){
            preferenceType = PreferenceType.TIME_TO_TRAVEL_ONE_KM_ON_TRUCK;
        }else if(VehicleType.OTHERS.equals(vehicleType)){
            preferenceType = PreferenceType.TIME_TO_TRAVEL_ONE_KM_ON_OTHERS;
        }
        return preferenceType;
    }

}
