package io.jpress.web.admin;

import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.jfinal.kit.Ret;
import io.jboot.utils.StrUtils;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.web.base.AdminControllerBase;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 首页
 * @Package io.jpress.web.admin
 */
@RequestMapping("/admin/commons")
public class _AdminCommonsController extends AdminControllerBase {


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
