package io.jpress.web.commons.controller;

import com.jfinal.core.Controller;
import io.jboot.web.controller.annotation.RequestMapping;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web
 */
@RequestMapping("/commons/captcha")
public class CaptchaController extends Controller {


    public void index() {
        renderCaptcha();
    }


}
