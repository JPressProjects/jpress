package io.jpress.commons.utils;


import com.jfinal.core.JFinal;
import io.jpress.JPressOptions;

public class UrlUtils {


    public static String getUrl(Object... paths) {
        return buildUrl(paths);
    }


    private static String buildUrl(Object... paths) {
        boolean isWebFlatUrlEnable = JPressOptions.isFlatUrlEnable();
        StringBuilder url = new StringBuilder(JFinal.me().getContextPath());
        for (int i = 0; i < paths.length; i++) {
            Object path = paths[i];
            if (path == null){
                continue;
            }
            if (isWebFlatUrlEnable) {
                url.append(toFlat(i, path.toString()));
            } else {
                url.append(path);
            }
        }
        return url.append(JPressOptions.getAppUrlSuffix()).toString();
    }


    /**
     * 把 /xxx/xxx 类型的 url 转换为 /xxx-xxx
     * @param index
     * @param path
     * @return
     */
    private static String toFlat(int index, String path) {
        char[] chars = new char[path.length()];
        path.getChars(index == 0 ? 1 : 0, chars.length, chars, index == 0 ? 1 : 0);
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '/') {
                chars[i] = '-';
            }
        }
        if (index == 0) {
            chars[0] = '/';
        }
        return new String(chars);
    }


}
