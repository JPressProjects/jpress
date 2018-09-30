package io.jpress.web.commons.controller;

import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;
import io.jboot.utils.StrUtils;
import io.jboot.web.controller.annotation.RequestMapping;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 首页
 * @Package io.jpress.web.admin
 */
@RequestMapping("/commons/pinyin")
public class PinyinController extends Controller {


    public void doGetPinyin() {
        String para = getPara();
        if (StrUtils.isBlank(para)) {
            renderJson(Ret.fail());
            return;
        }

        try {
            String pinyin = PinyinHelper.convertToPinyinString(StrUtils.urlDecode(para), "", PinyinFormat.WITHOUT_TONE);
            renderJson(Ret.ok().set("data", pinyin));
            return;
        } catch (PinyinException e) {
            e.printStackTrace();
        }

        renderJson(Ret.fail());
    }
}
