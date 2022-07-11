package io.jpress.web.cpatcha;

import com.anji.captcha.model.common.Const;
import com.anji.captcha.service.CaptchaService;
import com.anji.captcha.service.impl.CaptchaServiceFactory;
import io.jboot.aop.annotation.Bean;
import io.jboot.aop.annotation.Configuration;

import java.util.Properties;

@Configuration
public class CaptchaConfig {


    @Bean
    public static CaptchaService getCaptchaService() {
        Properties config = new Properties();

        //缓存类型 jboot
        //必须在 resources/META-INF/services 添加文件 com.anji.captcha.service.CaptchaCacheService
        config.put(Const.CAPTCHA_CACHETYPE, "jboot");

        //水印
        config.put(Const.CAPTCHA_WATER_MARK, "");
        config.put(Const.CAPTCHA_WATER_FONT, "宋体");

        config.put(Const.CAPTCHA_FONT_TYPE, "宋体");
        config.put(Const.CAPTCHA_TYPE, "default");
        config.put(Const.CAPTCHA_INTERFERENCE_OPTIONS, "0");
        config.put(Const.CAPTCHA_SLIP_OFFSET, "5");
        config.put(Const.CAPTCHA_AES_STATUS, "true");
        config.put(Const.CAPTCHA_CACAHE_MAX_NUMBER, "1000");
        config.put(Const.CAPTCHA_TIMING_CLEAR_SECOND, "180");

        return CaptchaServiceFactory.getInstance(config);
    }


}
