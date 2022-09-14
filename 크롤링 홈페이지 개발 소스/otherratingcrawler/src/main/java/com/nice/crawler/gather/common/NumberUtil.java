package com.nice.crawler.gather.common;

public class NumberUtil {

    public static boolean isNumber(String text) {
        try {
            Double.parseDouble(text.replace(",", ""));
            return true;
        } catch(Exception e) {
            return false;
        }
    }
}
