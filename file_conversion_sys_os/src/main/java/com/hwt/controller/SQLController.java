package com.hwt.controller;

import com.hwt.constant.SQLConstant;
import com.hwt.utils.ExportUtils;
import com.hwt.utils.ImportExcelUtils;
import org.apache.commons.collections4.SplitMapUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * @author 黄雯婷
 * @version 1.0
 * @description: 处理生成sql文件的controller
 * @date 2020-08-18 09:48:40
 **/
@Controller
@RequestMapping("/api/sql")
public class SQLController {

    private static final Logger log= LoggerFactory.getLogger(SQLController.class);
    /**
     * 导出sql字段excel表格模板
     * @param response
     */
    @RequestMapping(value="/exportTemplate.action", method= RequestMethod.POST)
    public void exportTemplate(HttpServletResponse response){
        log.info("开始进行sql字段模板导出...");
        final String fileName="sql字段导入模板";
        OutputStream outputStream=null;
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-Disposition","attachment;filename="+new String((fileName+".xlsx").getBytes("utf-8"),"iso8859-1"));
            XSSFWorkbook workbook=new XSSFWorkbook();
            XSSFSheet xssfSheet=workbook.createSheet(fileName);
            xssfSheet.setDefaultColumnWidth(18);
            XSSFRow row=xssfSheet.createRow(0);
            row.setHeight((short) 500);
            for(int i=0;i< SQLConstant.SQL_EXCEL_HEADER.length;i++){
                XSSFCell cell=row.createCell(i);
                cell.setCellValue(SQLConstant.SQL_EXCEL_HEADER[i]);
                cell.setCellStyle(ExportUtils.getTitleStyle(workbook));
            }
            outputStream=response.getOutputStream();
            workbook.write(outputStream);
            outputStream.flush();

        } catch (Exception e) {
            log.error("sql字段模板导出异常",e);
            e.printStackTrace();
        }finally{
            try {
                if(null!=outputStream){
                    outputStream.close();
                }
            }catch(Exception e){
                log.error("文件流关闭异常");
            }
        }
    }
    @RequestMapping(value="/generateSqlTxt.action",method = RequestMethod.POST)
    @ResponseBody
    public String generateSqlTxt(HttpServletRequest request,HttpServletResponse response){
        log.info("sql字段文件上传处理...");
        MultipartHttpServletRequest multipartHttpServletRequest= (MultipartHttpServletRequest) request;
        Collection<MultipartFile> files=multipartHttpServletRequest.getFileMap().values();
        MultipartFile file=files.iterator().next();
        Workbook workbook= ImportExcelUtils.getExcelWorkbook(file);
        if(workbook.getNumberOfSheets()<1){
            throw new RuntimeException("Excel文件工作簿少于1页，请下载模板后填写数据再导入");
        }
        Sheet sheet=workbook.getSheetAt(0);
        if(sheet.getLastRowNum()<1){
            throw new RuntimeException("没有需要导入的内容，请填写有效数据再导入");
        }

        //获取表名、表描述、sql类型
        String tableName=multipartHttpServletRequest.getParameter("name");
        String textType=multipartHttpServletRequest.getParameter("type");
        String tableDesc=multipartHttpServletRequest.getParameter("desc");
        StringBuilder result=new StringBuilder();
        if(textType.equals(String.valueOf(SQLConstant.CREATE_FLAG))){
            //建表
            result.append("CREATE TABLE "+tableName+" (\n");
            return createTable(sheet, result,tableDesc);
        }else{
            result.append("ALTER TABLE "+tableName+"\n");
            return modifyTable(sheet, result);
        }

    }

    private String createTable(Sheet sheet, StringBuilder result,String tableDesc) {
        boolean flag;
        List<String> indexFields=new ArrayList<>();
        for(int i=1;i<=sheet.getLastRowNum();i++){
            Row row=sheet.getRow(i);
            if(row==null)
                continue;
            flag=false;
            //移除没有数据，但有一定格式的行
            for(Cell c:row){
                if(!"".equals(ImportExcelUtils.getCellValue(c))){
                    flag=true;
                    break;
                }
            }
            if(!flag){
                sheet.removeRow(row);
                continue;

            }
            String FieldName=ImportExcelUtils.getCellValue(row.getCell(0));
            String FieldDesc=ImportExcelUtils.getCellValue(row.getCell(1));
            String FieldType=ImportExcelUtils.getCellValue(row.getCell(2));
            String isPrimary=ImportExcelUtils.getCellValue(row.getCell(3));
            String isNUll=ImportExcelUtils.getCellValue(row.getCell(4));
            String defaultValue=ImportExcelUtils.getCellValue(row.getCell(5));
            String remark=ImportExcelUtils.getCellValue(row.getCell(6));
            String isIndex=ImportExcelUtils.getCellValue(row.getCell(7));

            //字段名
            result.append(FieldName+" ");
            //字段类型
            result.append(FieldType.toUpperCase()+" ");
            if("NO".equals(isNUll.toUpperCase())){
                //不允许null
                result.append("NOT NULL ");
            }
            if("YES".equals(isPrimary.toUpperCase())){
                //是主键
                result.append("PRIMARY KEY AUTO_INCREMENT ");
            }
            //默认值
            if(!("".equals(defaultValue) || "NULL".equals(defaultValue.toUpperCase())))
            {
                result.append("DEFAULT "+defaultValue+" ");
            }
            if("".equals(remark)){
                //备注
                result.append("COMMENT '"+FieldDesc+"',\n");
            }else{
                result.append("COMMENT '"+FieldDesc+":"+remark+"',\n");
            }
            if("YES".equals(isIndex.toUpperCase())){
                //建索引
                indexFields.add(FieldName);
            }
        }
        if(!indexFields.isEmpty()){
            for(String str:indexFields){
                result.append("KEY idx_"+str+" ("+str+"),\n");
            }
        }
        result=result.deleteCharAt(result.length()-2);
        result.append(")ENGINE=INNODB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='"+tableDesc+"';");
        return result.toString();
    }
    private String modifyTable(Sheet sheet, StringBuilder result) {
        boolean flag;
        List<String> indexFields=new ArrayList<>();
        for(int i=1;i<=sheet.getLastRowNum();i++){
            Row row=sheet.getRow(i);
            if(row==null)
                continue;
            flag=false;
            //移除没有数据，但有一定格式的行
            for(Cell c:row){
                if(!"".equals(ImportExcelUtils.getCellValue(c))){
                    flag=true;
                    break;
                }
            }
            if(!flag){
                sheet.removeRow(row);
                continue;

            }
            String FieldName=ImportExcelUtils.getCellValue(row.getCell(0));
            String FieldDesc=ImportExcelUtils.getCellValue(row.getCell(1));
            String FieldType=ImportExcelUtils.getCellValue(row.getCell(2));
            String isPrimary=ImportExcelUtils.getCellValue(row.getCell(3));
            String isNUll=ImportExcelUtils.getCellValue(row.getCell(4));
            String defaultValue=ImportExcelUtils.getCellValue(row.getCell(5));
            String remark=ImportExcelUtils.getCellValue(row.getCell(6));
            String isIndex=ImportExcelUtils.getCellValue(row.getCell(7));

            //字段名
            result.append("ADD "+FieldName+" ");
            //字段类型
            result.append(FieldType.toUpperCase()+" ");
            if("NO".equals(isNUll.toUpperCase())){
                //不允许null
                result.append("NOT NULL ");
            }
            //默认值
            if(!("".equals(defaultValue) || "NULL".equals(defaultValue.toUpperCase())))
            {
                result.append("DEFAULT "+defaultValue+" ");
            }
            if("".equals(remark)){
                //备注
                result.append("COMMENT '"+FieldDesc+"',\n");
            }else{
                result.append("COMMENT '"+FieldDesc+":"+remark+"',\n");
            }
            if("YES".equals(isIndex.toUpperCase())){
                //建索引
                indexFields.add(FieldName);
            }
        }
        if(!indexFields.isEmpty()){
            for(String str:indexFields){
                result.append("ADD KEY idx_"+str+" ("+str+"),\n");
            }
        }
        result=result.deleteCharAt(result.length()-2);
        result.append(";");
        return result.toString();
    }
}
