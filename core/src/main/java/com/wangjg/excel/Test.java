package com.wangjg.excel;

import com.wangjg.excel.annotations.ExcelSheet;
import com.wangjg.excel.annotations.ExcelTitleCell;
import com.wangjg.excel.annotations.ExcelValCell;
import com.wangjg.excel.enums.ExcelCustomCellEnum;
import lombok.Data;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ExcelSheet(name = "测试 Sheet")
@Data
public class Test {
    @ExcelTitleCell(name = "f1", x1Axis = 0, y1Axis = 0, customCell = ExcelCustomCellEnum.FONT_BOND)
    @ExcelValCell(x1Axis = 0, y1Axis = 1)
    private String f1;

    @ExcelTitleCell(name = "f2", x1Axis = 1, y1Axis = 0, customCell = ExcelCustomCellEnum.FONT_BOND)
    @ExcelValCell(x1Axis = 1, y1Axis = 1)
    private String f2;

    @ExcelTitleCell(name = "f3", x1Axis = 2, y1Axis = 0, customCell = ExcelCustomCellEnum.FONT_BOND)
    @ExcelValCell(x1Axis = 2, y1Axis = 1)
    private String f3;

    @ExcelTitleCell(name = "f4", x1Axis = 3, y1Axis = 0, customCell = ExcelCustomCellEnum.FONT_BOND)
    @ExcelValCell(x1Axis = 3, y1Axis = 1)
    private String f4;


    public static void main(String[] args) throws IllegalAccessException, IOException {
        HSSFWorkbook workbook = new HSSFWorkbook();
        ExcelHelper<Test> helper = new ExcelHelper<>(workbook);

        Test t1 = new Test();
        t1.setF1("f1");
        t1.setF2("f2");
        t1.setF3("f3");
        t1.setF4("f4");

        Test t2 = new Test();
        t2.setF1("f1");
        t2.setF2("f2");
        t2.setF3("f3");
        t2.setF4("f4");

        List<Test> datas = new ArrayList<>();
        datas.add(t1);
        datas.add(t2);

        helper.dealSheet(datas);
        helper.saveExcel("test.xls");

    }
}
