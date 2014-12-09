package com.yetistep.delivr.util;

import com.yetistep.delivr.dto.HeaderDto;
import org.apache.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 11/19/14
 * Time: 11:37 AM
 * To change this template use File | Settings | File Templates.
 */
public class GeneralUtil {
    private static Logger log = Logger.getLogger(GeneralUtil.class);

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String VERIFICATION_CODE = "verificationCode";
    private static final String NEW_PASSWORD = "newPassword";
    private static final String ID = "id";
    private static final String ACCESS_TOKEN = "id";

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
    public static void fillHeaderCredential(HttpHeaders headers, HeaderDto headerDto) {
        String username = null;
        String password = null;
        String verificationCode = null;
        String newPassword = null;
        String id = null;
        String accessToken = null;
        List<String> hd = headers.get(USERNAME);
        if (hd != null && hd.size() > 0)
            username = hd.get(0);

        hd = headers.get(PASSWORD);
        if (hd != null && hd.size() > 0)
            password = hd.get(0);

        hd = headers.get(VERIFICATION_CODE);
        if(hd !=null && hd.size() > 0)
            verificationCode = hd.get(0);

        hd = headers.get(NEW_PASSWORD);
        if(hd !=null && hd.size() > 0)
            newPassword = hd.get(0);

        hd = headers.get(ID);
        if(hd !=null && hd.size() > 0)
            id = hd.get(0);

        hd = headers.get(ACCESS_TOKEN);
        if(hd !=null && hd.size() > 0)
            accessToken = hd.get(0);

        headerDto.setUsername(username);
        headerDto.setPassword(password);
        headerDto.setVerificationCode(verificationCode);
        headerDto.setNewPassword(newPassword);
        headerDto.setId(id);
        headerDto.setAccessToken(accessToken);
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

}
