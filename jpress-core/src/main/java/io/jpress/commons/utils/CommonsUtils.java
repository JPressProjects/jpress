package io.jpress.commons.utils;

import io.jboot.utils.StrUtils;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.commons.utils
 */
public class CommonsUtils {

    public static String maxLength(String content, int maxLength) {
        if (StrUtils.isBlank(content)) {
            return content;
        }

        if (maxLength <= 0) {
            throw new IllegalArgumentException("maxLength 必须大于 0 ");
        }

        return content.length() <= maxLength ? content :
                content.substring(0, maxLength);

    }
}
