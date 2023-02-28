package com.wangjg.test.annotationprocessor;

import com.wangjg.annotationprocessor.MyAnnotation;

@MyAnnotation
public class Test1 {

    private String f1;

    public void test1() {
        System.out.println("test1 ....");
    }
}
