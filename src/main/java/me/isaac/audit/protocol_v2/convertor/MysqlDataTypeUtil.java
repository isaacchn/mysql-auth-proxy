package me.isaac.audit.protocol_v2.convertor;

import cn.hutool.core.util.StrUtil;

import java.util.regex.Pattern;

/**
 * 由字符串判断数据类型
 */
public class MysqlDataTypeUtil {
    //1 int<[123468]>
    //2 int<lenenc>
    //3 string<[0-9]+>
    //4 string<lenenc>
    //5 string<var>,filler_1
    //6 string<EOF>
    //7 string<NUL>
    //8 [(capabilities,function,string<var>,cal-auth-plugin-data-part-2,)]

    public static boolean isFixedLengthInt(String s) {
        String pattern = "^int<[123468]>$";
        return Pattern.matches(pattern, s);
    }

    public static boolean isEncodedLengthInt(String s) {
        return StrUtil.equals("int<lenenc>", s);
    }

    public static boolean isFixedLengthString(String s) {
        String pattern = "^string<[0-9]+>$";
        return Pattern.matches(pattern, s);
    }

    public static boolean isEncodedLengthString(String s) {
        return StrUtil.equals("string<lenenc>", s);
    }

    public static boolean isVariableLengthString(String s) {
        String pattern = "^string<var>,[a-z0-9_]+$";
        return Pattern.matches(pattern, s);
    }

    public static boolean isEofString(String s) {
        return StrUtil.equals("string<EOF>", s);
    }

    public static boolean isNullTerminatedString(String s) {
        return StrUtil.equals("string<NUL>", s);
    }

    public static int getIntFixedLength(String s) {
        return Integer.parseInt(s.substring(4, 5));
    }

    public static int getStringFixedLength(String s) {
        return Integer.parseInt(s.substring(7, s.length() - 1));
    }

    public static String getStringVariableKey(String s) {
        return s.split(",")[1];
    }
}
