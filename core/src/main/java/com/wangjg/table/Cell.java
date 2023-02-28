package com.wangjg.table;

import lombok.Getter;
import lombok.Setter;

import java.util.StringJoiner;

@Getter
@Setter
public class Cell {
    /**
     * 行号
     */
    private int row;

    /**
     * 列号
     */
    private int col;

    /**
     * 值
     */
    private String orginVal;

    private String[] val;

    /**
     * 单元格样式
     */
    private CellStyle cellStyle;

    public Cell() {
    }

    public Cell(int row, int col, String orginVal) {
        this.row = row;
        this.col = col;
        this.orginVal = orginVal;
    }

    String draw(boolean drawDownBound) {
        // 上边界
        String up = horizontal(cellStyle);
        // 左边界
        String left = vertical();
        // 下边界
        String down = horizontal(cellStyle);
        // 右边界
        String right = vertical();

        StringJoiner sj = new StringJoiner("\n");
        StringBuilder mid = new StringBuilder();
        for (int i = 0; i < cellStyle.getHeight(); i++) {
            StringBuilder midLine = new StringBuilder(left);
            for (int j = 0; j < cellStyle.getPending(); j++) {
                midLine.append(" ");
            }

            // 填充单元格的值
            if (i < val.length) {
                typeVal(midLine, val[i]);
            }

            for (int j = 0; j < cellStyle.getPending(); j++) {
                midLine.append(" ");
            }
            midLine.append(right);
            sj.add(midLine);
        }

        mid.append(sj);
        if (drawDownBound) {
            return up + "\n" + mid + "\n" + down;
        } else {
            return up + "\n" + mid;
        }
    }

    private void typeVal(StringBuilder midLine, String content) {
        int startTypeIndex = beginTypeValInThisCol(content, cellStyle);
        // 左 中 右
        int rightBlankStart = startTypeIndex + content.getBytes().length;
        for (int k = 0; k < startTypeIndex; k++) {
            midLine.append(" ");
        }

        midLine.append(content);

        for (int k = rightBlankStart; k < cellStyle.getWidth(); k++) {
            midLine.append(" ");
        }
    }


    private int beginTypeValInThisCol(String val, CellStyle cellStyle) {
        // 是否需要在当前行进行填充
        int width = cellStyle.getWidth();
        int valWidth = val.getBytes().length;
        switch (cellStyle.getHAlignment()) {
            case H_CENTER:
                return (width - valWidth) / 2;
            case H_LEFT:
                return 0;
            case H_RIGHT:
                return (width - 1) - valWidth;
            default:
                throw new IllegalArgumentException("水平对齐方式设置不正确");
        }
    }

    private String horizontal(CellStyle cellStyle) {
        StringBuilder sb = new StringBuilder();
        sb.append("+");

        for (int i = 0; i < cellStyle.getWidth() + 2 * cellStyle.getPending(); i++) {
            sb.append("-");
        }

        sb.append("+");
        return sb.toString();
    }

    private String vertical() {
        return "|";
    }

    public static void main(String[] args) {
//        Cell cell = new Cell();
//        String s = "王建光";
//        cell.setCellStyle(new CellStyle(){{
//            this.setWidth(CharacterUtil.getDoubleByteCharAmt(s));
//            this.setHeight(1);
//        }});
//        System.out.println(CharacterUtil.getDoubleByteCharAmt(s));
//        cell.setVal(new String[]{s});
//        cell.draw();

//        System.out.println("hello");
    }
}
