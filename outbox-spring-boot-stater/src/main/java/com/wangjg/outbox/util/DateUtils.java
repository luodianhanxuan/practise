package com.wangjg.outbox.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalUnit;
import java.util.Date;

public class DateUtils {

    /**
     * 日期相加
     *
     * @param date        日期
     * @param amountToAdd 要加的量
     * @param timeUnit    要加的日期单位，{@link java.time.temporal.ChronoUnit}
     * @return 加完之后的日期
     */
    public static Date plus(Date date, long amountToAdd, TemporalUnit timeUnit) {
        LocalDateTime localDateTime = LocalDateTime.from(date.toInstant().atZone(ZoneId.systemDefault()));
        return Date.from(localDateTime.plus(amountToAdd, timeUnit).atZone(ZoneId.systemDefault()).toInstant());
    }
}
