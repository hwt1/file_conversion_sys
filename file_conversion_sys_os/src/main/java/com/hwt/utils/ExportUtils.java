package com.hwt.utils;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author 黄雯婷
 * @version 1.0
 * @description: 导出工具类
 * @date 2020-08-18 11:39:31
 **/
public class ExportUtils {
    public static XSSFCellStyle getTitleStyle(XSSFWorkbook wookbook) {
        XSSFCellStyle colorStyle = wookbook.createCellStyle();
        colorStyle.setFillForegroundColor(HSSFColor.YELLOW.index);
        colorStyle.setFillBackgroundColor(HSSFColor.YELLOW.index);
        return colorStyle;
    }
}
