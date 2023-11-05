package com.wangjg.editdistance;

import org.simmetrics.StringDistance;
import org.simmetrics.StringMetric;
import org.simmetrics.builders.StringDistanceBuilder;
import org.simmetrics.builders.StringMetricBuilder;
import org.simmetrics.metrics.CosineSimilarity;
import org.simmetrics.metrics.Levenshtein;
import org.simmetrics.simplifiers.Simplifiers;
import org.simmetrics.tokenizers.Tokenizers;

import java.util.Locale;


/**
 * <a href="https://github.com/Simmetrics/simmetrics/blob/master/README.md">...</a>
 */
public class SimMetricsTest {

    public static void main(String[] args) {
        String str1 = "国债风险资本准备计提比例";
        String str2 = "定向产品其他风险资本准备计提比例";

        StringMetric metric =
                StringMetricBuilder.with(new CosineSimilarity<>())
//                        .simplify(Simplifiers.toLowerCase(Locale.CHINA))
//                        .simplify(Simplifiers.replaceNonWord())
                        .tokenize(new NlpTokenizer())
                        .build();

        float result = metric.compare(str1, str2); // 0.5720
        System.out.println(result);

        StringDistance distance = StringDistanceBuilder.with(new Levenshtein())
                .simplify(Simplifiers.toLowerCase(Locale.CHINA))
                .simplify(Simplifiers.replaceNonWord())
                .build();

        result = distance.distance(str1, str2); // 30.0
        System.out.println(result);
    }
}
