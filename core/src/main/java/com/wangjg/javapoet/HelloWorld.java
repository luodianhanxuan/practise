package com.wangjg.javapoet;

import com.squareup.javapoet.*;
import okhttp3.Headers;
import org.junit.Test;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.LogRecord;


public class HelloWorld {

    /*
    package com.wangjg.helloworld;

    public final class HelloWorld {
      public static void main(String[] args) {
        System.out.println("Hello, JavaPoet!");
      }
    }
     */
    @Test
    public void test1() throws IOException {
        MethodSpec main = MethodSpec.methodBuilder("main")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addParameter(String[].class, "args")
                .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")
                .build();

        TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(main)
                .build();

        JavaFile javaFile = JavaFile.builder("com.wangjg.helloworld", helloWorld)
                .build();

        javaFile.writeTo(System.out);
    }

    /*
    package com.wangjg.helloworld;

    public final class HelloWorld {
      void main() {
        int total = 0;
        for (int i = 0; i < 10; i++) {
          total += i;
        }
      }
    }
     */
    @Test
    public void test2() throws IOException {
        MethodSpec main = MethodSpec.methodBuilder("main")
                .addCode(""
                        + "int total = 0;\n"
                        + "for (int i = 0; i < 10; i++) {\n"
                        + "  total += i;\n"
                        + "}\n")
                .build();

        TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(main)
                .build();

        JavaFile javaFile = JavaFile.builder("com.wangjg.helloworld", helloWorld)
                .build();

        javaFile.writeTo(System.out);
    }

    /*
    package com.wangjg.helloworld;

    public final class HelloWorld {
      void main() {
        int total = 0;
        for (int i = 0; i < 10; i++) {
          total += i;
        }
      }
    }
     */
    @Test
    public void test3() throws IOException {
        MethodSpec main = MethodSpec.methodBuilder("main")
                .addStatement("int total = 0")
                .beginControlFlow("for (int i = 0; i < 10; i++)")
                .addStatement("total += i")
                .endControlFlow()
                .build();

        TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(main)
                .build();

        JavaFile javaFile = JavaFile.builder("com.wangjg.helloworld", helloWorld)
                .build();

        javaFile.writeTo(System.out);
    }

    private MethodSpec computeRange(String name, int from, int to, String op) {
        return MethodSpec.methodBuilder(name)
                .returns(int.class)
                .addStatement("int result = 0")
                .beginControlFlow("for (int i = $L; i < $L; i++)", from, to)
                .addStatement("result = result $L i", op)
                .endControlFlow()
                .addStatement("return result")
                .build();
    }

    private MethodSpec computeRange0(String name, int from, int to, String op) {
        return MethodSpec.methodBuilder(name)
                .returns(int.class)
                .addStatement("int result = 1")
                .beginControlFlow("for (int i = " + from + "; i < " + to + "; i++)")
                .addStatement("result = result " + op + " i")
                .endControlFlow()
                .addStatement("return result")
                .build();
    }

    /*
    package com.wangjg.helloworld;

    public final class HelloWorld {
      int multiply10to20() {
        int result = 0;
        for (int i = 10; i < 20; i++) {
          result = result * i;
        }
        return result;
      }
    }

     */
    @Test
    public void test4() throws IOException {
        MethodSpec method = computeRange("multiply10to20", 10, 20, "*");

        TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(method)
                .build();

        JavaFile javaFile = JavaFile.builder("com.wangjg.helloworld", helloWorld)
                .build();

        javaFile.writeTo(System.out);
    }

    /*
    package com.wangjg.helloworld;

    import java.lang.String;

    public final class HelloWorld {
      String slimShady() {
        return "slimShady";
      }

      String eminem() {
        return "eminem";
      }

      String marshallMathers() {
        return "marshallMathers";
      }
    }
     */
    @Test
    public void test8() throws IOException {
        TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(whatsMyName("slimShady"))
                .addMethod(whatsMyName("eminem"))
                .addMethod(whatsMyName("marshallMathers"))
                .build();

        JavaFile javaFile = JavaFile.builder("com.wangjg.helloworld", helloWorld)
                .build();

        javaFile.writeTo(System.out);
    }

    private MethodSpec whatsMyName(String name) {
        return MethodSpec.methodBuilder(name)
                .returns(String.class)
                .addStatement("return $S", name)
                .build();
    }

    /*
    package com.example.helloworld;

    import java.util.Date;

    public final class HelloWorld {
      Date today() {
        return new Date();
      }
    }
     */
    @Test
    public void test9() throws IOException {
        MethodSpec today = MethodSpec.methodBuilder("today")
                .returns(Date.class)
                .addStatement("return new $T()", Date.class)
                .build();

        TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(today)
                .build();

        JavaFile javaFile = JavaFile.builder("com.wangjg.helloworld", helloWorld)
                .build();

        javaFile.writeTo(System.out);
    }

    /*
    package com.wangjg.helloworld;

    import com.mattel.Hoverboard;

    public final class HelloWorld {
      Hoverboard tomorrow() {
        return new Hoverboard();
      }
    }
     */
    @Test
    public void test10() throws IOException {
        ClassName hoverboard = ClassName.get("com.mattel", "Hoverboard");

        MethodSpec today = MethodSpec.methodBuilder("tomorrow")
                .returns(hoverboard)
                .addStatement("return new $T()", hoverboard)
                .build();

        TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(today)
                .build();

        JavaFile javaFile = JavaFile.builder("com.wangjg.helloworld", helloWorld)
                .build();

        javaFile.writeTo(System.out);
    }


    /*
    package com.wangjg.helloworld;

    import java.lang.String;

    public final class HelloWorld {
      char hexDigit(int i) {
        return (char) (i < 10 ? i + '0' : i - 10 + 'a');
      }

      String byteToHex(int b) {
        char[] result = new char[2];
        result[0] = hexDigit((b >>> 4) & 0xf);
        result[1] = hexDigit(b & 0xf);
        return new String(result);
      }
    }
     */

    /**
     * Generated code is often self-referential.
     * Use $N to refer to another generated declaration by its name. Here's a method that calls another:
     */
    @Test
    public void test13() throws IOException {
        MethodSpec hexDigit = MethodSpec.methodBuilder("hexDigit")
                .addParameter(int.class, "i")
                .returns(char.class)
                .addStatement("return (char) (i < 10 ? i + '0' : i - 10 + 'a')")
                .build();

        MethodSpec byteToHex = MethodSpec.methodBuilder("byteToHex")
                .addParameter(int.class, "b")
                .returns(String.class)
                .addStatement("char[] result = new char[2]")
                .addStatement("result[0] = $N((b >>> 4) & 0xf)", hexDigit)
                .addStatement("result[1] = $N(b & 0xf)", hexDigit)
                .addStatement("return new String(result)")
                .build();

        TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(hexDigit)
                .addMethod(byteToHex)
                .build();

        JavaFile javaFile = JavaFile.builder("com.wangjg.helloworld", helloWorld)
                .build();

        javaFile.writeTo(System.out);
    }

    public String byteToHex(int b) {
        char[] result = new char[2];
        result[0] = hexDigit((b >>> 4) & 0xf);
        result[1] = hexDigit(b & 0xf);
        return new String(result);
    }

    public char hexDigit(int i) {
        return (char) (i < 10 ? i + '0' : i - 10 + 'a');
    }
    /*
    package com.wangjg.helloworld;

    import java.lang.System;

    public final class HelloWorld {
      void main() {
        long now = System.currentTimeMillis();
        if (System.currentTimeMillis() < now) {
          System.out.println("Time travelling, woo hoo!");
        } else if (System.currentTimeMillis() == now) {
          System.out.println("Time stood still!");
        } else {
          System.out.println("Ok, time still moving forward");
        }
      }
    }
     */

    @Test
    public void test5() throws IOException {
        MethodSpec main = MethodSpec.methodBuilder("main")
                .addStatement("long now = $T.currentTimeMillis()", System.class)
                .beginControlFlow("if ($T.currentTimeMillis() < now)", System.class)
                .addStatement("$T.out.println($S)", System.class, "Time travelling, woo hoo!")
                .nextControlFlow("else if ($T.currentTimeMillis() == now)", System.class)
                .addStatement("$T.out.println($S)", System.class, "Time stood still!")
                .nextControlFlow("else")
                .addStatement("$T.out.println($S)", System.class, "Ok, time still moving forward")
                .endControlFlow()
                .build();

        TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(main)
                .build();

        JavaFile javaFile = JavaFile.builder("com.wangjg.helloworld", helloWorld)
                .build();

        javaFile.writeTo(System.out);
    }


    /*
    package com.wangjg.helloworld;

    import java.lang.Exception;
    import java.lang.RuntimeException;

    public final class HelloWorld {
      void main() {
        try {
          throw new Exception("Failed");
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    }
     */

    @Test
    public void test6() throws IOException {
        MethodSpec main = MethodSpec.methodBuilder("main")
                .beginControlFlow("try")
                .addStatement("throw new Exception($S)", "Failed")
                .nextControlFlow("catch ($T e)", Exception.class)
                .addStatement("throw new $T(e)", RuntimeException.class)
                .endControlFlow()
                .build();

        TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(main)
                .build();

        JavaFile javaFile = JavaFile.builder("com.wangjg.helloworld", helloWorld)
                .build();

        javaFile.writeTo(System.out);
    }

    /*
    package com.wangjg.helloworld;

    import java.lang.Exception;
    import java.lang.RuntimeException;

    public final class HelloWorld {
      void main() {
        try {
          throw new Exception("Failed");
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    }
     */
    @Test
    public void test7() throws IOException {
        MethodSpec main = MethodSpec.methodBuilder("main")
                .beginControlFlow("try")
                .addStatement("throw new Exception($S)", "Failed")
                .nextControlFlow("catch ($T e)", Exception.class)
                .addStatement("throw new $T(e)", RuntimeException.class)
                .endControlFlow()
                .build();

        TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(main)
                .build();

        JavaFile javaFile = JavaFile.builder("com.wangjg.helloworld", helloWorld)
                .build();

        javaFile.writeTo(System.out);
    }

    /*
    package com.wangjg.helloworld;

    import com.mattel.Hoverboard;
    import java.util.ArrayList;
    import java.util.List;

    public final class HelloWorld {
      List<Hoverboard> beyond() {
        List<Hoverboard> result = new ArrayList<>();
        result.add(new Hoverboard());
        result.add(new Hoverboard());
        result.add(new Hoverboard());
        return result;
      }
    }
     */
    @Test
    public void test11() throws IOException {
        ClassName hoverboard = ClassName.get("com.mattel", "Hoverboard");
        ClassName list = ClassName.get("java.util", "List");
        ClassName arrayList = ClassName.get("java.util", "ArrayList");
        TypeName listOfHoverboards = ParameterizedTypeName.get(list, hoverboard);

        MethodSpec beyond = MethodSpec.methodBuilder("beyond")
                .returns(listOfHoverboards)
                .addStatement("$T result = new $T<>()", listOfHoverboards, arrayList)
                .addStatement("result.add(new $T())", hoverboard)
                .addStatement("result.add(new $T())", hoverboard)
                .addStatement("result.add(new $T())", hoverboard)
                .addStatement("return result")
                .build();

        TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(beyond)
                .build();

        JavaFile javaFile = JavaFile.builder("com.wangjg.helloworld", helloWorld)
                .build();

        javaFile.writeTo(System.out);
    }

    /*
    package com.wangjg.helloworld;

    import static com.mattel.Hoverboard.Boards.*;
    import static com.mattel.Hoverboard.createNimbus;
    import static java.util.Collections.*;

    import com.mattel.Hoverboard;
    import java.util.ArrayList;
    import java.util.List;

    class HelloWorld {
      List<Hoverboard> beyond() {
        List<Hoverboard> result = new ArrayList<>();
        result.add(createNimbus(2000));
        result.add(createNimbus("2001"));
        result.add(createNimbus(THUNDERBOLT));
        sort(result);
        return result.isEmpty() ? emptyList() : result;
      }
    }

     */
    @Test
    public void test12() throws IOException {
        ClassName hoverboard = ClassName.get("com.mattel", "Hoverboard");
        ClassName list = ClassName.get("java.util", "List");
        ClassName arrayList = ClassName.get("java.util", "ArrayList");
        TypeName listOfHoverboards = ParameterizedTypeName.get(list, hoverboard);

        ClassName namedBoards = ClassName.get("com.mattel", "Hoverboard", "Boards");

        MethodSpec beyond = MethodSpec.methodBuilder("beyond")
                .returns(listOfHoverboards)
                .addStatement("$T result = new $T<>()", listOfHoverboards, arrayList)
                .addStatement("result.add($T.createNimbus(2000))", hoverboard)
                .addStatement("result.add($T.createNimbus(\"2001\"))", hoverboard)
                .addStatement("result.add($T.createNimbus($T.THUNDERBOLT))", hoverboard, namedBoards)
                .addStatement("$T.sort(result)", Collections.class)
                .addStatement("return result.isEmpty() ? $T.emptyList() : result", Collections.class)
                .build();

        TypeSpec hello = TypeSpec.classBuilder("HelloWorld")
                .addMethod(beyond)
                .build();

        JavaFile javaFile = JavaFile.builder("com.wangjg.helloworld", hello)
                .addStaticImport(hoverboard, "createNimbus")
                .addStaticImport(namedBoards, "*")
                .addStaticImport(Collections.class, "*")
                .build();

        javaFile.writeTo(System.out);
    }

    /*
    package com.wangjg.helloworld;

    public abstract class HelloWorld {
      protected abstract void flux();
    }
     */
    @Test
    public void test14() throws IOException {
        MethodSpec flux = MethodSpec.methodBuilder("flux")
                .addModifiers(Modifier.ABSTRACT, Modifier.PROTECTED)
                .build();

        TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addMethod(flux)
                .build();

        JavaFile javaFile = JavaFile.builder("com.wangjg.helloworld", helloWorld)
                .build();

        javaFile.writeTo(System.out);

    }

    /*
    package com.wangjg.helloworld;

    import java.lang.String;

    public class HelloWorld {
      private final String greeting;

      public HelloWorld(String greeting) {
        this.greeting = greeting;
      }
    }
     */
    @Test
    public void test15() throws IOException {
        MethodSpec flux = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(String.class, "greeting")
                .addStatement("this.$N = $N", "greeting", "greeting")
                .build();

        TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
                .addModifiers(Modifier.PUBLIC)
                .addField(String.class, "greeting", Modifier.PRIVATE, Modifier.FINAL)
                .addMethod(flux)
                .build();

        JavaFile javaFile = JavaFile.builder("com.wangjg.helloworld", helloWorld)
                .build();

        javaFile.writeTo(System.out);
    }

    /*
    package com.wangjg.helloworld;

    import java.lang.String;

    public class HelloWorld {
      private final String android;

      private final String robot;
    }
     */
    @Test
    public void test16() throws IOException {
        FieldSpec android = FieldSpec.builder(String.class, "android")
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .build();

        TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
                .addModifiers(Modifier.PUBLIC)
                .addField(android)
                .addField(String.class, "robot", Modifier.PRIVATE, Modifier.FINAL)
                .build();

        JavaFile javaFile = JavaFile.builder("com.wangjg.helloworld", helloWorld)
                .build();

        javaFile.writeTo(System.out);
    }

    /*
    package com.wangjg.helloworld;

    import java.lang.String;

    public class HelloWorld {
      private final String android = "Lollipop v." + 5.0;

      private final String robot;
    }
     */
    @Test
    public void test17() throws IOException {
        FieldSpec android = FieldSpec.builder(String.class, "android")
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .initializer("$S + $L", "Lollipop v.", 5.0d)
                .build();

        TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
                .addModifiers(Modifier.PUBLIC)
                .addField(android)
                .addField(String.class, "robot", Modifier.PRIVATE, Modifier.FINAL)
                .build();

        JavaFile javaFile = JavaFile.builder("com.wangjg.helloworld", helloWorld)
                .build();

        javaFile.writeTo(System.out);
    }

    /*
    package com.wangjg.helloworld;

    import java.lang.String;

    public interface HelloWorld {
      String ONLY_THING_THAT_IS_CONSTANT = "change";

      void beep();
    }

     */
    @Test
    public void test18() throws IOException {
        TypeSpec helloWorld = TypeSpec.interfaceBuilder("HelloWorld")
                .addModifiers(Modifier.PUBLIC)
                .addField(FieldSpec.builder(String.class, "ONLY_THING_THAT_IS_CONSTANT")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                        .initializer("$S", "change")
                        .build())
                .addMethod(MethodSpec.methodBuilder("beep")
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .build())
                .build();

        JavaFile javaFile = JavaFile.builder("com.wangjg.helloworld", helloWorld)
                .build();

        javaFile.writeTo(System.out);
    }


    /*
    package com.wangjg.helloworld;

    public enum Roshambo {
      ROCK,

      SCISSORS,

      PAPER
    }

     */
    @Test
    public void test19() throws IOException {
        TypeSpec helloWorld = TypeSpec.enumBuilder("Roshambo")
                .addModifiers(Modifier.PUBLIC)
                .addEnumConstant("ROCK")
                .addEnumConstant("SCISSORS")
                .addEnumConstant("PAPER")
                .build();

        JavaFile javaFile = JavaFile.builder("com.wangjg.helloworld", helloWorld)
                .build();

        javaFile.writeTo(System.out);
    }

    /*
    package com.wangjg.helloworld;

    import java.lang.Override;
    import java.lang.String;

    public enum Roshambo {
      ROCK("fist") {
        @Override
        public String toString() {
          return "avalanche!";
        }
      },

      SCISSORS("peace"),

      PAPER("flat");

      private final String handsign;

      Roshambo(String handsign) {
        this.handsign = handsign;
      }
    }
     */
    @Test
    public void test20() throws IOException {
        TypeSpec helloWorld = TypeSpec.enumBuilder("Roshambo")
                .addModifiers(Modifier.PUBLIC)
                .addEnumConstant("ROCK", TypeSpec.anonymousClassBuilder("$S", "fist")
                        .addMethod(MethodSpec.methodBuilder("toString")
                                .addAnnotation(Override.class)
                                .addModifiers(Modifier.PUBLIC)
                                .addStatement("return $S", "avalanche!")
                                .returns(String.class)
                                .build())
                        .build())
                .addEnumConstant("SCISSORS", TypeSpec.anonymousClassBuilder("$S", "peace")
                        .build())
                .addEnumConstant("PAPER", TypeSpec.anonymousClassBuilder("$S", "flat")
                        .build())
                .addField(String.class, "handsign", Modifier.PRIVATE, Modifier.FINAL)
                .addMethod(MethodSpec.constructorBuilder()
                        .addParameter(String.class, "handsign")
                        .addStatement("this.$N = $N", "handsign", "handsign")
                        .build())
                .build();

        JavaFile javaFile = JavaFile.builder("com.wangjg.helloworld", helloWorld)
                .build();

        javaFile.writeTo(System.out);
    }

    /*
    package com.wangjg.helloworld;

    import java.lang.Override;
    import java.lang.String;
    import java.util.Collections;
    import java.util.Comparator;
    import java.util.List;

    class HelloWorld {
      void sortByLength(List<String> strings) {
        Collections.sort(strings, new Comparator<String>() {
          @Override
          public int compare(String a, String b) {
            return a.length() - b.length();
          }
        });
      }
    }
     */
    @Test
    public void test21() throws IOException {
        TypeSpec comparator = TypeSpec.anonymousClassBuilder("")
                .addSuperinterface(ParameterizedTypeName.get(Comparator.class, String.class))
                .addMethod(MethodSpec.methodBuilder("compare")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(String.class, "a")
                        .addParameter(String.class, "b")
                        .returns(int.class)
                        .addStatement("return $N.length() - $N.length()", "a", "b")
                        .build())
                .build();

        TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
                .addMethod(MethodSpec.methodBuilder("sortByLength")
                        .addParameter(ParameterizedTypeName.get(List.class, String.class), "strings")
                        .addStatement("$T.sort($N, $L)", Collections.class, "strings", comparator)
                        .build())
                .build();

        JavaFile javaFile = JavaFile.builder("com.wangjg.helloworld", helloWorld)
                .build();

        javaFile.writeTo(System.out);
    }

    /*
    package com.wangjg.helloworld;

    import java.lang.Override;
    import java.lang.String;

    class HelloWorld {
      @Override
      public String toString() {
        return "Hoverboard";
      }
    }
     */
    @Test
    public void test22() throws IOException {
        MethodSpec toString = MethodSpec.methodBuilder("toString")
                .addAnnotation(Override.class)
                .returns(String.class)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return $S", "Hoverboard")
                .build();
        TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
                .addMethod(toString)
                .build();
        JavaFile javaFile = JavaFile.builder("com.wangjg.helloworld", helloWorld)
                .build();

        javaFile.writeTo(System.out);

    }

    /*
    package com.wangjg.helloworld;

    import java.util.logging.LogRecord;
    import okhttp3.Headers;

    class HelloWorld {
      @Headers(
          accept = "application/json; charset=utf-8",
          userAgent = "Square Cash"
      )
      public void recordEvent(LogRecord logRecord) {
      }
    }
     */
    @Test
    public void test23() throws IOException {
        MethodSpec logRecord = MethodSpec.methodBuilder("recordEvent")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(AnnotationSpec.builder(Headers.class)
                        .addMember("accept", "$S", "application/json; charset=utf-8")
                        .addMember("userAgent", "$S", "Square Cash")
                        .build())
                .addParameter(LogRecord.class, "logRecord")
                .returns(void.class)
                .build();

        TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
                .addMethod(logRecord)
                .build();

        JavaFile javaFile = JavaFile.builder("com.wangjg.helloworld", helloWorld)
                .build();

        javaFile.writeTo(System.out);
    }
}
