package com.wangjg.table;

import com.google.common.collect.HashBasedTable;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

public class TableDrawer {

    /**
     * 包含行数
     */
    private int row;

    /**
     * 包含列数
     */
    private int col;

    /**
     * 最大宽度（按字符记）
     */
    private int maxWid = Integer.MAX_VALUE;

    /**
     * 行列风格设置
     */
    private final HashBasedTable<Integer, Integer, CellStyle> cellStyles = HashBasedTable.create();


    /**
     * 表格体内容
     */
    private List<List<Cell>> rows;


    public static TableDrawer init() {
        return new TableDrawer();
    }


    public TableDrawer maxWid(int i) {
        this.maxWid = i;
        return this;
    }

    public TableDrawer header(String... headerContents) {
        appendRow(headerContents);
        return this;
    }

    public TableDrawer appendRow(String... rowContents) {
        if (col == 0) {
            col = rowContents.length;
        } else if (!Objects.equals(col, rowContents.length)) {
            throw new IllegalArgumentException("表头长度和表格的列数不一致");
        }

        if (rows == null) {
            rows = new ArrayList<>();
        }
        row++;
        List<Cell> rowList = new ArrayList<>(col);
        for (int i = 1; i <= col; i++) {
            rowContents[i - 1] = foldContentIfNeed(rowContents[i - 1]);
            rowList.add(new Cell(row, i, rowContents[i - 1]));
        }

        rows.add(rowList);

        return this;
    }

    private String foldContentIfNeed(String rowContent) {
        int length = rowContent.length();
        if (length <= maxWid) {
            return rowContent;
        }
        StringJoiner sj = new StringJoiner("\n");
        while (rowContent.length() > maxWid) {
            String substring = rowContent.substring(0, maxWid);
            rowContent = rowContent.substring(maxWid);
            sj.add(substring);
        }
        sj.add(rowContent);
        return sj.toString();
    }

    private int calcWidth(String s) {
        return s.getBytes().length;
    }

    private int calcHeight(String s) {
        return s.split("\n").length == 0 ? 1 : s.split("\n").length;
    }


    public Table build() {
        Table table = new Table();
        // 表格的总行数为表格体的行数 + 1 （表头）
        int tableRow = rows.size();
        int tableCol = col;
        table.setRow(tableRow);
        table.setCol(tableCol);

        // 表格体
        Cell[][] bodyArr = getBodyArr(tableRow);
        table.setBody(bodyArr);

        CellStyle[][] cellStylesArr = new CellStyle[tableRow][];

        // 初始化表格样式
        CellStyle[] rowCellStyle;
        for (int i = 0; i < tableRow; i++) {
            rowCellStyle = new CellStyle[tableCol];
            for (int j = 0; j < tableCol; j++) {
                CellStyle cellStyle = cellStyles.get(i, j);
                if (cellStyle == null) {
                    cellStyle = new CellStyle();
                    cellStyles.put(i, j, cellStyle);
                }
                rowCellStyle[j] = cellStyle;
            }
            cellStylesArr[i] = rowCellStyle;
        }

        // 设置行列长度高度
        autoSetRowColWidthHeight(table);

        table.setCellStyles(cellStylesArr);

        return table;
    }


    private void autoSetRowColWidthHeight(Table table) {
        Cell[][] body = table.getBody();

        int[] widthMax = new int[table.getCol()];
        int[] heightMax = new int[table.getRow()];

        // 寻找行列最大值
        Consumer<Cell> getMaxWidthHeightConsumer = cell -> {
            int h = calcHeight(cell.getOrginVal());
            if (h > 1) {
                cell.setVal(cell.getOrginVal().split("\n"));
                for (int i = 0; i < h; i++) {
                    int w = calcWidth(cell.getVal()[i]);
                    widthMax[cell.getCol() - 1] = Math.max(widthMax[cell.getCol() - 1], w);
                }
            } else {
                int w = calcWidth(cell.getOrginVal());
                cell.setVal(new String[]{cell.getOrginVal()});
                widthMax[cell.getCol() - 1] = Math.max(widthMax[cell.getCol() - 1], w);
            }

            heightMax[cell.getRow() - 1] = Math.max(heightMax[cell.getRow() - 1], h);
        };

        Arrays.stream(body).flatMap(Arrays::stream).forEach(getMaxWidthHeightConsumer);


        // 设置行列长度最大值
        Consumer<Cell> consumer = cell -> {
            cellStyles.column(cell.getCol() - 1).values().forEach(i -> i.setWidth(widthMax[cell.getCol() - 1]));
            cellStyles.row(cell.getRow() - 1).values().forEach(i -> i.setHeight(heightMax[cell.getRow() - 1]));
        };

        Arrays.stream(body).flatMap(Arrays::stream).forEach(consumer);
    }

    private Cell[][] getBodyArr(int tableRow) {
        Cell[][] bodyArr = new Cell[tableRow][];
        Cell[] tmp;
        for (int i = 0; i < rows.size(); i++) {
            tmp = rows.get(i).toArray(new Cell[0]);
            bodyArr[i] = tmp;
        }
        return bodyArr;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
//        TableDrawer.init()
//                .appendRow("asasdf", "def", "ghi")
//                .appendRow("1123", "123123", "12312312")
//                .appendRow("123123", "123123", "123123123")
//                .appendRow("12", "123", "1")
//                .appendRow("1231`", "123", "123123as")
//                .appendRow("123123", "123", "q1234")
//                .build()
//                .print();
        int i = 0;
        while (true) {
            i++;

            // 通过移动光标，实现覆盖式循环输出
            System.out.printf("\r\t 打印第%d轮", i);
            System.out.print("\n\r\t 这是第二行");
            System.out.print("\n\r\t 这是第三行");
            System.out.print("\n\r\t 这是第四行");
            System.out.print("\n\r\t 这是第五行\r\b\r\b\r\b\r\b");

            Thread.sleep(2000);
        }
    }
}