package com.wangjg.util;

import com.google.common.collect.MapMaker;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
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


}
