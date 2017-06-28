/*
 * DateTime 
 * 
 * Created by sealy on 2013-07-01. 
 * Copyright 2013 Sealy, Inc. All rights reserved.
 */

package inroids.common;

import android.os.SystemClock;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * Date and Time Manage
 *
 * @author Sealy
 */

public abstract class DateTime {
    private static final String sTag = "IRLibrary";

    /**
     * update system time
     *
     * @param sTime a DateTime format string.
     * @param sRoot a Root Shell
     * @return Whether the operation succeeded.
     * @throws code {@code<manifest ... android:sharedUserId="android.uid.system" ...>}
     */
    public static boolean updateSystemTime(Shell sRoot, String sTime) {
        try {
            Calendar c = Calendar.getInstance();
            Date s_Date = Convert.stringToDate(sTime, "yyyy-MM-dd HH:mm:ss");
            c.setTime(s_Date);
            long when = c.getTimeInMillis();
            if (when / 1000 < Integer.MAX_VALUE) {
                if (sRoot.getRoot().isRootShell()) {
                    sRoot.execute("chmod 666 /dev/alarm");
                    SystemClock.setCurrentTimeMillis(when);
                    sRoot.execute("chmod 664 /dev/alarm");
                }
                return true;
            }
        } catch (Exception e) {
            MyLog.e(sTag, "DateTime.updateSystemTime:" + e.toString());
        }
        return false;
    }

    /**
     * format current time
     *
     * @param sFormat the format string.format:yyyy-MM-dd HH:mm:ss
     * @return String Get a string for the formatted date.
     */
    public static String formatCurTime(String sFormat) {
        return Convert.dateToString(new Date(), sFormat);
    }

    /**
     * Date before
     *
     * @param dValue this date
     * @param iN     day(-:before Day +:After Day)
     * @return String Get a string for the formatted date.
     */
    public static Date getDateFromDay(Date dValue, int iN) {
        try {
            Calendar now = Calendar.getInstance();
            now.setTime(dValue);
            now.set(Calendar.DATE, now.get(Calendar.DATE) + iN);
            return now.getTime();
        } catch (Exception e) {
            MyLog.e(sTag, "DateTime.updateSystemTime:" + e.toString());
        }
        return null;
    }

    /**
     * Judge the given date is overdue or not.
     *
     * @param endTime 过期时间
     * @param sFormat the format string.format:yyyy-MM-dd
     * @return boolean the given date is overdue
     */
    public static boolean dateOverdue(String sEndTime, String sFormat) {
        if (sEndTime == null || sEndTime.equals("null") || sEndTime.equals(""))
            return false;
        try {
            Date endTime = Convert.stringToDate(sEndTime, sFormat);
            String now = DateTime.formatCurTime(sFormat);
            Date nowTime = Convert.stringToDate(now, sFormat);
            if (endTime.before(nowTime)) {
                return true;
            }
        } catch (Exception e) {
            MyLog.e(sTag, "DateTime.dateOverdue:" + e.toString());
        }
        return false;

    }

    /**
     * Get order number in week.
     *
     * @param Date 给定的日期
     * @return int the date order 1~7(星期一~星期日)
     */
    public static int dayOfWeek(String sDate) {
        int result = 0;
        try {
            int week = defaultDayOfWeek(sDate);
            result = week == 0 ? 7 : week;
        } catch (Exception e) {
            MyLog.e(sTag, "DateTime.dayOfWeek:" + e.toString());
        }
        return result;
    }

    /**
     * Get order number in week.
     *
     * @param Date 给定的日期
     * @return int the date order 0~6(星期日星期一~星期六)
     */
    public static int defaultDayOfWeek(String sDate) {
        int result = 0;
        try {
            Calendar c = Calendar.getInstance();
            Date date = Convert.stringToDate(sDate, "yyyy-MM-dd");
            c.setTime(date);
            result = c.get(Calendar.DAY_OF_WEEK) - 1;
        } catch (Exception e) {
            MyLog.e(sTag, "DateTime.dayOfWeek:" + e.toString());
        }
        return result;
    }

    /**
     * 根据数据中的规则,及当前是周几, 找出最近的下一次设定的时间间隔
     *
     * @param int    当前星期几
     * @param String "1,2,3,4,5,6,7"
     * @return int 间隔天数
     */
    //
    public static int getIntervalDays(int current, String day) {
        int result = 0;
        int index = 0;
        String[] days = day.split(",");
        int[] eachDay = new int[days.length];
        for (int i = 0; i < eachDay.length; i++) {
            eachDay[i] = Integer.parseInt(days[i]);
        }
        Arrays.sort(eachDay);

        // 数组中仅一个元素
        if (eachDay.length == 1) {
            if (eachDay[0] == current) {
                result = 7;
            } else if (eachDay[0] > current) {
                result = eachDay[0] - current;
            } else {
                result = 7 + eachDay[0] - current;
            }
        }
        if (eachDay.length >= 2) {
            if (eachDay[0] == current) {
                result = eachDay[1] - current;
            } else if (eachDay[0] > current) {
                result = eachDay[0] - current;
            } else {
                if (eachDay[eachDay.length - 1] <= current) {
                    result = 7 + eachDay[0] - current;
                } else {
                    for (int i = 0, length = eachDay.length; i < length; i++) {
                        if (eachDay[i] > current) {
                            index = i;
                            break;
                        } else if (eachDay[i] == current) {
                            index = i + 1;
                            break;
                        }
                    }
                    result = eachDay[index] - current;
                }
            }
        }
        return result;
    }

    public static Integer binarysearchKey(int targetNum, String day) {
        String[] days = day.split(",");
        int[] array = new int[days.length];
        for (int i = 0; i < array.length; i++) {
            array[i] = Integer.parseInt(days[i]);
        }
        Arrays.sort(array);
        int targetindex = 0;
        int left = 0, right = 0;
        for (right = array.length - 1; left != right; ) {
            int midIndex = (right + left) / 2;
            int mid = (right - left);
            int midValue = (Integer) array[midIndex];
            if (targetNum == midValue) {
                return midIndex;
            }

            if (targetNum > midValue) {
                left = midIndex;
            } else {
                right = midIndex;
            }

            if (mid <= 2) {
                break;
            }
        }
        java.lang.System.out.println("和要查找的数：" + targetNum + "最接近的数：" + array[targetindex]);
        return (Integer) (((Integer) array[right] - (Integer) array[left]) / 2 > targetNum ? array[right] : array[left]);
    }

    /**
     * 获取两个日期之间的间隔天数
     *
     * @return
     */
    public static int getGapCount(Date startDate, Date endDate) {
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(startDate);
        fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
        fromCalendar.set(Calendar.MINUTE, 0);
        fromCalendar.set(Calendar.SECOND, 0);
        fromCalendar.set(Calendar.MILLISECOND, 0);

        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTime(endDate);
        toCalendar.set(Calendar.HOUR_OF_DAY, 0);
        toCalendar.set(Calendar.MINUTE, 0);
        toCalendar.set(Calendar.SECOND, 0);
        toCalendar.set(Calendar.MILLISECOND, 0);
        return (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24));
    }
}
