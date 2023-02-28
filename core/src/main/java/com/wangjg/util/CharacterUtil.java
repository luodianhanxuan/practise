package com.wangjg.util;

import java.util.regex.Pattern;

public class CharacterUtil {

    public static int getDoubleByteCharAmt(String str) {
        int amount = 0;
        String regex = "[·]{0,}[^\\x00-\\xff]{0,}$";
        for (int j = 0; j < str.length(); j++) {
            String s = str.charAt(j) + "";
            boolean matches = Pattern.matches(regex, s);
            if (matches) {
                amount++;
            }
            amount++;
        }
        return amount;
    }
}
