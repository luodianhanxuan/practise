package com.wangjg.ansi;

import java.util.*;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("\u001b[30m A \u001b[31m B \u001b[32m C \u001b[33m D \u001b[0m");
        System.out.println("\u001b[34m E \u001b[35m F \u001b[36m G \u001b[37m H \u001b[0m");

        System.out.println("=================================================================");
        System.out.println("\u001b[30;1m A \u001b[31;1m B \u001b[32;1m C \u001b[33;1m D \u001b[0m");
        System.out.println("\u001b[34;1m E \u001b[35;1m F \u001b[36;1m G \u001b[37;1m H \u001b[0m");

        System.out.println("=================================================================");

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                int code = i * 16 + j;
                System.out.printf("\u001b[38;5;%dm%-4d", code, code);
            }
            System.out.println("\u001b[0m");
        }

        System.out.println("=================================================================");


        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                int code = i * 16 + j;
                System.out.printf("\u001b[48;5;%dm%-4d", code, code);
            }
            System.out.println("\u001b[0m");
        }

        System.out.println("\u001b[1m BOLD \u001b[0m\u001b[4m Underline \u001b[0m\u001b[7m Reversed \u001b[0m");

        System.out.println("=================================================================");

        System.out.println("\u001b[1m\u001b[4m\u001b[7m BOLD Underline Reversed \u001b[0m");

        System.out.println("=================================================================");
        System.out.println("\u001b[1m\u001b[31m Red Bold \u001b[0m");
        System.out.println("\u001b[4m\u001b[44m Blue Background Underline \u001b[0m");

        System.out.println("=================================================================");
        System.out.println("\u001b[10m Font \u001b[0m");
        System.out.println("\u001b[11m Font \u001b[0m");
        System.out.println("\u001b[12m Font \u001b[0m");
        System.out.println("\u001b[13m Font \u001b[0m");
        System.out.println("\u001b[14m Font \u001b[0m");
        System.out.println("\u001b[15m Font \u001b[0m");
        System.out.println("\u001b[16m Font \u001b[0m");
        System.out.println("\u001b[17m Font \u001b[0m");
        System.out.println("\u001b[18m Font \u001b[0m");
        System.out.println("\u001b[19m Font \u001b[0m");
        System.out.println("=================================================================");
    }

}
