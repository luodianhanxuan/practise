package com.wangjg.learnlambda;


import org.junit.validator.PublicClassValidator;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Main {

    public void testConsumer(){
        Consumer<String> c = s -> System.out.println(s);
    }

    public void testSupplier(){
        Supplier<String> c = () -> "hello supplier";
    }

    public void testFunction(){
        Function<String, String> c = (s) -> s;
    }


    public void testStaticMethod() {
        Consumer<String> c = Test::staticMethod;
    }

    public void testMethod() {
        BiConsumer<Test, String> c = Test::method;
    }

    public static class Test {

        public void method(String s){

        }
        public static void staticMethod(String s) {

        }
    }
}
