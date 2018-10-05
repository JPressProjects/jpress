package io.jpress.web.admin;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.weixin.sdk.api.ApiResult;
import io.jboot.utils.StrUtils;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.wechat.WechatApis;
import io.jpress.JPressConsts;
import io.jpress.commons.layer.SortKit;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.core.wechat.WechatAddonInfo;
import io.jpress.core.wechat.WechatAddonManager;
import io.jpress.model.WechatMenu;
import io.jpress.model.WechatReply;
import io.jpress.service.OptionService;
import io.jpress.service.WechatMenuService;
import io.jpress.service.WechatReplyService;
import io.jpress.web.base.AdminControllerBase;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 首页
 * @Package io.jpress.web.admin
 */
@RequestMapping("/admin/wechat")
public class _WechatController extends AdminControllerBase {

    @Inject
    private WechatReplyService replyService;

    @Inject
    private OptionService optionService;

    @Inject
    private WechatMenuService wechatMenuService;


    @AdminMenu(text = "基础设置", groupId = JPressConsts.SYSTEM_MENU_WECHAT_PUBULIC_ACCOUNT, order = 1)
    public void base() {
        render("wechat/setting_base.html");
    }


    @AdminMenu(text = "菜单设置", groupId = JPressConsts.SYSTEM_MENU_WECHAT_PUBULIC_ACCOUNT, order = 2)
    public void menu() {
        List<WechatMenu> menus = wechatMenuService.findAll();
        SortKit.toLayer(menus);
        setAttr("menus", menus);

        int id = getParaToInt(0, 0);
        if (id > 0) {
            for (WechatMenu menu : menus) {
                if (menu.getId() == id) {
                    setAttr("menu", menu);
                }
            }
        }
        render("wechat/menu.html");
    }


    @AdminMenu(text = "默认回复", groupId = JPressConsts.SYSTEM_MENU_WECHAT_PUBULIC_ACCOUNT, order = 10)
    public void reply() {
        render("wechat/reply_base.html");
    }


    @AdminMenu(text = "自动回复", groupId = JPressConsts.SYSTEM_MENU_WECHAT_PUBULIC_ACCOUNT, order = 11)
    public void keyword() {
        Page<WechatReply> page = replyService._paginate(getPagePara(), 10, getPara("keyword"), getPara("content"));
        setAttr("page", page);
        render("wechat/reply_list.html");
    }


    public void doDelReply() {
        Long id = getIdPara();
        replyService.deleteById(id);
        renderJson(Ret.ok());
    }

    public void doDelReplyByIds() {
        String ids = getPara("ids");
        if (StrUtils.isBlank(ids)) {
            renderJson(Ret.fail());
            return;
        }

        Set<String> idsSet = StrUtils.splitToSet(ids, ",");
        if (idsSet == null || idsSet.isEmpty()) {
            renderJson(Ret.fail());
            return;
        }
        render(replyService.deleteByIds(idsSet.toArray()) ? Ret.ok() : Ret.fail());
    }


    @AdminMenu(text = "运营插件", groupId = JPressConsts.SYSTEM_MENU_WECHAT_PUBULIC_ACCOUNT, order = 99)
    public void addons() {
        List<WechatAddonInfo> wechatAddons = WechatAddonManager.me().getWechatAddons();
        setAttr("wechatAddons", wechatAddons);
        render("wechat/addons.html");
    }

    public void doEnableAddon(String id) {
        WechatAddonManager.me().doEnableAddon(id);
        renderJson(Ret.ok());
    }

    public void doCloseAddon(String id) {
        WechatAddonManager.me().doCloseAddon(id);
        renderJson(Ret.ok());
    }


    public void replyWrite() {
        int id = getParaToInt(0, 0);
        if (id > 0) {
            WechatReply wechatReply = replyService.findById(id);
            setAttr("reply", wechatReply);
        }
        render("wechat/reply_write.html");
    }

    public void doReplySave() {
        WechatReply reply = getBean(WechatReply.class, "");
        replyService.saveOrUpdate(reply);
        redirect("/admin/wechat/keyword");
    }

    public void doMenuSave() {
        WechatMenu menu = getModel(WechatMenu.class, "menu");
        wechatMenuService.saveOrUpdate(menu);
        redirect("/admin/wechat/menu");
    }

    /**
     * 微信菜单同步
     */
    public void doMenuSync() {
        List<WechatMenu> wechatMenus = wechatMenuService.findAll();
        SortKit.toTree(wechatMenus);

        if (wechatMenus == null || wechatMenus.isEmpty()) {
            renderJson(Ret.fail().set("message", "微信菜单为空"));
            return;
        }

        JSONArray button = new JSONArray();
        for (WechatMenu wechatMenu : wechatMenus) {
            if (wechatMenu.hasChild()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", wechatMenu.getText());
                List<WechatMenu> childMenus = wechatMenu.getChilds();
                JSONArray sub_buttons = new JSONArray();
                for (WechatMenu child : childMenus) {
                    createJsonObjectButton(sub_buttons, child);
                }
                jsonObject.put("sub_button", sub_buttons);
                button.add(jsonObject);
            } else {
                createJsonObjectButton(button, wechatMenu);
            }
        }

        JSONObject wechatMenuJson = new JSONObject();
        wechatMenuJson.put("button", button);
        String jsonString = wechatMenuJson.toJSONString();

        ApiResult result = WechatApis.createMenu(jsonString);
        if (result.isSucceed()) {
            renderJson(Ret.ok().set("message", "微信菜单同步成功"));
        } else {
            renderJson(Ret.fail().set("message", "错误码：" + result.getErrorCode() + "，" + result.getErrorMsg()));
        }

    }

    private void createJsonObjectButton(JSONArray button, WechatMenu content) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", content.getType());
        jsonObject.put("name", content.getText());

        if ("view".equals(content.getType())) {
            jsonObject.put("url", content.getText());
        } else {
            jsonObject.put("key", content.getText());
        }

        button.add(jsonObject);
    }

}
