package com.wangjg.util;

import com.google.common.collect.MapMaker;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class DateUtil {

    private final static ZoneId zoneId = ZoneId.systemDefault();

    public static final String PATTERN_DATE_TIME_DEFAULT = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_DATE_DEFAULT = "yyyy-MM-dd";

    public static final String PATTERN_YEAR_DEFAULT = "yyyy";


    /**
     * 获取两个日期之间的日期
     */
    public static List<Date> daysBetween(Date begin, Date end) {
        Instant beginInstant = begin.toInstant();
        Instant endInstant = end.toInstant();

        ZonedDateTime beginZonedDateTime = beginInstant.atZone(zoneId).toLocalDate().atStartOfDay(zoneId);
        ZonedDateTime endZonedDateTime = endInstant.atZone(zoneId).toLocalDate().atStartOfDay(zoneId);

        List<LocalDate> localDateList = new ArrayList<>();

        while (!beginZonedDateTime.isAfter(endZonedDateTime)) {
            localDateList.add(beginZonedDateTime.toLocalDate());
            beginZonedDateTime = beginZonedDateTime.plusDays(1);
        }

        List<Date> list = new ArrayList<>();
        for (LocalDate localDate : localDateList) {
            list.add(Date.from(localDate.atStartOfDay(zoneId).toInstant()));
        }

        return list;
    }

    public static List<Date> parseDates(List<String> dateStrList) {
        if (CollectionUtil.isEmpty(dateStrList)) {
            return null;
        }

        return dateStrList.stream().map(DateUtil::parseDate).collect(Collectors.toList());
    }

    public static Date parseDate(String date) {
        DateFormat dateFormat = getDateFormat(PATTERN_DATE_DEFAULT);
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static Date parseDateTime(String date) {
        DateFormat dateFormat = getDateFormat(PATTERN_DATE_TIME_DEFAULT);
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static List<String> formatDate(List<Date> daysBetween) {
        return CollectionUtil.isEmpty(daysBetween) ? null : daysBetween.stream().map(DateUtil::formatDate).collect(Collectors.toList());
    }


    public static String formatDate(Date date) {
        return getDateFormat(PATTERN_DATE_DEFAULT).format(date);
    }

    public static String format(String pattern, Date date) {
        return getDateFormat(pattern).format(date);
    }


    private static final Map<String, ThreadLocal<? extends DateFormat>> dfKeeper = new MapMaker().makeMap();

    private static DateFormat getDateFormat(String pattern) {
        if (dfKeeper.containsKey(pattern)) {
            return dfKeeper.get(pattern).get();
        }

        ThreadLocal<SimpleDateFormat> threadLocal = ThreadLocal.withInitial(() -> new SimpleDateFormat(pattern));
        dfKeeper.put(pattern, threadLocal);
        return threadLocal.get();
    }

    /**
     * 描述:此类用于取得当前日期相对应的月初，月末，季初，季末，年初，年末，返回值均为String字符串
     * 1、得到当前日期         today()
     * 2、得到当前月份月初      thisMonth()
     * 3、得到当前月份月底      thisMonthEnd()
     * 4、得到当前季度季初      thisSeason()
     * 5、得到当前季度季末      thisSeasonEnd()
     * 6、得到当前年份年初      thisYear()
     * 7、得到当前年份年底      thisYearEnd()
     * 8、判断输入年份是否为闰年 leapYear
     * <p>
     * 注意事项:  日期格式为：xxxx-yy-zz (eg: 2007-12-05)
     * <p>
     * 实例:
     *
     * @author pure
     */
    public static class DateThis {
        private int x;                  // 日期属性：年
        private int y;                  // 日期属性：月
        private int z;                  // 日期属性：日
        private Calendar localTime;     // 当前日期

        public DateThis() {
            localTime = Calendar.getInstance();
        }

        /**
         * 功能：得到当前日期 格式为：xxxx-yy-zz (eg: 2007-12-05)<br>
         *
         * @return String
         * @author pure
         */
        public String today() {
            String strY = null;
            String strZ = null;
            x = localTime.get(Calendar.YEAR);
            y = localTime.get(Calendar.MONTH) + 1;
            z = localTime.get(Calendar.DATE);
            strY = y >= 10 ? String.valueOf(y) : ("0" + y);
            strZ = z >= 10 ? String.valueOf(z) : ("0" + z);
            return x + "-" + strY + "-" + strZ;
        }

        /**
         * 功能：得到当前月份月初 格式为：xxxx-yy-zz (eg: 2007-12-01)<br>
         *
         * @return String
         * @author pure
         */
        public String thisMonth() {
            String strY = null;
            x = localTime.get(Calendar.YEAR);
            y = localTime.get(Calendar.MONTH) + 1;
            strY = y >= 10 ? String.valueOf(y) : ("0" + y);
            return x + "-" + strY + "-01";
        }

        /**
         * 功能：得到当前月份月底 格式为：xxxx-yy-zz (eg: 2007-12-31)<br>
         *
         * @return String
         * @author pure
         */
        public String thisMonthEnd() {
            String strY = null;
            String strZ = null;
            boolean leap = false;
            x = localTime.get(Calendar.YEAR);
            y = localTime.get(Calendar.MONTH) + 1;
            if (y == 1 || y == 3 || y == 5 || y == 7 || y == 8 || y == 10 || y == 12) {
                strZ = "31";
            }
            if (y == 4 || y == 6 || y == 9 || y == 11) {
                strZ = "30";
            }
            if (y == 2) {
                leap = leapYear(x);
                if (leap) {
                    strZ = "29";
                } else {
                    strZ = "28";
                }
            }
            strY = y >= 10 ? String.valueOf(y) : ("0" + y);
            return x + "-" + strY + "-" + strZ;
        }

        /**
         * 功能：得到当前季度季初 格式为：xxxx-yy-zz (eg: 2007-10-01)<br>
         *
         * @return String
         * @author pure
         */
        public String thisSeason() {
            String dateString = "";
            x = localTime.get(Calendar.YEAR);
            y = localTime.get(Calendar.MONTH) + 1;
            if (y <= 3) {
                dateString = x + "-" + "01" + "-" + "01";
            }
            if (y >= 4 && y <= 6) {
                dateString = x + "-" + "04" + "-" + "01";
            }
            if (y >= 7 && y <= 9) {
                dateString = x + "-" + "07" + "-" + "01";
            }
            if (y >= 10 && y <= 12) {
                dateString = x + "-" + "10" + "-" + "01";
            }
            return dateString;
        }

        /**
         * 功能：得到当前季度季末 格式为：xxxx-yy-zz (eg: 2007-12-31)<br>
         *
         * @return String
         * @author pure
         */
        public String thisSeasonEnd() {
            String dateString = "";
            x = localTime.get(Calendar.YEAR);
            y = localTime.get(Calendar.MONTH) + 1;
            if (y >= 1 && y <= 3) {
                dateString = x + "-" + "03" + "-" + "31";
            }
            if (y >= 4 && y <= 6) {
                dateString = x + "-" + "06" + "-" + "30";
            }
            if (y >= 7 && y <= 9) {
                dateString = x + "-" + "09" + "-" + "30";
            }
            if (y >= 10 && y <= 12) {
                dateString = x + "-" + "12" + "-" + "31";
            }
            return dateString;
        }

        /**
         * 功能：得到当前年份年初 格式为：xxxx-yy-zz (eg: 2007-01-01)<br>
         *
         * @return String
         * @author pure
         */
        public String thisYear() {
            x = localTime.get(Calendar.YEAR);
            return x + "-01" + "-01";
        }

        /**
         * 功能：得到当前年份年底 格式为：xxxx-yy-zz (eg: 2007-12-31)<br>
         *
         * @return String
         * @author pure
         */
        public String thisYearEnd() {
            x = localTime.get(Calendar.YEAR);
            return x + "-12" + "-31";
        }

        /**
         * 功能：判断输入年份是否为闰年<br>
         *
         * @return 是：true  否：false
         * @author pure
         */
        public boolean leapYear(int year) {
            boolean leap;
            if (year % 4 == 0) {
                if (year % 100 == 0) {
                    leap = year % 400 == 0;
                } else {
                    leap = true;
                }
            } else {
                leap = false;
            }
            return leap;
        }
    }


    public static void main(String[] args) {
        DateThis dateThis = new DateThis();
        String s = dateThis.thisMonth();
        System.out.println(s);
        System.out.println(dateThis.thisMonthEnd());

    }

}
