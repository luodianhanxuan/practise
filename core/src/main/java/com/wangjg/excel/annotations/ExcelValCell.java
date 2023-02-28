package com.wangjg.excel.annotations;

import com.wangjg.excel.enums.ExcelCustomCellEnum;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ExcelValCell {
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
