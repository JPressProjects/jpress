package io.jpress.commons.utils;

import com.google.common.collect.Lists;
import io.jboot.utils.StrUtil;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.security.InvalidParameterException;
import java.util.Date;
import java.util.List;

/**
 * 日期工具类
 *
 * @author Eric.Huang
 * @date 2019-03-10 10:37
 * @package io.jpress.commons.utils
 **/

public class DateUtils {

    public static final String DEFAULT_YEAR_MONTH_DAY = "yyyy-MM-dd";

    /** 缺省日期格式,精确到秒 */
    public static final String DEFAULT_FORMATTER = "yyyy-MM-dd HH:mm:ss";

    /**
     * 功能描述：字符串转日期
     * @param str		日期字符串
     * @param format	日期格式
     * @return
     * 返回类型：Date
     * 创建人：eric
     * 日期：2017年2月16日
     */
    public static Date strToDate(String str, String format) {

        if (StrUtil.isBlank(str)) {
            return null;
        }

        if (StrUtil.isBlank(format)) {
            format = DEFAULT_FORMATTER;
        }

        DateTimeFormatter dtf = DateTimeFormat.forPattern(format);
        return dtf.parseDateTime(str).toDate();

    }

    /** 获取指定日期之前的所有天数 */
    public static List<DateTime> getAllDaysBeforeDate(Date expireDate) {
        if (expireDate == null) {
            return null;
        }

        List<DateTime> result = Lists.newArrayList();
        DateTime expiredDateTime = new DateTime(expireDate);
        List<DateTime> list = getSurplusDaysAfterNowInMonth();

        for (DateTime dateTime : list) {
            if (dateTime.isBefore(expiredDateTime)) {
                result.add(dateTime);
            }
        }

        return result;
    }

    /** 获取当前月的剩余天数 */
    public static List<DateTime> getSurplusDaysAfterNowInMonth() {

        DateTime now = DateTime.now();
        int curDay = now.getDayOfMonth();
        int maxDay = now.dayOfMonth().getMaximumValue();
        List<DateTime> result = Lists.newArrayList();

        for (int i = curDay + 1; i <= maxDay; i++) {
            result.add(now.withDayOfMonth(i));
        }
        return result;
    }

    /** 获取一年中的剩余天数 */
    public static List<DateTime> getSurplusDaysAfterNowInYear() {

        DateTime now = DateTime.now();
        int curDay = now.getDayOfYear();
        int maxDay = now.dayOfYear().getMaximumValue();
        List<DateTime> result = Lists.newArrayList();

        for (int i = curDay + 1; i <= maxDay; i++) {
            result.add(now.withDayOfYear(i));
        }
        return result;
    }

    /**
     * 功能描述：获取当前时间之后的所有周几日期，如每周二
     * @param weekNo	周几，如周一，周二
     * @return List<DateTime>
     * @author：eric
     */
    public static List<DateTime> dayOfWeek(int weekNo) {

        DateTime now = DateTime.now();
        DateTime dateTime = null;
        int week = now.getDayOfWeek();
        if (week > weekNo) {
            dateTime = now.plusWeeks(1).withDayOfWeek(weekNo);
        } else {
            dateTime = now.withDayOfWeek(weekNo);
        }

        List<DateTime> list = Lists.newLinkedList();

        // 获取当前周是当年内第几周
        int currentWeekNo = dateTime.getWeekOfWeekyear();
        // 获取当年的总周数
        int maxWeekNo = dateTime.weekOfWeekyear().getMaximumValue();

        for ( int i = currentWeekNo; i <= maxWeekNo; i++) {
            list.add(new DateTime().withWeekOfWeekyear(i).withDayOfWeek(weekNo));
        }

        return list;
    }

    /**
     * 功能描述：给指定日期添加天数
     * @param date	日期
     * @param day	天数
     * @return：Date
     * @author：eric
     */
    public static Date plusDays(Date date, int day) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusDays(day).toDate();
    }

    /**
     * 功能描述：获取当前时间之后，指定时间之前的所有周几日期，如每周二
     * @param weekNo		周几，如周一，周二
     * @param endDate		截至日期
     * @return：List<DateTime>
     * @author：eric
     */
    public static List<DateTime> getDayOfWeek(int weekNo, Date endDate) {

        if (endDate == null) {
            return null;
        }

        List<DateTime> result = Lists.newArrayList();
        DateTime endDateTime = new DateTime(endDate);
        List<DateTime> list = dayOfWeek(weekNo);

        for (DateTime dateTime : list) {
            if (dateTime.isBefore(endDateTime)) {
                result.add(dateTime);
            }
        }

        return result;
    }

    /**
     * 功能描述：计算两个日期之间的天数
     * 输入参数：
     * @param startDate
     * @param endDate
     * @return
     * 返回类型：int
     * 创建人：eric
     * 日期：2017年6月10日
     */
    public static int getDaysBetweenDays(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            throw new InvalidParameterException("startDate and endDate cannot be null!");
        }

        return Days.daysBetween(new DateTime(startDate), new DateTime(endDate)).getDays();
    }
}
