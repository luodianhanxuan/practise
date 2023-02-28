package com.wangjg.playground;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import lombok.Data;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PlayGround {

    @Data
    public static class HeyMan {
        private String name;

        private int age;

        public void fooName(String name) {
            this.name = name;
        }

        public void birthday() {
            this.age++;
        }

    }

    public static void main(String[] args) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        int pressure = 1_9000_0000;
        // ############ instance ############

        TimeInterval timer = DateUtil.timer();
        timer.start();
        HeyMan heyMan = null;
        System.out.println("############ new instance start ############");
        for (int i = 0; i < pressure; i++) {
            heyMan = new HeyMan();
        }
        System.out.printf("############ new instance end , spent: %sms############%n", timer.intervalMs());

        System.out.println("############ reflection instance start ############");
        timer.restart();
        Class<HeyMan> heyManClass = HeyMan.class;
        for (int i = 0; i < pressure; i++) {
            heyMan = heyManClass.newInstance();
        }
        System.out.printf("############ reflection instance end , spent: %sms############%n", timer.intervalMs());

        System.out.println("############ direct call method start");
        timer.restart();
        for (int i = 0; i < pressure; i++) {
            heyMan.birthday();
        }
        System.out.printf("############ direct call method end , spent: %sms############%n", timer.intervalMs());

        System.out.println("############ reflection call method start ############");
        timer.restart();
        for (int i = 0; i < pressure; i++) {
            Method birthday = heyManClass.getMethod("birthday");
            birthday.invoke(heyMan);
        }
        System.out.printf("############ reflection call method end , spent: %sms############%n", timer.intervalMs());

        System.out.println("############ reflection ignore get method class call method start  ############");
        timer.restart();
        Method birthday = heyManClass.getMethod("birthday");
        for (int i = 0; i < pressure; i++) {
            birthday.invoke(heyMan);
        }
        System.out.printf("############ reflection ignore get method class call method, spent: %sms############%n", timer.intervalMs());


        System.out.println("############ reflection optimized call method start");
        timer.restart();
        birthday = heyManClass.getMethod("birthday");
        for (int i = 0; i < pressure; i++) {
            birthday.setAccessible(true);
            birthday.invoke(heyMan);
        }
        System.out.printf("############ reflection optimized call method , spent: %sms############%n", timer.intervalMs());

    }
}
