package io.jpress.web.cpatcha;

import com.anji.captcha.service.CaptchaCacheService;
import io.jboot.Jboot;
import io.jboot.components.cache.JbootCache;

public class CaptchaCache implements CaptchaCacheService {

    private static final String cacheName = "_aj_captcha";
    private JbootCache cache = Jboot.getCache();

    @Override
    public void set(String key, String value, long expiresInSeconds) {
        cache.put(cacheName, key, value, (int) expiresInSeconds);
    }

    @Override
    public boolean exists(String key) {
        return cache.get(cacheName, key) != null;
    }

    @Override
    public void delete(String key) {
        cache.remove(cacheName, key);
    }

    @Override
    public String get(String key) {
        return cache.get(cacheName, key);
    }

    @Override
    public String type() {
        return "jboot";
    }
}
