package com.wangjg.protoc;

import com.github.os72.protocjar.Protoc;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        int i = Protoc.runProtoc("v".split(","));
        System.out.println(i);
    }
}
