package com.wangjg.guavacase.interner;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;

public class GuavaInterner {

    public static void main(String[] args) {
        Interner<String> stringPool = Interners.newWeakInterner();
        String orderId = "123123123123";
        synchronized (stringPool.intern(orderId)) {
            // .........

        }
    }

}
