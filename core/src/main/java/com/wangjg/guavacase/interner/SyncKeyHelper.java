package com.wangjg.guavacase.interner;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import lombok.Getter;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;

@Getter
public enum SyncKeyHelper {
    STRING;

    private Interner<Object> pool;

    SyncKeyHelper() {
        this.pool = Interners.newWeakInterner();
    }

    static class Counter {
        private int i;
        private String businessId;

        public Counter(String businessId) {
            this.businessId = businessId;
        }

        public void incr() {
            synchronized (SyncKeyHelper.STRING.pool.intern(businessId)) {
                i++;
            }
        }

        public int getI() {
            return i;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        String info = "baidu";
        String strA = "www.baidu.com";//存储在运行时常量池
        String strB = "www." + info + ".com";
        System.out.println(strA == strB);//false
        System.out.println(SyncKeyHelper.STRING.pool.intern(strA) == SyncKeyHelper.STRING.pool.intern(strB));//true

        int threadNum = 100;
        int loop = 100;

        String orderId = UUID.randomUUID().toString();

        CountDownLatch countDownLatch = new CountDownLatch(threadNum * loop);
        Counter counter = new Counter(orderId);
        Runnable runnable = () -> {
            for (int i = 0; i < loop; i++) {
                counter.incr();
                countDownLatch.countDown();
            }
        };
        for (int i = 0; i < threadNum; i++) {
            new Thread(runnable).start();
        }


        countDownLatch.await();
        System.out.println(counter.i);

    }
}

