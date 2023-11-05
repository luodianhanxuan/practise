package com.wangjg.editdistance;

import org.apache.commons.text.similarity.LevenshteinDistance;

/**
 * <a href="https://github.com/apache/commons-text">...</a>
 */
public class ApacheCommonsTextTest {
    public static void main(String[] args) {
        String str1 = "This is a sentence. It is made of words";
        String str2 = "This sentence is similar. It has almost the same words";


        Integer distance = LevenshteinDistance.getDefaultInstance().apply(str1, str2); // 30
        System.out.println(distance);
    }
}
