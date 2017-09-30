package com.tzdq.bluetooth;

import android.annotation.SuppressLint;
import android.util.Log;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数据转换工具类
 *
 * @author Administrator
 */
@SuppressLint("SimpleDateFormat")
public class DataUtil {
    private static String TAG = "DataUtil";

    protected DataUtil() {
    }

    // 获取系统时间
    public static String getTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("HHmmss");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        return str;
    }

    // 获取系统日期
    public static String getDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyMMdd");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        return str;
    }

    /**
     * byte[]转变为16进制String字符, 每个字节2位, 不足补0
     */
    public static String getStringByBytes(byte[] bytes) {
        String result = null;
        String hex = null;
        if (bytes != null && bytes.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(bytes.length);
            for (byte byteChar : bytes) {
                hex = Integer.toHexString(byteChar & 0xFF);
                if (hex.length() == 1) {
                    hex = '0' + hex;
                }
                stringBuilder.append(hex.toUpperCase());
            }
            result = stringBuilder.toString();
        }
        Log.i(TAG, "result:" + result);
        return result;
    }

    /**
     * 把16进制String字符转变为byte[]
     */
    public static byte[] getBytesByString(String data) {
        byte[] bytes = null;
        if (data != null) {
            data = data.toUpperCase();//将字符串变成大写
            int length = data.length() / 2;//获取字符串的1/2长度
            char[] dataChars = data.toCharArray();//
            bytes = new byte[length];
            for (int i = 0; i < length; i++) {
                int pos = i * 2;
                bytes[i] = (byte) (charToByte(dataChars[pos]) << 4 | charToByte(dataChars[pos + 1]));
            }
        }
        return bytes;
    }

    /**
     * 取得在16进制字符串中各char所代表的16进制数
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * 从字符串中筛选出数字
     */
    public static String[] getMultiNumFromString(String str) {
        String regEx = "[^0-9\\.]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        //将输入的字符串中非数字部分用空格取代并存入一个字符串
        String string = m.replaceAll(" ").trim();
        //以空格为分割符在讲数字存入一个字符串数组中
        String[] strArr = string.split(" ");
        return strArr;
    }

    /**
     * 16进制字符串转2进制字符串，位数不足8的倍数前面补0
     * @param hex
     * @return
     */
    public static String getBinaryFromHex(String hex){
        String binary = "";
        while (hex.length()>16){
            String hex2 = hex.substring(hex.length()-16, hex.length());
            hex = hex.substring(0, hex.length()-16);
            binary = getBinaryFromHex2(hex2) + binary;
        }
        binary = getBinaryFromHex2(hex) + binary;
        return binary;
    }
    private static String getBinaryFromHex2(String hex){//hex长度必须在16内
        BigInteger bi = new BigInteger(hex, 16);
        String binary = Long.toBinaryString(bi.longValue());
        while (binary.length() % 8 != 0 || binary.length() < hex.length()*4){
            binary = 0 + binary;
        }
        return binary;
    }
}
