package com.nice.crawler.gather.common;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Commons {
	
	public static String checkDateUtil(int num){
		String afterNum="";
		if(num<10){
			afterNum = "0"+Integer.toString(num);
		}else{
			afterNum = Integer.toString(num);
		}
		return afterNum;
	}
	
	public static String getBeforeMonth() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -1);
		String beforeMonth = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
		return beforeMonth;
	}
	
	public static String getNowDate(){
		Calendar calendar = Calendar.getInstance();
		String date = checkDateUtil(calendar.get(calendar.YEAR)) + "-" + checkDateUtil(calendar.get(calendar.MONTH)+1) + "-" + checkDateUtil(calendar.get(calendar.DAY_OF_MONTH));
		return date;
	}
	
	public static String getNowDateExceptSign(){
		Calendar calendar = Calendar.getInstance();
		String formattedDate = new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());
		return formattedDate;
	}
	
	public static String getBeforeSixDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -5);
		String formattedDate = new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());
		return formattedDate;
	}
	
	public static String getBeforeDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1);
		String formattedDate = new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());
		return formattedDate;
	}
	
	public static String getNowDateTime(){
		Calendar calendar = Calendar.getInstance();
		String date = checkDateUtil(calendar.get(calendar.YEAR)) + checkDateUtil(calendar.get(calendar.MONTH)+1) + checkDateUtil(calendar.get(calendar.DAY_OF_MONTH)) + checkDateUtil(calendar.get(calendar.HOUR_OF_DAY)) + checkDateUtil(calendar.get(calendar.MINUTE)) + checkDateUtil(calendar.get(calendar.SECOND));
		return date;
	}
	
	public static String getConvertDateToString(Date date){
		SimpleDateFormat transFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String dateString = transFormat.format(date);
		return dateString;
	}
	
	public static String getNowDay(){
		Calendar calendar = Calendar.getInstance();
		String date = checkDateUtil(calendar.get(calendar.DAY_OF_MONTH));
		return date;
	}
	
	public static String getChangeDateString(String date, String type){
		String dateString = date;
		if(date.length()==8 && type!=null && type!=""){
			dateString = date = date.substring(0,4) + type + date.substring(4,6) + type + date.substring(6,8);
		}
		return dateString;
	}
	
	// ������ ���ϱ�
	public static String getCurMonday(){
 		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMMdd");
 		Calendar c = Calendar.getInstance();
 		c.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
 		return formatter.format(c.getTime());
 	}

 	// �ݿ��� ���ϱ�
 	public static String getCurFriday(){
 		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMMdd");
 		Calendar c = Calendar.getInstance();
 		c.set(Calendar.DAY_OF_WEEK,Calendar.FRIDAY);
 		return formatter.format(c.getTime());
 	}
 	
 // ������ ���ϱ�
 	public static String getPastMonday(){
  		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMMdd");
  		Calendar c = Calendar.getInstance();
  		c.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
  	    c.add(Calendar.DATE, -7);
  		return formatter.format(c.getTime());
  	}

  	// �ݿ��� ���ϱ�
  	public static String getPastFriday(){
  		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMMdd");
  		Calendar c = Calendar.getInstance();
  		c.set(Calendar.DAY_OF_WEEK,Calendar.FRIDAY);
  	    c.add(Calendar.DATE, -7);
  		return formatter.format(c.getTime());
  	}
	
	// NULL �Ǵ� "" �� ��� N/A ���
	public static String getNullChange(String text){
		
		String result = "";
		if(text==null || text==""){
			result = "N/A";
		}else{
			result = text;
		}
		return result;
	}
	
	public static boolean getNumberChk(String text){
		boolean chk = true;
		try{
			Double.parseDouble(text.replace(",", ""));
			chk = true;
		}catch(Exception e){
			chk = false;
		}
		return chk;
	}

	/**
     * 엑셀 파일에 들어가는 제목을 설정
     * @return
     */
    public static String excelTitle(String startDate, String endDate) {
        StringBuffer sb = new StringBuffer();
        sb.append("기간 : ");
        sb.append(DateUtil.getChangeDateString(startDate, "."));
        sb.append(" ~ ");
        sb.append(DateUtil.getChangeDateString(endDate, "."));
        return sb.toString();
    }

    /**
     * 엑셀 파일명을 설정
     * @return
     */
    public static String fileName(String startDate, String endDate) {
       StringBuffer sb = new StringBuffer();
       sb.append(startDate);
       sb.append("-");
       sb.append(endDate);
       sb.append(".xls");
       return sb.toString();
    }
    
    /**
     * 글자수 예외처리 메소드
     * @return String
     */
    public static String checkNullOfString(String value) {
    	String result = "";
    	try {
			result = value.substring(0, 8);
			return result;
		} catch (IndexOutOfBoundsException e) {
			result = "";
			return result;
		}
    }
}