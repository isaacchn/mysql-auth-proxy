package me.isaac.audit.util;

import cn.hutool.core.util.StrUtil;

import java.util.regex.Pattern;

public class RegexUtil {
    public static boolean isInt(String str) {
        String pattern = "^int<([123468]|lenenc)>$";
        return Pattern.matches(pattern, str);
    }

    public static boolean isString(String str) {
        String pattern = "^string<(lenenc|fix|var|EOF|NUL)>$";
        return Pattern.matches(pattern, str);
    }

    public static boolean isConditional(String str) {
        return StrUtil.equals("conditional", str);
    }

    //^int<[0-9]*>$
    //int<lenenc>
    //string<lenenc>
    //^string<[0-9]*>$
    //^string<var>,[a-z0-9_]+$
    //string<EOF>
    //string<NUL>
}
