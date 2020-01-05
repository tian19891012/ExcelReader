package com.kevin.tools;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author kevin
 * @ClassName
 * @Date 2020/1/59:36
 */
public class ImportExcel {

    private final static String excel2003L = ".xls";    //2003- 版本的excel
    private final static String excel2007U = ".xlsx";   //2007+ 版本的excel

    public static boolean isXls(String fileName) {
        // (?i)忽略大小写
        if (fileName.matches("^.+\\.(?i)(xls)$")) {
            return true;
        } else if (fileName.matches("^.+\\.(?i)(xlsx)$")) {
            return true;
        } else {
            throw new RuntimeException("格式不对");
        }
    }

    /**
     * 描述：根据文件后缀，自适应上传文件的版本
     */
    public static Workbook getWorkbook(InputStream inStr, String fileName) throws Exception {
        Workbook wb = null;
        String fileType = fileName.substring(fileName.lastIndexOf("."));
        if (excel2003L.equals(fileType)) {
            wb = new HSSFWorkbook(inStr);  //2003-
        } else if (excel2007U.equals(fileType)) {
            wb = new XSSFWorkbook(inStr);  //2007+
        } else {
            throw new RuntimeException("请检查excel文本的格式");
        }
        return wb;
    }


    public static List<Map<String, Object>> readExcel(String fileName, InputStream inputStream) throws Exception {
        boolean ret = isXls(fileName);
        Workbook workbook = null;
        // 根据后缀创建不同的对象
        if (ret) {
            workbook = new HSSFWorkbook(inputStream);
        } else {
            workbook = new XSSFWorkbook(inputStream);
        }
        Sheet sheet = workbook.getSheetAt(0);
        // 得到标题行
        Row titleRow = sheet.getRow(0);
        int lastRowNum = sheet.getLastRowNum();
        int lastCellNum = titleRow.getLastCellNum();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (int i = 1; i <= lastRowNum; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            Row row = sheet.getRow(i);
            for (int j = 0; j < lastCellNum; j++) {
                // 得到列名
                String key = titleRow.getCell(j).getStringCellValue();
                Cell cell = row.getCell(j);
                map.put(key, cell.getStringCellValue());
            }
            list.add(map);
        }
        return list;
    }

    public static List<List<Object>> getBankListByExcel(InputStream is, String fileName) throws Exception {
        List<List<Object>> list = null;
        Workbook work = getWorkbook(is, fileName);
        Sheet sheet = null;
        Row row = null;
        Cell cell = null;
        list = new ArrayList<List<Object>>();
        //遍历Excel中所有的sheet
        for (int i = 0; i < work.getNumberOfSheets(); i++) {
            sheet = work.getSheetAt(i);
            if (sheet == null) {
                continue;
            }
            //遍历当前sheet中的所有行
            //包涵头部，所以要小于等于最后一列数,这里也可以在初始值加上头部行数，以便跳过头部
            for (int j = sheet.getFirstRowNum(); j <= sheet.getLastRowNum(); j++) {
                //读取一行
                row = sheet.getRow(j);
                //去掉空行和表头
                if (row == null || row.getFirstCellNum() == j) {
                    continue;
                }
                //遍历所有的列
                List<Object> li = new ArrayList<Object>();
                for (int y = row.getFirstCellNum(); y < row.getLastCellNum(); y++) {
                    cell = row.getCell(y);
                    li.add(getCellValue(cell));
                }
                list.add(li);
            }
        }
        return list;
    }


    /**
     * 描述：对表格中数值进行格式化
     */
    public static Object getCellValue(Cell cell) {
        Object value = null;
        DecimalFormat df = new DecimalFormat("0");  //格式化字符类型的数字
        SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd");  //日期格式化
        DecimalFormat df2 = new DecimalFormat("0.00");  //格式化数字
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                value = cell.getRichStringCellValue().getString();
                break;
            case Cell.CELL_TYPE_NUMERIC:
                if ("General".equals(cell.getCellStyle().getDataFormatString())) {
                    value = df.format(cell.getNumericCellValue());
                } else if ("m/d/yy".equals(cell.getCellStyle().getDataFormatString())) {
                    value = sdf.format(cell.getDateCellValue());
                } else {
                    value = df2.format(cell.getNumericCellValue());
                }
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                value = cell.getBooleanCellValue();
                break;
            case Cell.CELL_TYPE_BLANK:
                value = "";
                break;
            default:
                break;
        }
        return value;
    }
}

