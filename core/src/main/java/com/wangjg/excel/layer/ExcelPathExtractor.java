package com.wangjg.excel.layer;

import com.google.common.collect.Maps;
import java.io.InputStream;
import java.util.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

@Slf4j
abstract class ExcelPathExtractor {

  private final String sheetName;
  private final Integer sheetIndex;
  private final CellRangeAddress headerRangeStartAddr;

  // 预处理合并单元格信息
  private Map<String, String> mergedCellMap;
  private Map<Integer, List<String>> columnHeaders;

  protected final List<Integer> rowHeaderIndexesOfCol;

  protected ExcelPathExtractor(
      String sheetName, Integer sheetIndex, CellRangeAddress headerRangeStartAddr) {

    if (sheetName == null && sheetIndex == null) {
      throw new IllegalArgumentException("sheetName and sheetIndex must not be all null");
    }

    this.sheetName = sheetName;
    this.sheetIndex = sheetIndex;
    this.headerRangeStartAddr = headerRangeStartAddr;

    List<Integer> rowHeaderColIndex = new ArrayList<>();
    for (int colIndex = headerRangeStartAddr.getFirstColumn();
        colIndex <= headerRangeStartAddr.getLastColumn();
        colIndex++) {
      rowHeaderColIndex.add(colIndex);
    }
    this.rowHeaderIndexesOfCol = rowHeaderColIndex;
  }

  public List<ExcelRowContext> extractPaths(InputStream inputStream) {
    try (Workbook workbook = WorkbookFactory.create(inputStream)) {
      Sheet sheet;

      // 优先采用顺序
      if (sheetName != null) {
        sheet = workbook.getSheet(sheetName);
      } else {
        sheet = workbook.getSheetAt(sheetIndex);
      }

      // 预处理合并单元格
      preProcessMergedCells(sheet);

      // 首先处理列头
      extractColumnHeaders(sheet);

      List<ExcelRowContext> list = new ArrayList<>();
      // 然后处理每一行
      for (Row row : sheet) {
        int startRowIndex = headerRangeStartAddr.getFirstRow();
        if (row.getRowNum() < startRowIndex) {
          log.info(
              "当前行号小于指定的开始行号 -> < rowNum： {}；startRowIndex：{} >", row.getRowNum(), startRowIndex);
          continue;
        }

        ExcelRowContext context = parseRow(row, sheet);
        if (context == null) {
          continue;
        }
        list.add(context);
      }
      return list;
    } catch (Exception e) {
      log.error("提取 ExcelPath 异常", e);
      return new ArrayList<>();
    }
  }

  protected abstract ExcelRowContext parseRow(Row row, Sheet sheet);

  protected ExcelRowContext createContext(Row row, List<String> rowHeaders) {
    return new ExcelRowContext(row, rowHeaders, Maps.newHashMap(this.columnHeaders));
  }

  // 预处理合并单元格
  private void preProcessMergedCells(Sheet sheet) {
    this.mergedCellMap = new HashMap<>();
    for (CellRangeAddress mergedRegion : sheet.getMergedRegions()) {
      Row firstRow = sheet.getRow(mergedRegion.getFirstRow());
      if (firstRow != null) {
        Cell firstCell = firstRow.getCell(mergedRegion.getFirstColumn());
        String firstCellValue = getCellValue(firstCell);

        for (int rowIndex = mergedRegion.getFirstRow();
            rowIndex <= mergedRegion.getLastRow();
            rowIndex++) {
          for (int columnIndex = mergedRegion.getFirstColumn();
              columnIndex <= mergedRegion.getLastColumn();
              columnIndex++) {
            String cellKey = getCellKey(rowIndex, columnIndex);
            this.mergedCellMap.put(cellKey, firstCellValue);
          }
        }
      }
    }
  }

  // 提取列头信息
  private void extractColumnHeaders(Sheet sheet) {
    Map<Integer, List<String>> columnHeaders = new HashMap<>();

    // 假设第一行为列头
    int firstRow = this.headerRangeStartAddr.getFirstRow();
    int lastRow = this.headerRangeStartAddr.getLastRow();
    for (int currentRowIndex = firstRow; currentRowIndex < lastRow + 1; currentRowIndex++) {
      Row currentHeaderRow = sheet.getRow(currentRowIndex);
      if (currentHeaderRow == null) {
        throw new IllegalArgumentException("表格开始位置指定有误：根据 rowColHeader 没有找到有效行");
      }

      for (Cell headerCell : currentHeaderRow) {
        List<String> headerPath =
            columnHeaders.computeIfAbsent(
                headerCell.getColumnIndex(), colIndex -> new ArrayList<>());
        // 获取某个单元格的列头路径，处理合并单元格
        headerPath.add(getCellValue(headerCell));
      }
    }
    this.columnHeaders = columnHeaders;
  }

  // 获取单元格的值，如果单元格是合并单元格，则返回合并单元格的头部值
  protected String getCellValue(Cell cell) {
    if (cell == null) {
      return "";
    }
    String cellKey = getCellKey(cell.getRowIndex(), cell.getColumnIndex());
    return this.mergedCellMap.getOrDefault(cellKey, cell.getStringCellValue());
  }

  // 获取单元格的唯一标识符（行号_列号）
  private String getCellKey(int rowNum, int colNum) {
    return rowNum + "_" + colNum;
  }

  @AllArgsConstructor
  @Getter
  public static class ExcelRowContext {
    private Row row;
    private final List<String> rowHeaders;
    private final Map<Integer, List<String>> columnHeaders;
  }
}
