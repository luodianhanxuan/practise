package com.wangjg.table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CellStyle {
    /**
     * 列宽
     */
    private int width;

    /**
     * 行高
     */
    private int height;

    /**
     * 横向对齐方式
     */
    private CellStyles.Alignment hAlignment = CellStyles.Alignment.H_CENTER;

    /**
     * pending
     */
    private int pending = 2;

}
