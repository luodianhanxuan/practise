package com.wangjg.excel.layer;

import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

public class MergedCellsPathExtractor extends ExcelPathExtractor {

  public MergedCellsPathExtractor(String sheetName, CellRangeAddress startAddress) {
    this(sheetName, null, startAddress);
  }

  public MergedCellsPathExtractor(Integer sheetIndex, CellRangeAddress startAddress) {
    this(null, sheetIndex, startAddress);
  }

  protected MergedCellsPathExtractor(
      String sheetName, Integer sheetIndex, CellRangeAddress startAddress) {
    super(sheetName, sheetIndex, startAddress);
  }

  @Override
  protected ExcelRowContext parseRow(Row row, Sheet sheet) {
    List<String> rowHeaders = new ArrayList<>();

    for (Integer headerColIndex : rowHeaderIndexesOfCol) {
      Cell headerCell = row.getCell(headerColIndex);
      String cellValue = getCellValue(headerCell);
      rowHeaders.add(cellValue);
    }

    return createContext(row, rowHeaders);
  }
}
