package com.wangjg.spi.impl;

import com.google.auto.service.AutoService;
import com.wangjg.spi.Car;

@AutoService(Car.class)
public class Audi implements Car {
    @Override
    public void run() {
        System.out.println("Audi run ...");
    }
}
