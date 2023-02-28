package com.wangjg.annotationprocessor;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

@SupportedAnnotationTypes("com.wangjg.annotationprocessor.MyAnnotation")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class MyProcessor extends AbstractProcessor {
    Messager messager;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        messager = processingEnv.getMessager();
        messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING, "--------------- init 123! ---------------");
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(MyAnnotation.class);
        if (elementsAnnotatedWith == null || elementsAnnotatedWith.size() <= 0) {
            return false;
        }

//        System.out.println("--------------- process 123! ---------------");
        messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING, "--------------- process 123! ---------------");
        return false;
    }

}
