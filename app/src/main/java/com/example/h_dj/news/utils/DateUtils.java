package com.example.h_dj.news.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by H_DJ on 2017/5/7.
 */

public class DateUtils {

    /**
     * Date 转换为String
     *
     * @param date
     * @param format
     * @return
     */
    public static String Date2String(Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }

    /**
     * String 转化为Date
     *
     * @param time
     * @param format
     * @return
     * @throws ParseException
     */
    public static Date String2Date(String time, String format) throws ParseException {
        return new SimpleDateFormat(format).parse(time);
    }

    /**
     * @param st     格式化时间
     * @param format 时间的格式
     * @return 返回时间戳
     */
    public static long String2Tims(String st, String format) throws ParseException {
        return String2Date(st, format).getTime();
    }

    /**
     * 时间戳转为格式化时间串
     *
     * @param tims
     * @param format
     * @return
     */
    public static String Times2String(long tims, String format) {
        Date date = new Date(tims);
        return Date2String(date, format);
    }
}
