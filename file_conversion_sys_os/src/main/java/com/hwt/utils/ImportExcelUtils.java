package com.hwt.utils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author 黄雯婷
 * @version 1.0
 * @description: 导入工具类
 * @date 2020-08-18 14:52:18
 **/
public class ImportExcelUtils {

    //根据文件获取workbook
    public static Workbook getExcelWorkbook(MultipartFile file){
        Workbook workbook=null;
        //获取excel文件后缀，从而判断excel版本
        String excelType="xls";
        String fileName=file.getOriginalFilename();
        if(fileName.contains(".")){
            excelType=fileName.substring(fileName.lastIndexOf('.')+1);
        }
        try {
            if("xls".equals(excelType)) {
                //excel 2003
                workbook = new HSSFWorkbook(file.getInputStream());
            }else{
                //excel 2007
                workbook=new XSSFWorkbook(file.getInputStream());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return workbook;
    }

    //获取excel中单元格的值
    public static String getCellValue(Cell cell){
        String value="";
        if(cell==null){
            return value;
        }
        if(cell.getCellTypeEnum().compareTo(CellType.NUMERIC)==0){
            value=String.valueOf((int) cell.getNumericCellValue());
        }else{
            if(cell.getStringCellValue()!=null){
                value=cell.getStringCellValue().trim();
            }
        }

        return value;
    }
}
