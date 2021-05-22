package com.andbase.library.bluetooth.utils;

import java.util.List;

/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2018/04/13 09:13
 * Email 396196516@qq.com
 * Info 一些蓝牙工具类
 */

public class AbBluetoothHexUtil {

    /**
     * byte[]转16进制字符串
     * @param data
     * @return
     */
    public static String toHexString(byte[] data,boolean blank){
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            sb.append(toHexString(data[i]));

            if(blank){
                if(i < data.length -1){
                    sb.append(" ");
                }
                /*if(i!=0 && (i+1)%4==0){
                    sb.append(" ");
                }*/
            }


        }
        return sb.toString();
    }

    /**
     * byte转16进制字符串
     * @param data
     * @return
     */
    public static String toHexString(byte data){
        String hex = Integer.toHexString(data & 0xFF);
        if (hex.length() == 1) {
            hex = '0' + hex;
        }
        return hex.toUpperCase();
    }


    /**
     * 16进制字符串转byte[]
     * @param hexString
     * @return
     */
    public static byte[] toBytes(String hexString) {
        if(hexString == null || hexString.trim().equals("")) {
            return new byte[0];
        }

        byte[] bytes = new byte[hexString.length() / 2];
        for(int i = 0; i < hexString.length() / 2; i++) {
            String subStr = hexString.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }
        return bytes;
    }

    /**
     * byte表示的ascii转String
     * @param ascii
     * @return
     */
    public static String asciiToString(byte ascii) {
        StringBuffer sbu = new StringBuffer();
        sbu.append((char)ascii);
        return sbu.toString().toUpperCase();
    }


    /**
     * 数据连接
     * @param dataRow
     * @return
     */
    public static byte[] linkData(List<byte[]> dataRow){

        int length = 0;
        for(byte[] byteDataItem : dataRow){
            length += byteDataItem.length;
        }
        byte[] data = new byte[length];
        int index = 0;
        for(int i=0;i<dataRow.size();i++){
            byte[] bs = dataRow.get(i);
            for(byte b:bs){
                data[index++] = b;
            }
        }
        return  data;
    }

    /**
     * 字符串不足长度补足
     * @param str
     * @param strLength
     * @return
     */
    public static String addZeroForNum(String str, int strLength) {
        int strLen = str.length();
        if (strLen < strLength) {
            while (strLen < strLength) {
                StringBuffer sb = new StringBuffer();
                sb.append("0").append(str);
                str = sb.toString();
                strLen = str.length();
            }
        }

        return str;
    }

    /**
     * 校验和
     * @param data
     * @return
     */
    public static String sum(byte [] data){
        int sum = 0;
        for(byte b:data){
            sum += (int)(b & 0xFF);
        }
        //不足8位补0
        String sumStr = String.format("%08x",sum);
        //每2位哟个空格拆分
        String regex = "(.{2})";
        sumStr = sumStr.replaceAll (regex, "$1 ").trim();
        return sumStr;
    }

    /**
     * 校验和
     * @param hexString  16进制字符串
     * @return
     */
    public static int sum(String hexString){
        byte[] data = toBytes(hexString);
        int sum = 0;
        for(byte b:data){
            sum += (int)(b & 0xFF);
        }
        return sum;
    }
}
