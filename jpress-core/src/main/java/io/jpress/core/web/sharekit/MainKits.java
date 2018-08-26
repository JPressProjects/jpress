package io.jpress.core.web.sharekit;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.core.web.sharekit
 */
public class MainKits {

    public static final String nbsp() {
        return "&nbsp;";
    }


    public static final String nbsp(int count) {
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
}
