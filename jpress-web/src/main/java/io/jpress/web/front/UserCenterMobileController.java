package io.jpress.web.front;

import com.jfinal.aop.Inject;
import io.jboot.utils.CookieUtil;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConsts;
import io.jpress.model.User;
import io.jpress.service.UserService;
import io.jpress.web.base.TemplateControllerBase;

/**
 * 自定义用户中心
 *
 * @author Eric.Huang
 * @date 2019-03-05 11:57
 * @package io.jpress.web.front
 **/

@RequestMapping("/user/center")
public class UserCenterMobileController extends TemplateControllerBase {

    @Inject
    private UserService userService;

    /**
     * 用户信息页面
     */
    public void index() {

        //不支持渲染用户详情
        if (hasTemplate("user_center.html") == false) {
            renderError(404);
            return;
        }

        Long id = getParaToLong();
        if (id == null) {
            String uid = CookieUtil.get(this, JPressConsts.COOKIE_UID);
            if (StrUtil.isBlank(uid)) {
                renderError(404);
                return;
            }
            id = Long.valueOf(uid);
        }

        User user = userService.findById(id);
        if (user == null) {
            redirect("/user/login");
            return;
        }

        doFlagMenuActive();

        setAttr("user", user.keepSafe());
        render("user_center.html");
    }

    private void doFlagMenuActive() {
        setMenuActive(menu -> menu.getUrl().contains("user/center"));
    }
}
