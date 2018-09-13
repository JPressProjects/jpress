package io.jpress.web.sharekit;

import io.jboot.Jboot;
import io.jpress.service.OptionService;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.core.web.sharekit
 */
public class MainKits {

    public static String blankCount(Integer count) {
        if (count == null || count == 0) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < count; i++) {
            sb.append("&nbsp;");
        }
        return sb.toString();
    }

    public static String escape(String html) {
        if (html == null || html.trim().length() == 0) {
            return "";
        }
        return StringEscapeUtils.escapeHtml(html);
    }


    public static String option(String key) {
        OptionService service = Jboot.bean(OptionService.class);
        return service.findByKey(key);
    }


    public static Boolean optionAsBool(String key) {
        OptionService service = Jboot.bean(OptionService.class);
        return service.findAsBoolByKey(key);
    }



}
