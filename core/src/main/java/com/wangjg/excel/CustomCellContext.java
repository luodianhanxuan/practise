package com.wangjg.excel;

import lombok.Data;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

@Data
public class CustomCellContext {
    /**
     * 该单元格的位置以及值定义
     */
    private ExcelHelper.CellDefinition cellDefinition;

    /**
     * 该单元格所属的工作簿
     */
    private Workbook workbook;

    /**
     * 该单元格所属的 sheet
     */
    private Sheet sheet;

    /**
     * 该单元格对象
     */
    private Cell cell;

    /**
     * 单元格所属 CellStyle
     */
    private CellStyle cellStyle;

}
