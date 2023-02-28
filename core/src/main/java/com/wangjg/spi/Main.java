package com.wangjg.spi;

import java.util.ServiceLoader;

public class Main {
    public static void main(String[] args) {
        ServiceLoader<Car> loader = ServiceLoader.load(Car.class);
        for (Car car : loader) {
            car.run();
        }
    }
}
