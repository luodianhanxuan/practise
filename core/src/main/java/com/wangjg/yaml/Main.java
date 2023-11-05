package com.wangjg.yaml;

import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class Main {

    /**
     * 解析yml字符串
     */
    @Test
    public void ymlStrTest() {
        Yaml yaml = new Yaml();
        //实际上返回的是LinkedHashMap
        Object ret = yaml.load("name: jerry");
        System.out.println(ret);
    }

    /**
     * 解析yml文件
     */
    @Test
    public void ymlFileTest() {
        Yaml yaml = new Yaml();
        //java.lang.ClassCastException: java.util.LinkedHashMap cannot be cast to java.util.List
        Object ret = yaml.load(this.getClass().getClassLoader().getResourceAsStream("testyaml.yml"));
        System.out.println(ret);
    }

    /**
     * 解析yml文件
     */
    @Test
    public void ymlFile2StrTest() {
        Yaml yaml = new Yaml();
        //java.lang.ClassCastException: java.util.LinkedHashMap cannot be cast to java.util.List
        Object ret = yaml.load(this.getClass().getClassLoader().getResourceAsStream("testyaml.yml"));
        String dump = yaml.dump(ret);
        System.out.println(dump);
//        Object load = yaml.load(dump);
//        System.out.println(load);
    }



    /**
     * 创建yml字符串
     */
    @Test
    public void testDump1() {
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("name", "Jerry");
        obj.put("age", 30);

        Yaml yaml = new Yaml();
        StringWriter sw = new StringWriter();
        yaml.dump(obj, sw);
        System.out.println(sw);
    }

}
