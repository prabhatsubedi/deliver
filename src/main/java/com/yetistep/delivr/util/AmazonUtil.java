package com.yetistep.delivr.util;


import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import org.apache.log4j.Logger;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: yetistep
 * Date: 1/2/14
 * Time: 4:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class AmazonUtil {
    public static final Logger log = Logger.getLogger(AmazonUtil.class);
    private static AWSCredentials credentials;
    private static AmazonS3 amazonS3;
    private static final String CLOUD_FRONT = System.getProperty("DELIVR_CLOUD_FRONT");

    public static final String S3_ENDPOINT = "s3.amazonaws.com";
    public static final String BUCKET = System.getProperty("DELIVR_S3_BUCKET");

    public static AWSCredentials getCredentials(){
        if(credentials==null){
            String accessKey = MessageBundle.getMessage("accessKey", "AwsCredentials.properties");
            String secretKey = MessageBundle.getMessage("secretKey", "AwsCredentials.properties");

            credentials = new BasicAWSCredentials(accessKey, secretKey);
        }

        return credentials;
    }


    public static AmazonS3 getAmazonS3(){
        if(amazonS3==null){
            amazonS3 = new AmazonS3Client(getCredentials());
            amazonS3.setRegion(Region.getRegion(Regions.AP_SOUTHEAST_1));
            amazonS3.setEndpoint(S3_ENDPOINT);
        }
        return amazonS3;
    }


    //desc is the folder path
    public static String uploadFileToS3(File file, String path, String filename, boolean deleteOriginal) throws InterruptedException {
        log.info("Uploading file "+filename+" to bucket... ");
        TransferManager tx = new TransferManager(getCredentials());
        String bucketUrl = null;

        try {
            String key = filename;
            if(path!=null && !path.equals(""))
                key = path + filename;

            final Upload upload = tx.upload(BUCKET, key, file);

//            final ProgressListener progressListener = new ProgressListener() {
//                // This method is called periodically as your transfer progresses
//                public void progressChanged(ProgressEvent progressEvent) {
//                    log.info(upload.getDescription() + " [" + upload.getState() + " :" + upload.getProgress().getPercentTransferred() + "%]");
//
//                    if (progressEvent.getEventCode() == ProgressEvent.COMPLETED_EVENT_CODE) {
//                        log.info(upload.getDescription() + " Upload complete!!! with file size " +upload.getProgress().getTotalBytesToTransfer() +"["+ upload.getProgress().getBytesTransferred()+"]");
//                        if (upload.getProgress().getTotalBytesToTransfer() !=upload.getProgress().getBytesTransferred()) {
//                            log.error("Failed to upload image completely. Trying for second time");
//                        }
//
//                    }
//                }
//            };
//
//            upload.addProgressListener(progressListener);

            upload.waitForCompletion();
            log.info("File "+filename+" successfully uploaded to bucket");
//            upload.removeProgressListener(progressListener);

            if(deleteOriginal) file.delete();

            getAmazonS3().setObjectAcl(BUCKET, key, CannedAccessControlList.PublicRead);
            bucketUrl = BUCKET+"."+S3_ENDPOINT+"/"+key;
        } finally {
            tx.shutdownNow();
        }


        return bucketUrl;
    }

    public static String cacheImage(String url){
        String bucketUrl = BUCKET+"."+S3_ENDPOINT;
        //return url.replaceAll(bucketUrl,MessageBundle.getMessage("cloud_front", "constants.properties"));
        return url.replaceAll(bucketUrl, CLOUD_FRONT);

    }

    public static void deleteFileFromBucket(String key) throws AmazonClientException, AmazonServiceException{
        log.info("Deleting "+key+" from S3 Bucket");
        //Key containing directory/directory/file
        getAmazonS3().deleteObject(BUCKET, key);
    }

   public static void deleteObjectsInFolder(String folderPath) {
        if(folderPath==null || folderPath.equals(""))
            throw new RuntimeException("BUCKET FolderPath cannot be empty");

        for (S3ObjectSummary file : getAmazonS3().listObjects(BUCKET, folderPath).getObjectSummaries()){
            getAmazonS3().deleteObject(BUCKET, file.getKey());
        }

    }
}
