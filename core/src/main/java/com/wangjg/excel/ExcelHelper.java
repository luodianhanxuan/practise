package com.wangjg.excel;

import com.wangjg.excel.annotations.ExcelSheet;
import com.wangjg.excel.annotations.ExcelTitleCell;
import com.wangjg.excel.annotations.ExcelValCell;
import com.wangjg.excel.enums.ExcelCustomCellEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;

@Slf4j
public class ExcelHelper<ValueObjectType> {

    private final Workbook workbook;

    protected ExcelHelper(Workbook workbook) {
        this.workbook = workbook;
    }

    public void saveExcel(String filePath) throws IOException {
        // Write the output to a file
        try (OutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        }
    }


    public Workbook dealSheet(Collection<ValueObjectType> objs) throws IllegalAccessException {
        // 提取 sheet 定义
        SheetDefinition sheetDefinition = extractDefinition(objs);
        Sheet sheet = fillSheetWith(sheetDefinition);
        return workbook;
    }

    /**
     * 填充数据到 workbook 中
     *
     * @param sheetDefinition sheet 定义对象
     */
    private Sheet fillSheetWith(SheetDefinition sheetDefinition) {
        Sheet sheet;
        if (sheetDefinition.sheetName != null && !sheetDefinition.sheetName.equals("")) {
            sheet = workbook.createSheet(sheetDefinition.sheetName);
        } else {
            sheet = workbook.createSheet();
        }

        Map<Integer, Row> rowMap = new HashMap<>();

        // title
        Collection<CellDefinition> titleDefinitions = sheetDefinition.getTitleDefinitions();
        for (CellDefinition titleDefinition : titleDefinitions) {
            int xAxis = titleDefinition.getXAxis();
            int yAxis = titleDefinition.getYAxis();

            Row row = rowMap.computeIfAbsent(yAxis, sheet::createRow);
            Cell cell = row.createCell(xAxis);
            fillCellWithValue(cell, titleDefinition.fieldType, titleDefinition.val);

            CellStyle cellStyle = workbook.createCellStyle();

            CustomCellContext context = new CustomCellContext();
            context.setCellStyle(cellStyle);
            context.setSheet(sheet);
            context.setWorkbook(workbook);
            context.setCell(cell);
            context.setCellDefinition(titleDefinition);

            if (titleDefinition.customCell != null && !titleDefinition.customCell.isEmpty()) {
                for (Consumer<CustomCellContext> consumer : titleDefinition.customCell) {
                    consumer.accept(context);
                }
            }
            cell.setCellStyle(cellStyle);
        }


        // value
        Collection<CellDefinition> valueDefinitions = sheetDefinition.getValueDefinitions();
        for (CellDefinition valueDefinition : valueDefinitions) {
            int xAxis = valueDefinition.getXAxis();
            int yAxis = valueDefinition.getYAxis();
            Row row = rowMap.computeIfAbsent(yAxis, sheet::createRow);
            Cell cell = row.createCell(xAxis);
            fillCellWithValue(cell, valueDefinition.fieldType, valueDefinition.val);

            // cell Style
            CellStyle cellStyle = workbook.createCellStyle();

            CustomCellContext context = new CustomCellContext();
            context.setWorkbook(workbook);
            context.setSheet(sheet);
            context.setCellDefinition(valueDefinition);
            context.setCellStyle(cellStyle);
            context.setCell(cell);

            if (valueDefinition.customCell != null && !valueDefinition.customCell.isEmpty()) {
                for (Consumer<CustomCellContext> consumer : valueDefinition.customCell) {
                    consumer.accept(context);
                }
            }
            cell.setCellStyle(cellStyle);
        }
        return sheet;
    }

    private final Map<Class<?>, Class<?>> fieldClassSetCellValClassParamRelation = new HashMap<Class<?>, Class<?>>() {
        {
            this.put(double.class, double.class);
            this.put(Double.class, double.class);
            this.put(int.class, double.class);
            this.put(Integer.class, double.class);
            this.put(short.class, double.class);
            this.put(Short.class, double.class);
            this.put(long.class, double.class);
            this.put(Long.class, double.class);

            this.put(String.class, String.class);
            this.put(boolean.class, boolean.class);
            this.put(Boolean.class, boolean.class);
            this.put(Date.class, Date.class);
        }
    };

    private void fillCellWithValue(Cell cell, Class<?> fieldType, Object val) {
        if (!fieldClassSetCellValClassParamRelation.containsKey(fieldType)) {
            log.warn("不存在字段类型为 {} 的 setCellValue 参数类型的映射关系，无法处理", fieldType);
            return;
        }
        try {
            Class<? extends Cell> cellClazz = cell.getClass();
            Method setCellValueMethod = cellClazz.getDeclaredMethod("setCellValueImpl", fieldClassSetCellValClassParamRelation.get(fieldType));
            setCellValueMethod.setAccessible(true);
            setCellValueMethod.invoke(cell, val);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param objs 通过要导出的数据对象的注解提取 sheet 定义对象
     */
    private SheetDefinition extractDefinition(Collection<ValueObjectType> objs) throws IllegalAccessException {
        if (objs == null || objs.size() <= 0) {
            return null;
        }

        List<CellDefinition> titleDefinitionList = new ArrayList<>();

        SheetDefinition sheetDefinition = new SheetDefinition();
        // 提取 sheet 信息
        for (ValueObjectType obj : objs) {
            Class<?> clazz = obj.getClass();
            ExcelSheet sheetAnnotation = clazz.getDeclaredAnnotation(ExcelSheet.class);
            if (Objects.isNull(sheetAnnotation)) {
                return null;
            }

            // title
            Field[] fields = clazz.getDeclaredFields();
            String expandAlong = sheetAnnotation.expandAlong();
            CellDefinition titleDefinition = null;
            for (Field field : fields) {
                ExcelTitleCell excelTitleAnnotation = field.getDeclaredAnnotation(ExcelTitleCell.class);
                if (Objects.isNull(excelTitleAnnotation)) {
                    continue;
                }

                List<Consumer<CustomCellContext>> customCellBehavior = new ArrayList<>();
                ExcelCustomCellEnum[] excelStyleEnums = excelTitleAnnotation.customCell();

                for (ExcelCustomCellEnum styleEnum : excelStyleEnums) {
                    Consumer<CustomCellContext> customCell = styleEnum.getCustomCell();
                    customCellBehavior.add(customCell);
                }

                int x1Axis = excelTitleAnnotation.x1Axis();
                int y1Axis = excelTitleAnnotation.y1Axis();
                if ("X".equalsIgnoreCase(expandAlong) || "Y".equalsIgnoreCase(expandAlong)) {
                    titleDefinition = getCellDefinition(field, obj, x1Axis, y1Axis, customCellBehavior);
                    titleDefinition.setVal(excelTitleAnnotation.name());
                    titleDefinition.setFieldType(String.class);
                } else if ("Func".equalsIgnoreCase(expandAlong)) {
                    titleDefinition = getTitleDefinition(obj, field, x1Axis, y1Axis);
                } else {
                    throw new UnsupportedOperationException("不支持此扩展标识");
                }
                titleDefinitionList.add(titleDefinition);
            }
            String sheetName = sheetAnnotation.name();
            sheetDefinition.setSheetName(sheetName);
            sheetDefinition.setExpandAlong(expandAlong);
            break;
        }

        // 提取被 @ExcelCell 注解标注的，所有声明的属性，并组装 cellDefinitions 属性 以及表头属性 titleDefinitions
        List<CellDefinition> cellDefinitionList = new ArrayList<>();
        Map<String, String> lastPositionHelper = new HashMap<>();

        for (ValueObjectType obj : objs) {
            Class<?> clazz = obj.getClass();
            Field[] declaredFields = clazz.getDeclaredFields();

            for (Field field : declaredFields) {
                ExcelValCell excelCellAnnotation = field.getDeclaredAnnotation(ExcelValCell.class);
                if (Objects.isNull(excelCellAnnotation)) {
                    continue;
                }
                int x1Axis = excelCellAnnotation.x1Axis();
                int y1Axis = excelCellAnnotation.y1Axis();
                if (x1Axis < 0 || y1Axis < 0) {
                    continue;
                }

                String expandAlong = sheetDefinition.getExpandAlong();
                String lastPositionHelperKey = sheetDefinition.getSheetName() + "_" + x1Axis + "_" + y1Axis;
                int xAxis;
                int yAxis;
                if (!lastPositionHelper.containsKey(lastPositionHelperKey)) {
                    lastPositionHelper.put(lastPositionHelperKey, x1Axis + "_" + y1Axis);
                    xAxis = x1Axis;
                    yAxis = y1Axis;
                } else {
                    String lastPostion = lastPositionHelper.get(lastPositionHelperKey);
                    String[] axisVals = lastPostion.split("_");
                    xAxis = Integer.parseInt(axisVals[0]);
                    yAxis = Integer.parseInt(axisVals[1]);

                    if ("X".equalsIgnoreCase(expandAlong)) {
                        xAxis = xAxis + 1;
                    } else if ("Y".equalsIgnoreCase(expandAlong)) {
                        yAxis = yAxis + 1;
                    } else if ("Func".equalsIgnoreCase(expandAlong)) {
                        // 自定义 XY轴 坐标逻辑
                        xAxis = getXAxisIndex(obj, field, x1Axis);
                        yAxis = getYAxisIndex(obj, field, y1Axis);
                    } else {
                        throw new UnsupportedOperationException("不支持此扩展标识");
                    }
                }
                ExcelCustomCellEnum[] excelStyleEnums = excelCellAnnotation.customCell();
                List<Consumer<CustomCellContext>> customCellBehaviorList = new ArrayList<>();
                for (ExcelCustomCellEnum styleEnum : excelStyleEnums) {
                    Consumer<CustomCellContext> customCell = styleEnum.getCustomCell();
                    customCellBehaviorList.add(customCell);
                }

                CellDefinition cellDefinition = getCellDefinition(field, obj, xAxis, yAxis, customCellBehaviorList);
                cellDefinitionList.add(cellDefinition);
            }
        }

        sheetDefinition.setTitleDefinitions(titleDefinitionList);
        sheetDefinition.setValueDefinitions(cellDefinitionList);

        return sheetDefinition;
    }

    /**
     * 当 ExcelCell 注解为"Func"，动态获取该对象的该字段的表头定义
     *
     * @param obj    当前对象
     * @param field  当前字段
     * @param x1Axis 数据起初 X 轴坐标
     * @param y1Axis 数据起初 Y 轴坐标
     * @return 该对象 title 定义
     */
    private CellDefinition getTitleDefinition(ValueObjectType obj, Field field, int x1Axis, int y1Axis) {
        throw new UnsupportedOperationException("不支持此操作");
    }

    /**
     * 当 ExcelCell 注解为"Func"，动态获取该对象的该字段的表头名称
     *
     * @param obj   当前对象
     * @param field 当前字段
     * @return 该对象的该字段的表头名称
     */
    protected String getTitleName(ValueObjectType obj, Field field) {
        throw new UnsupportedOperationException("不支持此操作");
    }

    /**
     * 当 ExcelCell 注解为"Func"，动态获取该对象的该字段的表头名称
     *
     * @param obj   当前对象
     * @param field 当前字段
     * @return 该对象的该字段的表头名称
     */
    protected String getTitleIndex(ValueObjectType obj, Field field) {
        throw new UnsupportedOperationException("不支持此操作");
    }

    /**
     * 当 ExcelCell 注解为"Func"（自定义 X 轴索引）时，获取该对象的该字段的 X 轴索引
     *
     * @param obj    当前对象
     * @param field  当前字段
     * @param x1Axis 起初 X 轴坐标
     * @return X 轴索引
     */
    protected int getXAxisIndex(ValueObjectType obj, Field field, int x1Axis) {
        throw new UnsupportedOperationException("不支持此操作");
    }

    /**
     * 当 ExcelCell 注解为"Func"（自定义 Y 轴索引）时，获取该对象的该字段的 Y 轴索引
     *
     * @param obj    当前对象
     * @param field  当前字段
     * @param y1Axis 起初 Y 轴坐标
     * @return Y 轴索引
     */
    protected int getYAxisIndex(ValueObjectType obj, Field field, int y1Axis) {
        throw new UnsupportedOperationException("不支持此操作");
    }

    protected CellDefinition getCellDefinition(Field field,
                                               Object obj,
                                               int xAxis,
                                               int yAxis,
                                               List<Consumer<CustomCellContext>> customCell
    ) throws IllegalAccessException {
        field.setAccessible(true);
        Object val = field.get(obj);

        CellDefinition cellDefinition = new CellDefinition();
        cellDefinition.setFieldType(field.getType());
        cellDefinition.setVal(val);
        cellDefinition.setXAxis(xAxis);
        cellDefinition.setYAxis(yAxis);
        cellDefinition.setCustomCell(customCell);
        return cellDefinition;
    }


    @Data
    protected static class SheetDefinition {
        /**
         * sheet 名称
         */
        private String sheetName;

        /**
         * Excel Cells 定义
         */
        private Collection<CellDefinition> valueDefinitions;

        /**
         * Excel 表头定义
         */
        private Collection<CellDefinition> titleDefinitions;

        /**
         * 沿着哪个轴(X、Y、XY)扩展
         */
        private String expandAlong;
    }

    @Data
    public static class CellDefinition {
        /**
         * x 轴坐标，自左而右，从 0 开始
         */
        private int xAxis;

        /**
         * y 轴坐标，自上而下，从 0 开始
         */
        private int yAxis;

        /**
         * 单元格类型
         */
        private Class<?> fieldType;

        /**
         * 单元格值
         */
        private Object val;

        /**
         * 单元格样式回调
         */
        private List<Consumer<CustomCellContext>> customCell;
    }
}
