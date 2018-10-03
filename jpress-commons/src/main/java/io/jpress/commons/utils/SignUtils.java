package io.jpress.commons.utils;

import com.jfinal.kit.HashKit;
import io.jboot.utils.StrUtils;

import java.util.Arrays;
import java.util.Map;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.commons.utils
 */
public class SignUtils {

    public static String signForRequest(Map<String, String> params, String secret) {
        String[] keys = params.keySet().toArray(new String[0]);
        Arrays.sort(keys);

        StringBuilder query = new StringBuilder();
        query.append(secret);
        for (String key : keys) {
            String value = params.get(key);
            if (StrUtils.areNotEmpty(key, value)) {
                query.append(key).append(value);
            }
        }
        query.append(secret);
        return HashKit.md5(query.toString()).toUpperCase();
    }
}
