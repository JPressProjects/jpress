package io.jpress.commons.utils;

import com.jfinal.core.Controller;
import io.jboot.utils.CookieUtil;
import io.jpress.JPressConsts;
import io.jpress.JPressOptions;

public class JpressCookieUtil {
    public static void put(Controller controller, Long userid){
        int maxAgeInSeconds = JPressOptions.getAsInt("maxAgeInSeconds",604800);
        CookieUtil.put(controller, JPressConsts.COOKIE_UID, String.valueOf(userid),maxAgeInSeconds);
    }
}
