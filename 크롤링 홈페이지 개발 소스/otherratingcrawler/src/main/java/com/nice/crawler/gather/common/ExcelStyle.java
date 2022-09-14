package com.nice.crawler.gather.common;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

public class ExcelStyle {

	// �ſ��򰡻�� ��Ÿ��
	public static HSSFCellStyle getCrprvidStyle(HSSFWorkbook workbook){
		
		HSSFCellStyle crprvid_style = workbook.createCellStyle();
		HSSFFont font = workbook.createFont();
		font.setFontHeightInPoints((short)15);
		font.setBold(true);
		crprvid_style.setFont(font);
		crprvid_style.setAlignment(HorizontalAlignment.CENTER);
		
		return crprvid_style;
	}
	
	// ä�������� ��Ÿ��
	public static HSSFCellStyle getSecuTypStyle(HSSFWorkbook workbook){
		
		HSSFCellStyle secu_style = workbook.createCellStyle();
			
		HSSFFont font = workbook.createFont();
		font.setColor(HSSFColor.BLACK.index);
		font.setBold(true);
		secu_style.setFont(font);
		
		secu_style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);  
		secu_style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		return secu_style;
	}
	
	// ������ ��Ÿ��
	public static HSSFCellStyle getDataHeaderStyle(HSSFWorkbook workbook){
		
		HSSFCellStyle dataheader_style = workbook.createCellStyle();
		
		HSSFFont font = workbook.createFont();
		font.setColor(HSSFColor.WHITE.index);
		font.setBold(true);
		dataheader_style.setFont(font);
		
		dataheader_style.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);  
		dataheader_style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		dataheader_style.setBorderTop(BorderStyle.MEDIUM);
		dataheader_style.setBorderBottom(BorderStyle.MEDIUM);
		dataheader_style.setBorderLeft(BorderStyle.MEDIUM);
		dataheader_style.setBorderRight(BorderStyle.MEDIUM);
		dataheader_style.setAlignment(HorizontalAlignment.CENTER);
		
		return dataheader_style;
	}

	// ������ ��Ÿ��
	public static HSSFCellStyle getDataStyle(HSSFWorkbook workbook){
		
		HSSFCellStyle data_style = workbook.createCellStyle();
		data_style.setBorderTop(BorderStyle.THIN);
		data_style.setBorderBottom(BorderStyle.THIN);
		data_style.setBorderLeft(BorderStyle.THIN);
		data_style.setBorderRight(BorderStyle.THIN);
		
		return data_style;
	}
}