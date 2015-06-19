package com.yetistep.delivr.util;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 3/3/14
 * Time: 2:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class DateUtil {
    public static Date getCurrentDate() {
        long time = System.currentTimeMillis();
        return new Date(time);
    }

    public static Date getYesterday(){
        return addDay(-1, getCurrentDate());
    }

    public static Date addDay(int days, Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        calendar.add(Calendar.DATE, days);

        return new Date(calendar.getTimeInMillis());
    }

    public static Date addMonth(int months, Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        calendar.add(Calendar.MONTH, months);

        return new Date(calendar.getTimeInMillis());
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

        return new Timestamp(calendar.getTimeInMillis());
    }

    public static Timestamp addMinute(Timestamp timestamp, int min) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp.getTime());
        calendar.add(Calendar.MINUTE, min);
        return new Timestamp(calendar.getTimeInMillis());
    }

    public static Timestamp addSeconds(Timestamp timestamp, int seconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp.getTime());
        calendar.add(Calendar.SECOND, seconds);
        return new Timestamp(calendar.getTimeInMillis());
    }

    public static Timestamp subtractSeconds(Timestamp timestamp, int seconds) {
        Long givenTimeStamp = timestamp.getTime();
        givenTimeStamp = givenTimeStamp - seconds * 1000;
        return new Timestamp(givenTimeStamp);
    }

    public  static Timestamp getCurrentTimestampSQL(){
        return new Timestamp(System.currentTimeMillis());
    }


    public static Date stringToSQLDate(String dateStr) {
        try{
            return Date.valueOf(dateStr);
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
        String reg = "^([0-1][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$";
        if (initialTime.matches(reg) && finalTime.matches(reg) && currentTime.matches(reg)) {
            boolean valid = false;
            // Start Time
            java.util.Date inTime = new SimpleDateFormat("HH:mm:ss")
                    .parse(initialTime);
            Calendar calendarOpenTime = Calendar.getInstance();
            calendarOpenTime.setTime(inTime);

            // Current Time
            java.util.Date checkTime = new SimpleDateFormat("HH:mm:ss")
                    .parse(currentTime);
            Calendar calendarCurrentTime = Calendar.getInstance();
            calendarCurrentTime.setTime(checkTime);

            // End Time
            java.util.Date finTime = new SimpleDateFormat("HH:mm:ss")
                    .parse(finalTime);
            Calendar calendarCloseTime = Calendar.getInstance();
            calendarCloseTime.setTime(finTime);

            if (calendarCloseTime.compareTo(calendarOpenTime) <= 0) {
                calendarCloseTime.add(Calendar.DATE, 1);
                if (calendarOpenTime.compareTo(calendarCurrentTime) >= 0) {
                    calendarCurrentTime.add(Calendar.DATE, 1);
                }
            }


            if ((calendarCurrentTime.compareTo(calendarOpenTime) >= 0) && (calendarCurrentTime.compareTo(calendarCloseTime) <= 0)) {
                valid = true;
            }
            return valid;

        } else {
            throw new YSException("VLD013");
        }

    }

    public static String formatDate(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    public static Integer findDelayDifference(Timestamp time, int delayInSeconds){
        Long delay = addSeconds(time, delayInSeconds).getTime() - System.currentTimeMillis();
        return delay.intValue();
    }

    public  static Timestamp getTimestampLessThanNMinutes(int minutes){
        Long currentMillis = System.currentTimeMillis();
        currentMillis = currentMillis - minutes * 60 * 1000;
        return new Timestamp(currentMillis);
    }
}
