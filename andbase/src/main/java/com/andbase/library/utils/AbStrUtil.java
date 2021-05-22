
package com.andbase.library.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2016/6/14 17:54
 * Email 396196516@qq.com
 * Info 字符串工具类
 */

public class AbStrUtil {
    
    /**
     * 判断一个字符串是否为null或空值.
     *
     * @param str 指定的字符串
     * @return true or false
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

	/**
	 * 将null转化为“”.
	 *
	 * @param str 指定的字符串
	 * @return 字符串的String类型
	 */
	public static String parseEmpty(String str) {
		if(str==null || "null".equals(str.trim())){
			str = "";
		}
		return str.trim();
	}
    
    /**
     * 获取字符串中文字符的长度（每个中文算2个字符）.
     *
     * @param str 指定的字符串
     * @return 中文字符的长度
     */
    public static int chineseLength(String str) {
        int valueLength = 0;
        String chinese = "[\u0391-\uFFE5]";
        /* 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1 */
        if(!isEmpty(str)){
	        for (int i = 0; i < str.length(); i++) {
	            /* 获取一个字符 */
	            String temp = str.substring(i, i + 1);
	            /* 判断是否为中文字符 */
	            if (temp.matches(chinese)) {
	                valueLength += 2;
	            }
	        }
        }
        return valueLength;
    }
    
    /**
     * 获取字符串的长度.
     * @param text 指定的字符串
     * @return  字符串的长度（中文字符计2个）
     */
     public static int getLength(String text) {
         int valueLength = 0;
         String chinese = "[\u0391-\uFFE5]";
         if(!isEmpty(text)){
	         //获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
	         for (int i = 0; i < text.length(); i++) {
	             //获取一个字符
	             String temp = text.substring(i, i + 1);
	             //判断是否为中文字符
	             if (temp.matches(chinese)) {
	                 //中文字符长度为2
	                 valueLength += 2;
	             } else {
	                 //其他字符长度为1
	                 valueLength += 1;
	             }
	         }
         }
         return valueLength;
     }
     
     /**
      * 获取指定长度的字符所在位置.
      * @param text 指定的字符串
      * @param maxL 要取到的长度（字符长度，中文字符计2个）
      * @return 字符的所在位置
      */
     public static int getIndexFromLength(String text,int maxL) {
    	 int currentIndex = 0;
         int valueLength = 0;
         String chinese = "[\u0391-\uFFE5]";
         //获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
         for (int i = 0; i < text.length(); i++) {
             //获取一个字符
             String temp = text.substring(i, i + 1);
             //判断是否为中文字符
             if (temp.matches(chinese)) {
                 //中文字符长度为2
                 valueLength += 2;
             } else {
                 //其他字符长度为1
                 valueLength += 1;
             }
             if(valueLength >= maxL){
            	 currentIndex = i;
            	 break;
             }
         }
         return currentIndex;
     }
     
    /**
     * 手机号格式验证.
     * @param str 指定的手机号码字符串
     * @return 是否为手机号码格式:是为true，否则false
     */
 	public static Boolean isMobileNo(String str) {
 		Boolean isMobileNo = false;
 		try {
			Pattern p = Pattern.compile("^1[0-9]{10}$");
			Matcher m = p.matcher(str);
			isMobileNo = m.matches();
		} catch (Exception e) {
			e.printStackTrace();
		}
 		return isMobileNo;
 	}
 	
 	/**
	  * 是否只是字母和数字.
	  * @param str 指定的字符串
	  * @return 是否只是字母和数字:是为true，否则false
	  */
 	public static Boolean isNumberLetter(String str) {
		if(isEmpty(str)){
			return false;
		}
 		Boolean isNoLetter = false;
 		String expr = "^[A-Za-z0-9]+$";
 		if (str.matches(expr)) {
 			isNoLetter = true;
 		}
 		return isNoLetter;
 	}

	/**
	 * 是否包含字母和数字.
	 * @param str 指定的字符串
	 * @return 是否只是字母和数字:是为true，否则false
	 */
	public static Boolean isNumberAndLetter(String str) {
		if(isEmpty(str)){
			return false;
		}
		Boolean isNoLetter = false;
		String expr = "^[a-z0-9A-Z]+$";
		if (str.matches(expr)) {
			isNoLetter = true;
		}
		return isNoLetter;
	}

	/**
	 * 是否包含字母和数字和中文.
	 * @param str 指定的字符串
	 * @return 是否只是字母和数字:是为true，否则false
	 */
	public static Boolean isNumberAndLetterAndChinese(String str) {
		if(isEmpty(str)){
			return false;
		}
		Boolean isNoLetter = false;
		String expr = "^[a-z0-9A-Z\u4E00-\u9FA5]+$";
		if (str.matches(expr)) {
			isNoLetter = true;
		}
		return isNoLetter;
	}
 	
 	/**
	  * 是否只是数字.
	  * @param str 指定的字符串
	  * @return 是否只是数字:是为true，否则false
	  */
 	public static Boolean isNumber(String str) {
		if(isEmpty(str)){
			return false;
		}
 		Boolean isNumber = false;
 		String expr = "^[0-9]+$";
 		if (str.matches(expr)) {
 			isNumber = true;
 		}
 		return isNumber;
 	}
 	
 	/**
	  * 是否是邮箱.
	  * @param str 指定的字符串
	  * @return 是否是邮箱:是为true，否则false
	  */
 	public static Boolean isEmail(String str) {
 		Boolean isEmail = false;
 		if(str.indexOf("@") == -1){
			return false;
		}
 		String expr = "^\\s*?(.+)@(.+?)\\s*$";
 		if (str.matches(expr)) {
 			isEmail = true;
 		}
 		return isEmail;
 	}
 	
 	/**
	  * 是否是中文.
	  * @param str 指定的字符串
	  * @return    是否是中文:是为true，否则false
	  */
    public static Boolean isChinese(String str) {
    	Boolean isChinese = true;
        String chinese = "[\u4E00-\u9FA5]";
        if(!isEmpty(str)){
	         //获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
	         for (int i = 0; i < str.length(); i++) {
	             //获取一个字符
	             String temp = str.substring(i, i + 1);
	             //判断是否为中文字符
	             if (temp.matches(chinese)) {
	             }else{
	            	 isChinese = false;
	             }
	         }
        }
        return isChinese;
    }
    
    /**
     * 是否包含中文.
     * @param text 指定的字符串
     * @return  是否包含中文:是为true，否则false
     */
    public static Boolean hasChinese(String text) {
    	Boolean isChinese = false;
        String chinese = "[\u4E00-\u9FA5]";
        if(!isEmpty(text)){
	         //获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
	         for (int i = 0; i < text.length(); i++) {
	             //获取一个字符
	             String temp = text.substring(i, i + 1);
	             //判断是否为中文字符
	             if (temp.matches(chinese)) {
	            	 isChinese = true;
	             }else{
	            	 
	             }
	         }
        }
        return isChinese;
    }
 	
 	/**
	  * 标准化日期时间类型的数据，不足两位的补0.
	  * @param dateTime 预格式的时间字符串，如:2012-3-2 12:2:20
	  * @return String 格式化好的时间字符串，如:2012-03-20 12:02:20
	  */
 	public static String dateTimeFormat(String dateTime) {
		StringBuilder sb = new StringBuilder();
		try {
			if(isEmpty(dateTime)){
				return null;
			}
			String[] dateAndTime = dateTime.split(" ");
			if(dateAndTime.length>0){
				  for(String str : dateAndTime){
					if(str.indexOf("-")!=-1){
						String[] date =  str.split("-");
						for(int i=0;i<date.length;i++){
						  String str1 = date[i];
						  sb.append(strFormat2(str1));
						  if(i< date.length-1){
							  sb.append("-");
						  }
						}
					}else if(str.indexOf(":")!=-1){
						sb.append(" ");
						String[] date =  str.split(":");
						for(int i=0;i<date.length;i++){
						  String str1 = date[i];
						  sb.append(strFormat2(str1));
						  if(i< date.length-1){
							  sb.append(":");
						  }
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
		return sb.toString();
	}
 	
 	/**
	  * 不足2个字符的在前面补“0”.
	  * @param str 指定的字符串
	  * @return 至少2个字符的字符串
	  */
    public static String strFormat2(String str) {
		try {
			if(str.length()<=1){
				str = "0"+str;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return str;
	}

	/**
	 * 2021-11-11
	 * @param str
	 * @return
	 */
	public static String strDateFormatYMD(String str) {
		str = parseEmpty(str);
		if(str.length() > 10){
			str = str.substring(0,10);
		}
		return str;
	}
    
    /**
     * 截取字符串到指定字节长度.
     * @param text the text
     * @param length 指定字节长度
     * @return 截取后的字符串
     */
    public static String getString(String text,int length){
		return getString(text, length,"");
	}
    
    /**
     * 截取字符串到指定字节长度.
     * @param text 文本
     * @param length 字节长度
     * @param dot 省略符号
     * @return 截取后的字符串
     */
	public static String getString(String text,int length,String dot){
		int strBLen = getByteLength(text,"GBK");
		if( strBLen <= length ){
     		return text;
     	}
		int temp = 0;
		StringBuffer sb = new StringBuffer(length);
		char[] ch = text.toCharArray();
		for ( char c : ch ) {
			sb.append( c );
			if ( c > 256 ) {
	    		temp += 2;
	    	} else {
	    		temp += 1;
	    	}
			if (temp >= length) {
				if( dot != null) {
					sb.append( dot );
				}
	            break;
	        }
		}
		return sb.toString();
    }
	
	/**
	 * 截取字符串从第一个指定字符.
	 * @param text1 原文本
	 * @param text2 指定字符
	 * @param offset 偏移的索引
	 * @return 截取后的字符串
	 */
	public static String getFromText(String text1,String text2,int offset){
		if(isEmpty(text1)){
			return "";
		}
		int start = text1.indexOf(text2);
		if(start!=-1){
			if(text1.length()>start+offset){
				return text1.substring(start+offset);
			}
		}
		return "";
    }
	
	/**
	 * 获取字节长度.
	 * @param text 文本
	 * @param charset 字符集（GBK）
	 * @return the int
	 */
	public static int getByteLength(String text,String charset){
		if(text == null || text.length()==0){
			return 0;
		}
		int length = 0;
		try {
			length = text.getBytes(charset).length;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return length;
	}
    
	/**
	 * 获取大小的描述.
	 * @param size 字节个数B
	 * @return  大小的描述
	 */
    public static String getSizeDesc(long size) {
    	 float sizeF = 0;
	   	 String suffix = "B";
	   	 if (size >= 1000){
			suffix = "K";
			sizeF = size/1000f;
			if (sizeF >= 1024){
				suffix = "M";
				sizeF = sizeF/1024f;
				if (sizeF >= 1024){
					suffix = "G";
					sizeF = sizeF/1024f;
		        }
			}
	   	}
        return AbMathUtil.round(sizeF,2) + suffix;
    }
    
    /**
     * ip地址转换为10进制数.
     * @param ip the ip
     * @return the long
     */
    public static long ip2int(String ip){ 
    	ip = ip.replace(".", ",");
    	String[]items = ip.split(","); 
    	return Long.valueOf(items[0])<<24 |Long.valueOf(items[1])<<16 |Long.valueOf(items[2])<<8 |Long.valueOf(items[3]); 
    }

	public static String formatStarName(String name) {
		String newStr;
		if (name.length() == 2) {
			newStr = "*" + name.substring(1, 2);
		} else if (name.length() == 3) {
			newStr = name.substring(0, 1) + "*" + name.substring(2);
		} else if (name.length() == 4) {
			newStr = name.substring(0, 2) + "*" + name.substring(3);
		}  else if (name.length() > 4) {
			newStr = name.substring(0, 2) + "**" + name.substring(4);
		} else {
			newStr = name;
		}
		return newStr;
	}
	
    /**
     * The main method.
     * @param args the arguments
     */
    public static void main(String[] args) {
		System.out.println(dateTimeFormat("2012-3-2 12:2:20"));
		System.out.println(isNumberAndLetterAndChinese("游客111"));
	}

}
