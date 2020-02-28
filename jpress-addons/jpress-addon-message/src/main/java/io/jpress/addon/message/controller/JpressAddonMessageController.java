package io.jpress.addon.message.controller;

import com.jfinal.aop.Inject;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.addon.message.model.JpressAddonMessage;
import io.jpress.addon.message.service.JpressAddonMessageService;
import io.jpress.commons.utils.CommonsUtils;

/**
 * anjie 2020年2月24日
 */
@RequestMapping(value = "/msgController",viewPath = "/")
public class JpressAddonMessageController extends JbootController{

    @Inject
    private JpressAddonMessageService service;

    public void doSave() {

        JpressAddonMessage entry = getModel(JpressAddonMessage.class);
        //防止xss注入攻击
        CommonsUtils.escapeModel(entry);

        if(entry.getName()==null||entry.getName().trim().equals("")){
            renderJson("{\"msg\":\"对不起名字不能是空\"}");
            return;
        }

        if(entry.getPhone()==null||entry.getPhone().trim().equals("")){
            renderJson("{\"msg\":\"对不起电话不能是空\"}");
            return;
        }
        if(entry.getEmail()==null||entry.getEmail().trim().equals("")){
            renderJson("{\"msg\":\"对不起邮箱不能是空\"}");
            return;
        }
        if(entry.getMessage()==null||entry.getMessage().trim().equals("")){
            renderJson("{\"msg\":\"对不起留言不能是空\"}");
            return;
        }
        entry.setIsshow(false);
        service.save(entry);

        renderJson("{\"msg\":\"ok\"}");
    }

}
