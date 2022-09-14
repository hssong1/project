package com.nice.crawler.gather.common;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateUtil {

    /**
     * 일자에 대해서 YYYYmmdd의 형식을 YYYY.mm.dd 형식으로 변경한다.
     * @param date
     * @param delimeter
     * @return
     */
    public static String getChangeDateString(String date, String delimeter){
        String dateString = date;
        if(date != null && date.length() == 8 && delimeter != null && delimeter != ""){
            dateString = date = date.substring(0,4) + delimeter + date.substring(4,6) + delimeter + date.substring(6,8);
        }
        return dateString;
    }

    /**
     * 날짜형식이 맞는지 확인한다. (강제로 돌리기 위한 조건)
     * @param date
     * @return
     */
    public static boolean isDate(String date) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
            simpleDateFormat.setLenient(false);
            simpleDateFormat.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * 이번주에 해당요일에 해당되는 일자를 가져온다.
     * @param week Calendar.MONDAY, Calendar.FRIEND
     * @return
     */
    public static String getDateOfThisWeek(int week) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, week);
        return formatter.format(c.getTime());
    }
    
    /**
     * @Description : 오늘에 해당하는 일자를 가져온다.
     * @return
     */
    public static String getToday() {
		DecimalFormat df = new DecimalFormat("00");
		Calendar currentCalendar = Calendar.getInstance();
		
		//현재 날짜 구하기
		String strYear = Integer.toString(currentCalendar.get(Calendar.YEAR));
		String strMonth = df.format(currentCalendar.get(Calendar.MONTH) + 1);
		String strDay = df.format(currentCalendar.get(Calendar.DATE));
		String strDate = strYear + strMonth + strDay;
		
		return strDate;
	}
	
    /**
     * @Description : 지난주에 해당하는 일자를 가져온다.
     * @return
     */
	public static String getLastOneWeek() {
		DecimalFormat df = new DecimalFormat("00");
		Calendar currentCalendar = Calendar.getInstance();
		
		//일주일 전 날짜 구하기
		currentCalendar.add(currentCalendar.DATE, -7);
		String strYear7 = Integer.toString(currentCalendar.get(Calendar.YEAR));
		String strMonth7 = df.format(currentCalendar.get(Calendar.MONTH) + 1);
		String strDay7 = df.format(currentCalendar.get(Calendar.DATE));
		String strDate7 = strYear7 + strMonth7 + strDay7;
		
		return strDate7;
	}
	
	/**
     * @Description : 이주전에 해당하는 일자를 가져온다.
     * @return
     */
	public static String getLastTwoWeek() {
		DecimalFormat df = new DecimalFormat("00");
		Calendar currentCalendar = Calendar.getInstance();
		
		//일주일 전 날짜 구하기
		currentCalendar.add(currentCalendar.DATE, -14);
		String strYear14 = Integer.toString(currentCalendar.get(Calendar.YEAR));
		String strMonth14 = df.format(currentCalendar.get(Calendar.MONTH) + 1);
		String strDay14 = df.format(currentCalendar.get(Calendar.DATE));
		String strDate14 = strYear14 + strMonth14 + strDay14;
		
		return strDate14;
	}
	
	/**
     * @Description : 지난달에 해당하는 일자를 가져온다.
     * @return
     */
	public static String getLastMonth() {
		DecimalFormat df = new DecimalFormat("00");
		Calendar currentCalendar = Calendar.getInstance();
		
		//한달 전 날짜 구하기
		currentCalendar.add(currentCalendar.MONTH, -1);
		String strYear31 = Integer.toString(currentCalendar.get(Calendar.YEAR));
		String strMonth31 = df.format(currentCalendar.get(Calendar.MONTH) + 1);
		String strDay31 = df.format(currentCalendar.get(Calendar.DATE));
		String strDate31 = strYear31 + strMonth31 + strDay31;
		
		return strDate31;
	}
}