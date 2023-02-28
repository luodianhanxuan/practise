package com.wangjg.excel.enums;

import com.wangjg.excel.CustomCellContext;
import lombok.Getter;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.function.Consumer;


@Getter
public enum ExcelCustomCellEnum {
    FONT_BOND("字体加粗", (context) -> {
        Workbook wb = context.getWorkbook();
        Font font = wb.createFont();
        font.setBold(true);
        context.getCellStyle().setFont(font);
    }),
    ;

    ExcelCustomCellEnum(String label, Consumer<CustomCellContext> customCell) {
        this.label = label;
        this.customCell = customCell;
    }

    private final String label;

    private final Consumer<CustomCellContext> customCell;

}
