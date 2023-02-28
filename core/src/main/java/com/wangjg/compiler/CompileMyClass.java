package com.wangjg.compiler;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class CompileMyClass {

    public static void main(String[] args) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        int result = compiler.run(null, null, null, "/Users/wangjg/workspace/java/base/core/src/main/java/com/wangjg/compiler/MyClass.java");
        System.out.println("Compile result code = " + result);
    }
}
