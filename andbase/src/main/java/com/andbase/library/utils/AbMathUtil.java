package com.andbase.library.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2016/6/14 17:54
 * Email 396196516@qq.com
 * Info 数学工具类
 */
public class AbMathUtil {

    /**
     * 四舍五入
     * @param number  原数
     * @param decimal 保留几位小数
     * @return 四舍五入后的值
     */
    public static double round(double number, int decimal) {
        return new BigDecimal(number).setScale(decimal, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 四舍五入
     * @param number  原数
     * @param decimal 保留几位小数
     * @return 四舍五入后的值
     */
    public static String roundString(double number, int decimal) {
        return new BigDecimal(number).setScale(decimal, BigDecimal.ROUND_HALF_UP).toString();
    }

    /**
     * 四舍五入
     * @param number  原数
     * @param decimal 保留几位小数
     * @return 四舍五入后的值
     */
    public static double roundInteger(double number, int decimal) {
        return new BigDecimal(number).setScale(decimal, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 截取2位小数
     * @param number 原数
     * @return 四舍五入后的值
     */
    public static String formatTwo(String number) {
        return String.format("%.2f", Double.parseDouble(number));
    }

    /**
     * 截取2位小数
     * @param number 原数
     * @return 四舍五入后的值
     */
    public static String formatTwo(double number) {
        return String.format("%.2f", number);
    }

    /**
     * 把.0去掉
     * @param number 原数
     * @return
     */
    public static String formatZero(double number) {
        DecimalFormat decimalFormat = new DecimalFormat("###################.###########");
        return decimalFormat.format(number);
    }

    /**
     * 每3位加逗号
     * @param number
     * @return
     */
    public static String format3Number(float number) {
        DecimalFormat df = new DecimalFormat("#,###.00");
        return df.format(number);
    }

    /**
     * 每3位加逗号
     * @param number
     * @return
     */
    public static String format3Number(double number) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(number);
    }


    /**
     * 字节数组转换成16进制串.
     * @param b      the b
     * @param length the length
     * @return the string
     */
    public static String byte2HexStr(byte[] b, int length) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < length; ++n) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            if (stmp.length() == 1)
                hs = hs + "0" + stmp;
            else {
                hs = hs + stmp;
            }
            hs = hs + ",";
        }
        return hs.toUpperCase();
    }

    /**
     * 二进制转为十六进制.
     * @param binary the binary
     * @return char hex
     */
    public static char binaryToHex(int binary) {
        char ch = ' ';
        switch (binary) {
            case 0:
                ch = '0';
                break;
            case 1:
                ch = '1';
                break;
            case 2:
                ch = '2';
                break;
            case 3:
                ch = '3';
                break;
            case 4:
                ch = '4';
                break;
            case 5:
                ch = '5';
                break;
            case 6:
                ch = '6';
                break;
            case 7:
                ch = '7';
                break;
            case 8:
                ch = '8';
                break;
            case 9:
                ch = '9';
                break;
            case 10:
                ch = 'a';
                break;
            case 11:
                ch = 'b';
                break;
            case 12:
                ch = 'c';
                break;
            case 13:
                ch = 'd';
                break;
            case 14:
                ch = 'e';
                break;
            case 15:
                ch = 'f';
                break;
            default:
                ch = ' ';
        }
        return ch;
    }


    /**
     * 一维数组转为二维数组
     * @param m      the m
     * @param width  the width
     * @param height the height
     * @return the int[][]
     */
    public static int[][] arrayToMatrix(int[] m, int width, int height) {
        int[][] result = new int[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int p = j * height + i;
                result[i][j] = m[p];
            }
        }
        return result;
    }


    /**
     * 二维数组转为一维数组
     * @param m the m
     * @return the double[]
     */
    public static double[] matrixToArray(double[][] m) {
        int p = m.length * m[0].length;
        double[] result = new double[p];
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[i].length; j++) {
                int q = j * m.length + i;
                result[q] = m[i][j];
            }
        }
        return result;
    }

    /**
     * int数组转换为double数组.
     * @param input the input
     * @return the double[]
     */
    public static double[] intToDoubleArray(int[] input) {
        int length = input.length;
        double[] output = new double[length];
        for (int i = 0; i < length; i++) {
            output[i] = Double.valueOf(String.valueOf(input[i]));
        }
        return output;
    }

    /**
     * int二维数组转换为double二维数组.
     * @param input the input
     * @return the double[][]
     */
    public static double[][] intToDoubleMatrix(int[][] input) {
        int height = input.length;
        int width = input[0].length;
        double[][] output = new double[height][width];
        for (int i = 0; i < height; i++) {
            // 列   
            for (int j = 0; j < width; j++) {
                // 行   
                output[i][j] = Double.valueOf(String.valueOf(input[i][j]));
            }
        }
        return output;
    }

    /**
     * 计算数组的平均值.
     * @param pixels 数组
     * @return int 平均值
     */
    public static int average(int[] pixels) {
        float m = 0;
        for (int i = 0; i < pixels.length; ++i) {
            m += pixels[i];
        }
        m = m / pixels.length;
        return (int) m;
    }

    /**
     * 计算数组的平均值.
     * @param pixels 数组
     * @return int 平均值
     */
    public static int average(double[] pixels) {
        float m = 0;
        for (int i = 0; i < pixels.length; ++i) {
            m += pixels[i];
        }
        m = m / pixels.length;
        return (int) m;
    }

    /**
     * 计算对数
     * @param value value的对数
     * @param base  以base为底
     * @return
     */
    public static double log(double value, double base) {
        return Math.log(value) / Math.log(base);
    }


    public static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * 根据两点间经纬度坐标（double值），计算两点间距离，单位为米.
     * @param longitude1
     * @param latitude1
     * @param longitude2
     * @param latitude2
     * @return
     */
    public static double getGeoDistance(double longitude1, double latitude1, double longitude2, double latitude2) {
        double EARTH_RADIUS = 6378137;
        double radLat1 = rad(latitude1);
        double radLat2 = rad(latitude2);
        double a = radLat1 - radLat2;
        double b = rad(longitude1) - rad(longitude2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }

    /**
     * 去掉最大和最小  再求平均值
     * @param values
     * @return
     */
    public static double averageOptimization(double[] values) {
        double tem, sum = 0;
        for (int i = 0; i < values.length - 1; i++) {
            for (int j = 0; j < values.length - 1; j++) {
                if (values[j] < values[j + 1]) {
                    tem = values[j];
                    values[j] = values[j + 1];
                    values[j + 1] = tem;
                }
            }
        }
        for (int i = 0; i < values.length; i++) {
            if (i != 0 && i != values.length - 1) {
                sum = values[i];
            }
        }
        return sum / (values.length - 2);
    }


    public static void main(String[] args) {
        //System.out.println(formatTwo("181895.515"));

        System.out.println(format3Number(AbMathUtil.roundInteger(2154807067.520000d,0)));
    }

}
