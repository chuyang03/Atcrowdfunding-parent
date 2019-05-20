package com.atguigu.atcrowdfunding.util;

public class StringUtil {

    public static boolean isEmpty(String s){

        //判断字符串是否为空，如果为空返回true
        return s == null || "".equals(s);
    }

    public static boolean isNotEmpty(String s){

        return !isEmpty(s);
    }
}
