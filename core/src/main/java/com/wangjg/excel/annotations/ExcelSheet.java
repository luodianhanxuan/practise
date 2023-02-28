package com.wangjg.excel.annotations;


import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ExcelSheet {
    String name() default "";

    String expandAlong() default "Y";
}
