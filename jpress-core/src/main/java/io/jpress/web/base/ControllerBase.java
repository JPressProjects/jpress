package io.jpress.web.base;

import io.jboot.web.controller.JbootController;
import io.jpress.JPressConsts;
import io.jpress.model.User;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web
 */
public abstract class ControllerBase extends JbootController {


    public Long getIdPara() {
        Long id = getParaToLong();
        if (id == null) {

            //renderError 会直接抛出异常，阻止程序往下执行
            renderError(404);
        }

        return id;
    }


    protected void assertNotNull(Object object) {
        if (object == null) {
            renderError(404);
        }
    }


    @Override
    public String getPara(String name) {
        String value = super.getPara(name);
        return "".equals(value) ? null : value;
    }


    protected User getLoginedUser() {
        return getAttr(JPressConsts.ATTR_LOGINED_USER);
    }

}
