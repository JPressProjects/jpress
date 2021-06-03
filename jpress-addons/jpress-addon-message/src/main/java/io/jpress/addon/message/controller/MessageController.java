package io.jpress.addon.message.controller;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
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
public class MessageController extends JbootController{

    @Inject
    private JpressAddonMessageService service;

    public void doSave() {

        JpressAddonMessage entry = getModel(JpressAddonMessage.class);
        //防止xss注入攻击
        CommonsUtils.escapeModel(entry);

        if(StrUtil.isBlank(entry.getName())){
            renderJson(Ret.fail().set("msg","对不起名字不能是空"));
            return;
        }

        if(StrUtil.isBlank(entry.getPhone())){
            renderJson(Ret.fail().set("msg","对不起电话不能是空"));
            return;
        }


        if(StrUtil.isBlank(entry.getTitle())){
            renderJson(Ret.fail().set("msg","对不起标题不能是空"));
            return;
        }


        if(StrUtil.isBlank(entry.getContent())){
            renderJson(Ret.fail().set("msg","对不起留言不能是空"));
            return;
        }

        entry.setShow(false);

        service.save(entry);

        renderJson(Ret.ok());
    }

}
