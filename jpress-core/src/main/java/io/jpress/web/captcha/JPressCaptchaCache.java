package io.jpress.web.captcha;

import com.jfinal.captcha.Captcha;
import com.jfinal.captcha.ICaptchaCache;
import io.jboot.Jboot;
import io.jboot.core.cache.JbootCache;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web.captcha
 */
public class JPressCaptchaCache implements ICaptchaCache {

    //验证码缓存的名称
    private static final String CAPTCHA_CACHE_NAME = "captchaCache";

    private JbootCache cache;


    public JPressCaptchaCache() {
        cache = Jboot.me().getCache();
    }

    @Override
    public void put(Captcha captcha) {
        cache.put(CAPTCHA_CACHE_NAME, captcha.getKey(), captcha);
    }

    @Override
    public Captcha get(String key) {
        return cache.get(CAPTCHA_CACHE_NAME, key);
    }

    @Override
    public void remove(String key) {
        cache.remove(CAPTCHA_CACHE_NAME, key);
    }

    @Override
    public void removeAll() {
        cache.removeAll(CAPTCHA_CACHE_NAME);
    }
}
