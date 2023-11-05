package com.wangjg.mapstruct;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Map<String, String> map = new HashMap<>();
        map.put("fs","1");
        map.put("si","2");
        TestObj obj = Test.INSTANCE.toObj(map);
        HashMap<String,String> map1 = Test.INSTANCE.toMap(obj);
        System.out.println(map);
        System.out.println(obj);
        System.out.println(map1);
    }
}
