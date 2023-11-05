package com.wangjg.editdistance;

import org.lionsoul.jcseg.ISegment;
import org.lionsoul.jcseg.IWord;
import org.lionsoul.jcseg.dic.ADictionary;
import org.lionsoul.jcseg.dic.DictionaryFactory;
import org.lionsoul.jcseg.segmenter.SegmenterConfig;
import org.simmetrics.tokenizers.AbstractTokenizer;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class NlpTokenizer extends AbstractTokenizer {
    @Override
    public List<String> tokenizeToList(String input) {
        //创建SegmenterConfig分词配置实例，自动查找加载jcseg.properties配置项来初始化
        SegmenterConfig config = new SegmenterConfig(true);

        //创建默认单例词库实现，并且按照config配置加载词库
        ADictionary dic = DictionaryFactory.createSingletonDictionary(config);

        //依据给定的ADictionary和SegmenterConfig来创建ISegment
        //为了Api往后兼容，建议使用SegmentFactory来创建ISegment对象
        ISegment seg = ISegment.NLP.factory.create(config, dic);

        //备注：以下代码可以反复调用，seg为非线程安全
        //设置要被分词的文本
        try {
            seg.reset(new StringReader(input));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<String> list = new ArrayList<>();
        //获取分词结果
        IWord word;
        while (true) {
            try {
                if ((word = seg.next()) == null) break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            list.add(word.getValue());
        }
        return list;
    }
}
