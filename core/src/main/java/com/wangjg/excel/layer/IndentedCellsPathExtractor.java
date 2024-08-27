package com.wangjg.excel.layer;

import java.util.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;

public class IndentedCellsPathExtractor extends ExcelPathExtractor {

  private final Deque<Cell> pathStack = new ArrayDeque<>();
  private int previousIndentLevel = -1;
  private final CellAddress headerStartAddress;

  protected IndentedCellsPathExtractor(String sheetName, CellAddress headerStartAddress) {
    this(sheetName, null, headerStartAddress);
  }

  protected IndentedCellsPathExtractor(Integer sheetIndex, CellAddress headerStartAddress) {
    this(null, sheetIndex, headerStartAddress);
  }

  protected IndentedCellsPathExtractor(
      String sheetName, Integer sheetIndex, CellAddress headerStartAddress) {

    super(
        sheetName,
        sheetIndex,
        new CellRangeAddress(
            headerStartAddress.getRow(),
            headerStartAddress.getRow(),
            headerStartAddress.getColumn(),
            headerStartAddress.getColumn()));
    this.headerStartAddress = headerStartAddress;
  }

  @Override
  protected ExcelRowContext parseRow(Row row, Sheet sheet) {

    Cell headerCell = row.getCell(this.headerStartAddress.getColumn());
    // 获取当前行的缩进级别
    int currentIndentLevel = getIndentLevel(headerCell);

    if (currentIndentLevel > previousIndentLevel) {
      pathStack.push(headerCell);
    } else if (currentIndentLevel == previousIndentLevel) {
      pathStack.pop();
      pathStack.push(headerCell);
    } else {
      while (currentIndentLevel < previousIndentLevel) {
        previousIndentLevel = getIndentLevel(pathStack.pop());
      }
      if (!pathStack.isEmpty()) {
        pathStack.pop();
      }
      pathStack.push(headerCell);
    }

    previousIndentLevel = currentIndentLevel;

    List<String> fullPath = new ArrayList<>();
    pathStack
        .descendingIterator()
        .forEachRemaining(
            cell -> {
              String cellValue = getCellValue(cell);
              fullPath.add(cellValue);
            });
    return createContext(row, fullPath);
  }

  private int getIndentLevel(Cell cell) {
    CellStyle cellStyle = cell.getCellStyle();
    return cellStyle.getIndention();
  }
}
