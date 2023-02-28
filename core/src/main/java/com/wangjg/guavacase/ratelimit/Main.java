package com.wangjg.guavacase.ratelimit;

import com.google.common.util.concurrent.RateLimiter;

public class Main {

    public static void main(String[] args) {
        RateLimiter rateLimiter = RateLimiter.create(1.0 / 3);
        for (; ; ) {
            rateLimiter.acquire();
            System.out.println(System.currentTimeMillis());
        }

    }
}
