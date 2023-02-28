package com.wangjg.test.annotationprocessor;

import com.wangjg.annotationprocessor.MyAnnotation;

@MyAnnotation
public class Test2 {
    private String f2;

    public void test2(){
        System.out.println("test2 ...");
    }
}
