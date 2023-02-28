package com.wangjg.excel.annotations;

import com.wangjg.excel.enums.ExcelCustomCellEnum;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ExcelTitleCell {
    /**
     * @return 该单元格的表头，ExcelSheet.expand 值为 Func 时，不需要指定此字段，其他必须指定 Title 名称
     */
    String name() default "";

    /**
     * @return 起始单元格 x 轴方向坐标，自左而右，从 0 开始
     */
    int x1Axis();

    /**
     * @return 起始单元格 y 轴方向坐标，自上而下，从 0 开始
     */
    int y1Axis();

    /**
     * @return 单元格的样式设置回调
     */
    ExcelCustomCellEnum[] customCell() default {};
}
