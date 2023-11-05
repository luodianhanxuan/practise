package com.wangjg.algorithm;

import java.util.ArrayList;
import java.util.List;

/**
 * 递推方程和状态的描述如下：
 * 递归方程：
 * 对冲矩阵 `hedgingMatrix[i][j]` 的值可以根据前一个状态的值来计算。对于每个合同 `contractList[i-1]` 和标的 `labelList[j-1]`，有以下递归方程：
 * `hedgingMatrix[i][j] = max(hedgingMatrix[i-1][j] + contractList[i-1].amount, hedgingMatrix[i][j-1] - labelList[j-1].amount)`
 * 其中 `max(a, b)` 表示取两个值中的较大值。
 *
 * 状态：
 * 状态可以用 `hedgingMatrix[i][j]` 表示，表示在考虑前 `i` 个合同和前 `j` 个标的的情况下的最大对冲金额。对冲路径 `hedgingPath[i][j]` 记录了达到最大对冲金额的路径，用于还原对冲组合。
 * 通过填充对冲矩阵和对冲路径，可以得到最大对冲金额和对冲组合。最后，从对冲路径的右下角开始，根据对冲路径的值依次回溯，确定对冲组合。
 */
public class MaximumHedging {

    /**
     * 合约类，包含合约金额和标签。
     */
    public static class Contract {
        public int amount;
        public String label;

        public Contract(int amount, String label) {
            this.amount = amount;
            this.label = label;
        }
    }

    /**
     * 标的类，包含标的金额和标签名。
     */
    public static class Label {
        public int amount;
        public String label;

        public Label(int amount, String label) {
            this.amount = amount;
            this.label = label;
        }
    }

    /**
     * 最大对冲计算结果类，包含最大对冲金额和对冲组合。
     */
    public static class HedgingResult {
        public int maximumHedgingAmount;
        public List<Object> hedgingCombination;

        public HedgingResult(int maximumHedgingAmount, List<Object> hedgingCombination) {
            this.maximumHedgingAmount = maximumHedgingAmount;
            this.hedgingCombination = hedgingCombination;
        }
    }

    /**
     * 计算给定合约列表和标的列表的最大对冲金额和对冲组合。
     *
     * @param contractList  合约列表
     * @param labelList     标的列表
     * @return              最大对冲金额和对冲组合
     */
    public static HedgingResult calculateMaximumHedging(List<Contract> contractList, List<Label> labelList) {
        int n = contractList.size(); // 合约数量
        int m = labelList.size(); // 标的数量

        int[][] hedgingMatrix = new int[n + 1][m + 1]; // 对冲矩阵
        Object[][] hedgingPath = new Object[n + 1][m + 1]; // 对冲路径

        hedgingMatrix[0][0] = 0; // 初始化对冲矩阵的第一行和第一列

        // 初始化第一列，即在不使用任何标的的情况下对冲合约金额
        for (int i = 1; i <= n; i++) {
            hedgingMatrix[i][0] = hedgingMatrix[i - 1][0] + contractList.get(i - 1).amount;
            hedgingPath[i][0] = contractList.get(i - 1).label;
        }

        // 初始化第一行，即在不使用任何合约的情况下对冲标的金额
        for (int j = 1; j <= m; j++) {
            hedgingMatrix[0][j] = hedgingMatrix[0][j - 1] - labelList.get(j - 1).amount;
            hedgingPath[0][j] = "-" + labelList.get(j - 1).label;
        }

        // 填充对冲矩阵和对冲路径
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int hedgingAmount1 = hedgingMatrix[i - 1][j] + contractList.get(i - 1).amount; // 对冲合约金额
                int hedgingAmount2 = hedgingMatrix[i][j - 1] - labelList.get(j - 1).amount; // 对冲标的金额

                // 选择最大的对冲金额
                if (hedgingAmount1 >= hedgingAmount2) {
                    hedgingMatrix[i][j] = hedgingAmount1;
                    hedgingPath[i][j] = contractList.get(i - 1).label;
                } else {
                    hedgingMatrix[i][j] = hedgingAmount2;
                    hedgingPath[i][j] = "-" + labelList.get(j - 1).label;
                }
            }
        }

        int maximumHedgingAmount = hedgingMatrix[n][m];
        List<Object> hedgingCombination = new ArrayList<>();
        int i = n;
        int j = m;

        while (i > 0 || j > 0) {
            if (hedgingPath[i][j] instanceof String && ((String) hedgingPath[i][j]).startsWith("-")) {
                hedgingCombination.add(labelList.get(j - 1));
                j--;
            } else {
                hedgingCombination.add(contractList.get(i - 1));
                i--;
            }
        }

        return new HedgingResult(maximumHedgingAmount, hedgingCombination);
    }
}
