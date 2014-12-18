package com.yetistep.delivr.util;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: yetistep
 * Date: 3/3/14
 * Time: 2:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class DateUtil {
    public static Date getCurrentDate() {
        long time = System.currentTimeMillis();
        Date date = new Date(time);
        return date;
    }

    public static Date getYesterday(){
        return addDay(-1, getCurrentDate());
    }

    public static Date addDay(int days, Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        calendar.add(Calendar.DATE, days);

        Date newDate = new Date(calendar.getTimeInMillis());
        return newDate;
    }

    public static Date addMonth(int months, Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        calendar.add(Calendar.MONTH, months);

        Date newDate = new Date(calendar.getTimeInMillis());
        return newDate;
    }

    public static int findAge(Date date) {
        if (date == null) throw new YSException("RS704");

        Calendar dob = Calendar.getInstance();
        dob.setTime(date);

        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) age--;

        return age;
    }

    public static Timestamp addHour(int hour, Timestamp timestamp){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp.getTime());
        calendar.add(Calendar.HOUR_OF_DAY, hour);

        Timestamp newTimeStamp = new Timestamp(calendar.getTimeInMillis());
        return newTimeStamp;
    }

    public static Timestamp addMinute(Timestamp timestamp, int min) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp.getTime());
        calendar.add(Calendar.MINUTE, min);
        Timestamp newTimeStamp = new Timestamp(calendar.getTimeInMillis());
        return newTimeStamp;
    }

    public  static Timestamp getCurrentTimestampSQL(){
        return new Timestamp(System.currentTimeMillis());
    }


    public static Date stringToSQLDate(String dateStr) {
        try{
            Date date = Date.valueOf(dateStr);
            return date;
        }catch (Exception e) {
            throw new RuntimeException("Invalid argument ["+dateStr+"], cannot be parsed to date");
        }
    }

    public static double getDaysDiff(Date date1, Date date2) {
        return getDaysDiff(date1.getTime(), date2.getTime());
    }

    public static double getDaysDiff(long time1, long time2) {
        return Math.abs(getHourDiff(time1, time2)/24.0);
    }

    public static double getHourDiff(Date date1, Date date2){
        return getHourDiff(date1.getTime(), date2.getTime());
    }

    public static double getHourDiff(long time1, long time2){
        return Math.abs(getMinDiff(time1, time2)/60.0);
    }

    public static double getMinDiff(Date date1, Date date2){
        return getMinDiff(date1.getTime(), date2.getTime());
    }

    public static double getMinDiff(long time1, long time2){
        return Math.abs((time1 - time2)/(1000.0 * 60.0));
    }

    public static Calendar dateToCalendar(java.util.Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static Time getCurrentTime() {
        return new Time(System.currentTimeMillis());
    }

    public static Time formatTime(String arg) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("H:m:s");
            return new Time(sdf.parse(arg).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isTimeBetweenTwoTime(String initialTime, String finalTime, String currentTime) throws ParseException {

        boolean valid  = false;
        java.util.Date inTime = new SimpleDateFormat("HH:mm:ss").parse(initialTime);
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(inTime);

        java.util.Date finTime = new SimpleDateFormat("HH:mm:ss").parse(finalTime);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(finTime);
        calendar2.add(Calendar.DATE, 1);

        java.util.Date checkTime = new SimpleDateFormat("HH:mm:ss").parse(currentTime);
        Calendar calendar3 = Calendar.getInstance();
        calendar3.setTime(checkTime);
        calendar3.add(Calendar.DATE, 1);

        java.util.Date actualTime = calendar3.getTime();

        if((actualTime.after(calendar1.getTime()) || actualTime.compareTo(calendar1.getTime())==0) && actualTime.before(calendar2.getTime())){
            valid = true;
        }

        return valid;
    }

    public static String formatDate(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }
}
