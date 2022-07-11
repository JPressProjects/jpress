package io.jpress.web.cpatcha;

import com.anji.captcha.model.vo.CaptchaVO;
import com.anji.captcha.service.CaptchaService;
import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.json.JsonBody;

@RequestMapping(value = "/commons/captcha")
public class CaptchaController extends Controller {

    @Inject
    private CaptchaService captchaService;


    public void get(@JsonBody CaptchaVO captchaVO) {
       renderJson(captchaService.get(captchaVO));
    }

    public void check(@JsonBody CaptchaVO captchaVO) {
        renderJson(captchaService.check(captchaVO));
    }



}
