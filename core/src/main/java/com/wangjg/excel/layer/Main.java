package com.wangjg.excel.layer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellAddress;

public class Main {

  public static void main(String[] args) throws FileNotFoundException {
    File file =
        new File(
            "xxxxx");
    FileInputStream fileInputStream = new FileInputStream(file);

    IndentedCellsPathExtractor extractor =
        new IndentedCellsPathExtractor("xxxxx", new CellAddress("B4"));

    List<ExcelPathExtractor.ExcelRowContext> excelRowContexts =
        extractor.extractPaths(fileInputStream);

    CellAddress readCellAddr = new CellAddress("D25");

    excelRowContexts.stream()
        .filter(context -> Objects.equals(context.getRow().getRowNum(), readCellAddr.getRow()))
        .forEach(
            context -> {
              Row row = context.getRow();
              Map<Integer, List<String>> columnHeaders = context.getColumnHeaders();
              List<String> rowHeaders = context.getRowHeaders();
              List<String> allPath = new ArrayList<>(rowHeaders);
              allPath.addAll(columnHeaders.get(readCellAddr.getColumn()));
              Cell cell = row.getCell(readCellAddr.getColumn());
              String value = cell.getStringCellValue();
              System.out.println(String.join("->", allPath) + ":" + value);
            });
  }
}
