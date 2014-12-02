package com.yetistep.delivr.util;

/**
 * Created with IntelliJ IDEA.
 * User: yetistep
 * Date: 12/18/13
 * Time: 5:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class DisplaySize {
    public static final String XHDPI    =   "xhdpi";  //original is 720,1280 as uploaded.
    public static final String HDPI     =   "hdpi";  //original is 480, 800: to maintain ratio 480, 854
    public static final String MDPI     =   "mdpi";  //original is 320, 480: to maintain ratio 320, 569
    public static final String LDPI     =   "ldpi";  //original is 240, 320: to maintain ratio 240, 427

    private String name;
    private int width;
    private int height;
    private int dpi;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getDpi() {
        return dpi;
    }

    public void setDpi(int dpi) {
        this.dpi = dpi;
    }

    public String toString(){
        return this.name+"-"+this.dpi;
    }
}
