package com.wangjg.util;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

/**
 * API文档地址：<a href="http://tool.bitefu.net/showdoc/web/#/2">...</a>
 * <p>
 * 工作日对应结果为 0, 休息日对应结果为 1, 节假日对应的结果为 2
 */
@SuppressWarnings("unused")
public class WorkDayUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkDayUtil.class);
    private static final String TAG = "工作日工具类：";

    private static final String apikey;

    static {
        apikey = System.getenv().getOrDefault("jiari.apiKey", "123456");
    }

    private static final String format_place_holder = "%s";
    private static final String API_URL_FORMAT = String.format("http://vip.bitefu.net/jiari/?apikey=%s&d=%s,%s&type=6&backtype=%s", apikey, format_place_holder, format_place_holder, format_place_holder);

    public static final int WORKDAY = 0;
    public static final int RESTDAY = 1;
    public static final int HOLIDAY = 2;

    private static Map<String, Map<String, Integer>> responseCache = new ConcurrentHashMap<>();

    private static CountDownLatch COUNT_DOWN_LATCH;

    /**
     * 获取指定日期的工作日信息
     *
     * @param datesStr 指定日期集合 yyyy-MM-dd
     */
    public static Map<String, Integer> getWorkdayInfo4Dates(List<String> datesStr) throws Exception {
        Date begin = new Date();
        List<Date> dates = DateUtil.parseDates(datesStr);

        //获取节假日信息
        doCacheYearsHolidayIfNessary(dates);

        if (COUNT_DOWN_LATCH != null) {
            COUNT_DOWN_LATCH.await();
        }

        Map<String, Integer> map = new HashMap<>(datesStr.size());

        String year;
        String yearMonthAndDay;
        Map<String, Integer> theYearHoliday;
        for (String dateStr : datesStr) {
            String[] split = dateStr.split("-");
            year = split[0];
            yearMonthAndDay = split[0] + split[1] + split[2];

            theYearHoliday = responseCache.get(year);

            map.put(dateStr, theYearHoliday.get(yearMonthAndDay));
        }
        Date end = new Date();
        LOGGER.info(String.format("%s：节假日方法总共消耗时间：【%s】", TAG, end.getTime() - begin.getTime()));
        return map;
    }

    /**
     * @param dates 日期集合
     */
    private static void doCacheYearsHolidayIfNessary(List<Date> dates) {
        List<String> allYears = getAllYears(dates);

        List<String> cachedYears = getCachedYears(allYears);

        List<String> unCachedYears;
        if (CollectionUtil.isEmpty(cachedYears)) {
            unCachedYears = allYears;
        } else {
            unCachedYears = allYears.stream().filter(item -> !cachedYears.contains(item)).collect(Collectors.toList());
        }

        if (!CollectionUtil.isEmpty(unCachedYears)) {
            COUNT_DOWN_LATCH = new CountDownLatch(3 * unCachedYears.size());
            for (String unCachedYear : unCachedYears) {
                ThreadPoolUtil.execute(() -> {
                    try {
                        listDatesOfTheYear4BackType(unCachedYear, 0);
                    } catch (Exception e) {
                        LOGGER.error(String.format("%s：year【%s】 backType【%s】获取节假日信息失败", TAG, unCachedYear, 0), e);
                    } finally {
                        COUNT_DOWN_LATCH.countDown();
                    }
                });

                ThreadPoolUtil.execute(() -> {
                    try {
                        listDatesOfTheYear4BackType(unCachedYear, 1);
                    } catch (Exception e) {
                        LOGGER.error(String.format("%s：year【%s】 backType【%s】获取节假日信息失败", TAG, unCachedYear, 1), e);
                    } finally {
                        COUNT_DOWN_LATCH.countDown();
                    }
                });

                ThreadPoolUtil.execute(() -> {
                    try {
                        listDatesOfTheYear4BackType(unCachedYear, 2);
                    } catch (Exception e) {
                        LOGGER.error(String.format("%s：year【%s】 backType【%s】获取节假日信息失败", TAG, unCachedYear, 2), e);
                    } finally {
                        COUNT_DOWN_LATCH.countDown();
                    }
                });
            }
        }
    }


    @SuppressWarnings("unchecked")
    private static void listDatesOfTheYear4BackType(String unCachedYear, int backType) throws IOException {
        String beginOfYear = unCachedYear + "0101";
        String endOfYear = unCachedYear + "1231";

        HttpGet httpGet = new HttpGet(String.format(API_URL_FORMAT, beginOfYear, endOfYear, backType));
        CloseableHttpClient httpClient;
        if (enableProxy && proxyHost != null && proxyPort != null) {
            LOGGER.info("proxy ->" + proxyHost + ":" + proxyPort);
            httpClient = HttpClientConfigUtil.init().proxy(proxyHost, proxyPort).build();
        } else {
            httpClient = HttpClientConfigUtil.init().build();
        }
//        LOGGER.info(String.format("%s：请求url为：%s", TAG, httpGet.getURI()));
        Date begin = new Date();
        CloseableHttpResponse response = httpClient.execute(httpGet);
        Date end = new Date();
        LOGGER.info(String.format("%s：请求url为：%s，接口响应时间为【%s】", TAG, httpGet.getURI(), end.getTime() - begin.getTime()));

        String json = EntityUtils.toString(response.getEntity(), "utf-8");
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> results = objectMapper.readValue(json, new TypeReference<HashMap<String, Object>>() {
        });
        if (results == null) {
            return;
        }
        List<String> dates = (ArrayList<String>) results.get("data");
        if (CollectionUtil.isEmpty(dates)) {
            return;
        }

        Map<String, Integer> map = responseCache.computeIfAbsent(unCachedYear, i -> new ConcurrentHashMap<>());
        // 放入缓存
        for (String date : dates) {
            if (map.containsKey(date)) {
                LOGGER.error(String.format("%s：日期【%s】已经缓存过，值为【%s】", TAG, date, map.get(date)));
            } else {
                map.put(date, backType);
            }
        }
    }


    private static List<String> getCachedYears(List<String> years) {
        if (responseCache.isEmpty()) {
            return null;
        }
        List<String> cachedYears = new ArrayList<>();

        for (String year : years) {
            if (!responseCache.containsKey(year)) {
                continue;
            }
            LOGGER.info(String.format("%s：%s年的节假日信息已经被缓存了，无需请求接口获取该年节假日的信息", TAG, year));
            cachedYears.add(year);
        }
        return cachedYears;
    }

    /**
     * 筛选出需要查询的年份
     *
     * @param dates 指定日期
     * @return yyyyMMdd 格式的年份集合
     */
    private static List<String> getAllYears(List<Date> dates) {
        Set<String> years = new HashSet<>();
        String theYear;
        for (Date date : dates) {
            if (date == null) {
                continue;
            }
            theYear = DateUtil.format(DateUtil.PATTERN_YEAR_DEFAULT, date);
            years.add(theYear);
        }

        return new ArrayList<>(years);
    }


    private static boolean enableProxy;
    private static String proxyHost;
    private static Integer proxyPort;

    static {
        enableProxy = Objects.equals("true", System.getenv("workday.net.proxy.enable"));
        proxyHost = System.getenv("workday.net.proxy.host");
        proxyPort = System.getenv("workday.net.proxy.port") == null ? null : Integer.valueOf(System.getenv("workday.net.proxy.port"));
    }


    public static void main(String[] args) throws Exception {
        System.out.println(JSON.toJSONString(getWorkdayInfo4Dates(
                DateUtil.formatDate(DateUtil.daysBetween(DateUtil.parseDate("2021-9-30"), DateUtil.parseDate("2025-12-06"))))));

        ThreadPoolUtil.shutdown();
    }

}
