package io.jpress.addon.wechat.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符匹配工具类
 *
 * @author Eric.Huang
 * @date 2019-03-13 18:17
 * @package io.jpress.addon.wechat.util
 **/

public class MatcherUtil {

    public static String match(String regex, String input){
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(input);
        if(m.find()){
            return m.group(1);
        }
        return null;
    }
}
