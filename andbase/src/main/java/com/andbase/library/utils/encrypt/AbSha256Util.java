package com.andbase.library.utils.encrypt;

import java.security.MessageDigest;

/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2016/6/14 17:54
 * Email 396196516@qq.com
 * Info sha256工具类
 */
public class AbSha256Util {

    /**
     * sha256 加密
     */
    public static String sha256(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes("UTF-8"));
            return bytesToHexString(hash);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 数组转换成十六进制字符串
     * @param data
     * @return 十六进制字符串
     */
    public static String bytesToHexString(byte[] data) {
        StringBuffer sb = new StringBuffer(data.length);
        String sTemp;
        for (int i = 0; i < data.length; i++) {
            sTemp = Integer.toHexString(0xFF & data[i]);
            if (sTemp.length() < 2) {
                sb.append(0);
            }
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

}
