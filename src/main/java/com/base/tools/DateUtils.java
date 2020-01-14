/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.base.tools;


import com.base.model.enumeration.publicenum.DateFormatEnum;
import org.junit.Test;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * DateUtils，时间操作类
 *
 * @author 司徒彬
 * @date 2016年10月27日13:50:56
 */
public class DateUtils extends org.apache.commons.lang.time.DateUtils {

    static {
        final TimeZone timeZone = TimeZone.getTimeZone("Asia/Shanghai");
        TimeZone.setDefault(timeZone);
    }

    /**
     * 获得当前时间的字符串 默认格式：yyyy-MM-dd HH:mm:ss
     *
     * @return the date string
     */
    public static String getDateString() {
        return getDateString(new Date(), null);
    }

    /**
     * 根据制指定的格式，获得当前时间的字符串
     *
     * @param dateFormatEnum the date format enum
     * @return the date string
     */
    public static String getDateString(DateFormatEnum dateFormatEnum) {
        return getDateString(new Date(), dateFormatEnum);
    }


    /**
     * 根据指定的格式，得到指定时间的字符串
     *
     * @param date           the date
     * @param dateFormatEnum the date format enum
     * @return the date string
     */
    public static String getDateString(Date date, DateFormatEnum dateFormatEnum) {
        if (date == null) {
            return null;
        }
        dateFormatEnum = dateFormatEnum == null ? DateFormatEnum.YYYY_MM_DD_HH_MM_SS : dateFormatEnum;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormatEnum.getValue());
        return simpleDateFormat.format(date);
    }

    public static String getDateString(Timestamp timestamp, DateFormatEnum dateFormatEnum) {
        if (timestamp == null) {
            return "";
        }
        Date date = new Date(timestamp.getTime());
        return getDateString(date, dateFormatEnum);
    }

    /**
     * 获得当前时间
     *
     * @return the date
     */
    public static Timestamp getNow() {
        return new Timestamp(System.currentTimeMillis());
    }

    /**
     * 根据指定的字符串获得时间
     *
     * @param dateStr 时间字符串 yyyy-MM-dd HH:mm:ss
     * @return
     * @throws ParseException
     */
    public static Timestamp getDate(String dateStr) throws ParseException {
        return getDate(dateStr, null);
    }

    /**
     * Gets date.
     *
     * @param dateStr        the date str
     * @param dateFormatEnum the date format enum
     * @return the date
     * @throws ParseException the parse exception
     */
    public static Timestamp getDate(String dateStr, DateFormatEnum dateFormatEnum) throws ParseException {
        if (StringUtils.isEmpty(dateStr)) {
            return null;
        }
        dateFormatEnum = dateFormatEnum == null ? DateFormatEnum.YYYY_MM_DD_HH_MM_SS : dateFormatEnum;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormatEnum.getValue());
        Date date = simpleDateFormat.parse(dateStr);
        return new Timestamp(date.getTime());
    }


    /**
     * 获取数据查询日期，格式是：2014-04-02，day是天差，如果传-1是昨天的日期
     *
     * @author：ErebusST @date：2017/4/25 14:19
     */
    public static Timestamp addDay(int day) {
        Calendar cal = Calendar.getInstance();

        cal.add(Calendar.DATE, day);
        Date date = cal.getTime();
        Timestamp currentTimestamp = new Timestamp(date.getTime());
        return currentTimestamp;
    }

    public static Timestamp addDay(Timestamp date, int day) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date.getTime());
        cal.add(Calendar.DATE, day);
        Date result = cal.getTime();
        Timestamp currentTimestamp = new Timestamp(result.getTime());
        return currentTimestamp;
    }


    public static Date getStartTime(int day) {
        Calendar todayStart = Calendar.getInstance();
        todayStart.add(Calendar.DATE, day);
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime();
    }

    public static Date getEndTime(int day) {
        Calendar todayEnd = Calendar.getInstance();

        todayEnd.add(Calendar.DATE, day);
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        return todayEnd.getTime();
    }

    /**
     * Gets start time in month.
     *
     * @param timestamp the timestamp
     * @return the start time in month
     */
    public static Timestamp getStartTimeInMonth(Timestamp timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp.getTime());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Timestamp time = new Timestamp(calendar.getTimeInMillis());
        return time;
    }

    /**
     * Gets last day in month.
     *
     * @param timestamp the timestamp
     * @return the last day in month
     */
    public static Timestamp getLastDayInMonth(Timestamp timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp.getTime());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        Timestamp time = new Timestamp(calendar.getTimeInMillis());
        return time;
    }


    public static String getStartTime(String specifiedDay, int day) {
        Calendar todayStart = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat(DateFormatEnum.YYYY_MM_DD.getValue()).parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        todayStart.setTime(date);
        todayStart.add(Calendar.DATE, day);
        todayStart.set(Calendar.HOUR, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        String datestr = new SimpleDateFormat(DateFormatEnum.YYYY_MM_DD_HH_MM_SS.getValue()).format(todayStart.getTime());
        return datestr;
    }

    public static String getEndTime(String specifiedDay, int day) {
        Calendar todayEnd = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat(DateFormatEnum.YYYY_MM_DD_HH_MM_SS.getValue()).parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        todayEnd.setTime(date);
        todayEnd.add(Calendar.DATE, day);
        todayEnd.set(Calendar.HOUR, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        String datestr = new SimpleDateFormat(DateFormatEnum.YYYY_MM_DD_HH_MM_SS.getValue()).format(todayEnd.getTime());
        return datestr;
    }

    /**
     * 获得指定日期的前一天
     *
     * @param specifiedDay
     * @return
     * @throws Exception
     */
    public static String getSpecifiedDayBefore(String specifiedDay, String dateFormat) {
        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat(dateFormat).parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day - 1);

        String dayBefore = new SimpleDateFormat(dateFormat).format(c.getTime());
        return dayBefore;
    }

    /**
     * 获得指定日期的后一天
     *
     * @param specifiedDay
     * @return
     */
    public static String getSpecifiedDayAfter(String specifiedDay, String dateFormat) {
        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat(dateFormat).parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day + 1);

        String dayAfter = new SimpleDateFormat(dateFormat).format(c.getTime());
        return dayAfter;
    }

    public static Timestamp addYear(Timestamp date, int year) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date.getTime());
        cal.add(Calendar.YEAR, year);
        Date result = cal.getTime();
        Timestamp currentTimestamp = new Timestamp(result.getTime());
        return currentTimestamp;
    }

    /**
     * 获取指定日期所在月份结束的时间戳
     *
     * @param date 指定日期
     * @return
     */
    public static String getMonthEnd(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        //设置为当月最后一天
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        //将小时至23
        c.set(Calendar.HOUR_OF_DAY, 23);
        //将分钟至59
        c.set(Calendar.MINUTE, 59);
        //将秒至59
        c.set(Calendar.SECOND, 59);
        //将毫秒至999
        c.set(Calendar.MILLISECOND, 999);
        String dayStr = new SimpleDateFormat(DateFormatEnum.YYYY_MM_DD_HH_MM_SS.getValue()).format(c.getTime());
        return dayStr;
    }

    /**
     * 获取指定日期所在月份开始的时间戳
     *
     * @param date 指定日期
     * @return
     */
    public static String getMonthBegin(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        //设置为1号,当前日期既为本月第一天
        c.set(Calendar.DAY_OF_MONTH, 1);
        //将小时至0
        c.set(Calendar.HOUR_OF_DAY, 0);
        //将分钟至0
        c.set(Calendar.MINUTE, 0);
        //将秒至0
        c.set(Calendar.SECOND, 0);
        //将毫秒至0
        c.set(Calendar.MILLISECOND, 0);
        // 获取本月第一天的时间
        String dayStr = new SimpleDateFormat(DateFormatEnum.YYYY_MM_DD_HH_MM_SS.getValue()).format(c.getTime());
        return dayStr;
    }

    public static List<String> getMonthBetween(String minDate, String maxDate) throws ParseException {
        ArrayList<String> result = new ArrayList<String>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");//格式化为年月

        Calendar min = Calendar.getInstance();   //获取最小日期
        Calendar max = Calendar.getInstance();	//获取最大日期

        min.setTime(sdf.parse(minDate));
        min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);   //最小日期的1号

        max.setTime(sdf.parse(maxDate));
        max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);  //最大日期2号

        Calendar curr = min;   //初始赋值，从最小的开始
        while (curr.before(max)) {   //判断 是否大于 最大日期2号
            result.add(sdf.format(curr.getTime()));   //放入list
            curr.add(Calendar.MONTH, 1);   //月 + 1
        }  //看到这里，就知道为什么需要定义最大日期的2号开始

        return result;
    }


    @Test
    public void test() throws ParseException {
        String beginTime = "2018-01-01";
        String endTime = "2020-01-01";
        List<String> result = DateUtils.getMonthBetween(beginTime,endTime);
        System.out.println(result);
    }

}
