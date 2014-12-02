package com.yetistep.delivr.util;

import com.yetistep.delivr.util.YSException;
import org.apache.log4j.Logger;

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

}
