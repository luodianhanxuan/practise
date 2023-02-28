package com.wangjg.outbox.util;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import lombok.Getter;

@Getter
public enum SyncHelper {

    COMMON_STRING_POOL,
    OUTBOX_STRING_POOL,
    ;

    private final Interner<Object> interner;

    SyncHelper() {
        interner = Interners.newWeakInterner();
    }


    public void sync(String syncKey, Runnable runnable) {
        synchronized (interner.intern(syncKey)) {
            runnable.run();
        }
    }

    public static void main(String[] args) {
        Interner<Object> pool1 = SyncHelper.COMMON_STRING_POOL.getInterner();
        Interner<Object> pool2 = SyncHelper.COMMON_STRING_POOL.getInterner();

        System.out.println(pool1 == pool2);

    }
}
