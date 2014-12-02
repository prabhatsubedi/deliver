package com.yetistep.delivr.util;

import org.apache.log4j.Logger;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.*;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * Created with IntelliJ IDEA.
 * User: golcha
 * Date: 9/24/13
 * Time: 10:25 AM
 * To change this template use File | Settings | File Templates.
 */
public class ImageConverter {

    public static final Logger log = Logger.getLogger(ImageConverter.class);
    //private static   String  root_path = "/Volumes/Golcha/deal/uploads";

    public static String convertToWebp(File file) {
        String s = null;
        String output_file = null;
        try {
            StringTokenizer stringTokenizer = new StringTokenizer(file.getName(), ".");
            String name = stringTokenizer.nextToken();


            output_file = file.getParentFile().getAbsolutePath() + "/" + name + ".webp";

            //String command = "C:\\libwebp-0.3.1-windows-x86\\cwebp " + source_file + " -noalpha -o " + output_file;
            String command = "cwebp " + file.getPath() + " -noalpha -o " + output_file;
            log.info("command: " + command);


            // run the Unix "ps -ef" command
            // using the Runtime exec method:
            // Process p = Runtime.getRuntime().exec("cwebp /Users/golcha/Dropbox/yetistep/swipr/ads/uploaded_ads/14.png -noalpha -o /Users/golcha/Dropbox/yetistep/swipr/ads/uploaded_ads/14.webp");

            Process p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(p.getErrorStream()));

            // read the output from the command
            log.debug("Here is the standard output of the command:\n");
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

            // read any errors from the attempted command
            log.debug("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                log.debug(s);
            }
            stdInput.close();
            stdError.close();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            throw new YSException("RS110", e);
        }

        return output_file;
    }

    public static String convertToJpg(File file) {
        String s = null;
        String output_file = null;
        try {
            StringTokenizer stringTokenizer = new StringTokenizer(file.getName(), ".");
            String name = stringTokenizer.nextToken();
            output_file = file.getParentFile().getAbsolutePath() + "/" + name + "dq.jpg";

            String command = "convert " + file.getPath() + " -quality 50% " + output_file;
            log.info("command: " + command);
            // run the Unix "ps -ef" command
            // using the Runtime exec method:
            // Process p = Runtime.getRuntime().exec("cwebp /Users/golcha/Dropbox/yetistep/swipr/ads/uploaded_ads/14.png -noalpha -o /Users/golcha/Dropbox/yetistep/swipr/ads/uploaded_ads/14.webp");
            Process p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(p.getErrorStream()));

            // read the output from the command
            log.debug("Here is the standard output of the command:\n");
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

            // read any errors from the attempted command
            log.debug("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                log.debug(s);
            }
            stdInput.close();
            stdError.close();

        } catch (Exception e) {
            System.out.println("exception happened - here's what I know: ");
            e.printStackTrace();
            throw new YSException("RS111", e);
        }

        return output_file;
    }

    //compress the provided image and save in the provided desctination
    public static String compressImageNSave(File imageFile) throws Exception {
        float quality = 0.5f;
        StringTokenizer stringTokenizer = new StringTokenizer(imageFile.getName(), ".");
        String name = stringTokenizer.nextToken();
        String desc = imageFile.getParentFile().getAbsolutePath() + File.separator + name + "_" + DisplaySize.XHDPI + ".jpg";
        BufferedImage originalImage = ImageIO.read(imageFile);

        Iterator iter = ImageIO.getImageWritersByFormatName("jpeg");

        ImageWriter writer = (ImageWriter) iter.next();

        ImageWriteParam iwp = writer.getDefaultWriteParam();
        iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        iwp.setCompressionQuality(quality);

        File file = new File(desc);
        writer.setOutput(new FileImageOutputStream(file));

        IIOImage image = new IIOImage(originalImage, null, null);

        writer.write(null, image, iwp);
        writer.dispose();

        log.info("Successfully compressed and saved image to " + desc);
        return desc;
    }

    //resizes and saves the image in the provided destination
    public static File resizeImageNSave(File image, int width, int height, String desc) throws IOException {
        BufferedImage originalImage = ImageIO.read(image);
        int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();

        BufferedImage resizedImage = new BufferedImage(width, height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();

        File newImage = new File(desc);
        ImageIO.write(resizedImage, "jpg", newImage);
        return newImage;
    }

    //compress the provided image and saves.
    //[compressor creates JPG images]
    public static BufferedImage compressImage(File imageFile, String imgType, String imageName, float quality) throws Exception {
        //float quality = 0.5f;

        BufferedImage originalImage = ImageIO.read(imageFile);

        //convert to jpg image if png
        if (imgType.equalsIgnoreCase("png")) {
            BufferedImage newBufferedImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);
            newBufferedImage.createGraphics().drawImage(originalImage, 0, 0, Color.WHITE, null);
            originalImage = newBufferedImage;
        }

        //for jpg image
        Iterator iter = ImageIO.getImageWritersByFormatName("jpg");

        ImageWriter writer = (ImageWriter) iter.next();

        ImageWriteParam iwp = writer.getDefaultWriteParam();
        iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        iwp.setCompressionQuality(quality);

//        imgType = imgType.equalsIgnoreCase("png")? "jpg": imgType;
        imgType = "jpg";
        String filePath = imageFile.getParentFile().getAbsolutePath() + File.separator + imageName + "." + imgType;

        File file = new File(filePath);
        FileImageOutputStream fileImageOutputStream = new FileImageOutputStream(file);
        writer.setOutput(fileImageOutputStream);

        IIOImage image = new IIOImage(originalImage, null, null);

        writer.write(null, image, iwp);
        fileImageOutputStream.close();
        writer.dispose();

        log.info("Successfully compressed and saved image to " + filePath);
        return originalImage;
    }

    //resizes and saves the image in the provided destination
    public static BufferedImage resizeImage(BufferedImage originalImage, int width, int height) throws IOException {
        // BufferedImage originalImage = ImageIO.read(image);
        int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();

        BufferedImage resizedImage = new BufferedImage(width, height, type);

        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();

        // File newImage = new File(desc);
        //  ImageIO.write(resizedImage, "jpg", newImage);
        return resizedImage;
    }

    // crops image
    public static BufferedImage cropImage(BufferedImage img, int cropWidth,
                                          int cropHeight, int cropStartX, int cropStartY) throws Exception {
        BufferedImage clipped = null;
        Dimension size = new Dimension(cropWidth, cropHeight);

        checkClipArea(img, size, cropStartX, cropStartY);

        clipped = img.getSubimage(cropStartX, cropStartY, cropWidth, cropHeight);

        try {
            clipped = img.getSubimage(cropStartX, cropStartY, cropWidth, cropHeight);
        } catch (RasterFormatException rfe) {
            log.error("Error occurred while cropping an image", rfe);
            throw rfe;
        }
        return clipped;
    }

    public static void decodeToImage(String encodedString, File newImage) throws IOException {
        int start = encodedString.indexOf(",");
        encodedString = encodedString.substring(start + 1);//remove data:image/png;base64

        byte[] btDataFile = new sun.misc.BASE64Decoder().decodeBuffer(encodedString);
        FileOutputStream osf = new FileOutputStream(newImage);
        osf.write(btDataFile);
        osf.flush();
        osf.close();
    }

    //checks if the given range to clip image lies within the image boundary.
    private static void checkClipArea(BufferedImage img, Dimension size,
                                      int clipX, int clipY) throws Exception {

        clipX = clipX < 0 ? 0 : clipX;//if negative, set it to zero
        clipY = clipY < 0 ? 0 : clipY;//if negative, set it to zero

        int clippableWidth = size.width + clipX;
        int clippableHeight = size.height + clipY;

        boolean isClipOutOfImg = clippableWidth > img.getWidth()
                || clippableHeight > img.getHeight();

        if (isClipOutOfImg)
            throw new Exception("Trying to clip outside the image boundary.Error!!!");
    }
}
