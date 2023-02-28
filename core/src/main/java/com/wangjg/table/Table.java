package com.wangjg.table;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

@Data
public class Table {

    /**
     * 包含行数
     */
    private int row;

    /**
     * 包含列数
     */
    private int col;

    /**
     * 行风格
     */
    private CellStyle[][] cellStyles;

    /**
     * 表格体内容
     */
    private Cell[][] body;

    public void print() {
        System.out.print(draw());
    }

    public String draw() {
        StringBuilder drawResult = new StringBuilder();

        for (int i = 0; i < body.length; i++) {
            if (i == body.length - 1) {
                drawResult.append(drawRow(body[i], cellStyles[i], true));
            } else {
                drawResult.append(drawRow(body[i], cellStyles[i], false));
            }

        }

        return drawResult.toString();
    }

    private String drawRow(Cell[] cells, CellStyle[] styles, boolean drawDown) {
        Cell cell;
        Map<Integer, StringJoiner> sbOfLines = new HashMap<>();
        for (int i = 0; i < cells.length; i++) {
            cell = cells[i];
            CellStyle cellStyle = styles[i];
            cell.setCellStyle(cellStyle);
            String cellStr = cell.draw(drawDown);

            String[] split = cellStr.split("\n");
            for (int j = 0; j < split.length; j++) {
                String rowS = split[j];
                StringJoiner sj = sbOfLines.computeIfAbsent(j, key -> new StringJoiner("\b"));
                sj.add(rowS);
            }
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sbOfLines.values().size(); i++) {
            sb.append(sbOfLines.get(i).toString()).append("\n");
        }

        return sb.toString();
    }


}
