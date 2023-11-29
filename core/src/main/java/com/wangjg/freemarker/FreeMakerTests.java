package com.wangjg.freemarker;

import freemarker.cache.StringTemplateLoader;
import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.*;
import lombok.Data;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class FreeMakerTests {

    /**
     * 使用 freeMarker 引擎替换模板中的变量
     */
    public static String processTemplate(String templateStr, Map<String ,Object> dataModel) {
        Configuration configuration = new Configuration(Configuration.getVersion());
        configuration.setTemplateLoader(new StringTemplateLoader());
        Template template;
        try {
            template = new Template("", new StringReader(templateStr), configuration);
            StringWriter out = new StringWriter();
            template.process(dataModel, out);
            return out.toString();
        } catch (IOException | TemplateException e) {
            throw new RuntimeException("模板引擎处理失败", e);
        }
    }

    public static TemplateHashModel getStaticModel(Class<?> clazz) {
        TemplateHashModel staticModels = new BeansWrapperBuilder(Configuration.VERSION_2_3_21)
                .build().getStaticModels();
        try {
            return (TemplateHashModel)
                    staticModels.get(clazz.getName());
        } catch (TemplateModelException e) {
            throw new RuntimeException(e);
        }
    }

    public static String echo(String s){
        return "echo: " +  s;
    }

    @Data
    public static class Test {
        private String name;
    }

    public static void main(String[] args) throws TemplateModelException {
        Map<String, Object> dataModel = new HashMap<>();
//        dataModel.put("FreeMakerTests", getStaticModels(FreeMakerTests.class));
        dataModel.put("DictEnum", getStaticModel(DictEnum.class));
        Test test = new Test();
        test.setName("1");
        dataModel.put("test", test);
//        String template = "${FreeMakerTests.echo(test.name)}";
        String template = "${DictEnum.getNameByCode(test.name)}";
        System.out.println(processTemplate(template, dataModel));
    }
}
