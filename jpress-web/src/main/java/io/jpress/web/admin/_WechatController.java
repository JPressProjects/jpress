package io.jpress.web.admin;

import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.utils.StrUtils;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConstants;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.core.wechat.WechatAddonInfo;
import io.jpress.core.wechat.WechatAddonManager;
import io.jpress.model.WechatReplay;
import io.jpress.service.OptionService;
import io.jpress.service.WechatReplayService;
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
    private WechatReplayService replayService;

    @Inject
    private OptionService optionService;


    @AdminMenu(text = "基础设置", groupId = JPressConstants.SYSTEM_MENU_WECHAT_PUBULIC_ACCOUNT, order = 1)
    public void base() {
        render("wechat/setting_base.html");
    }


    @AdminMenu(text = "菜单设置", groupId = JPressConstants.SYSTEM_MENU_WECHAT_PUBULIC_ACCOUNT, order = 2)
    public void menu() {
        render("wechat/menu.html");
    }


    @AdminMenu(text = "默认回复", groupId = JPressConstants.SYSTEM_MENU_WECHAT_PUBULIC_ACCOUNT, order = 10)
    public void replay() {
        render("wechat/replay_base.html");
    }


    @AdminMenu(text = "自动回复", groupId = JPressConstants.SYSTEM_MENU_WECHAT_PUBULIC_ACCOUNT, order = 11)
    public void keyword() {
        Page<WechatReplay> page = replayService._paginate(getPagePara(), 10, getPara("keyword"), getPara("content"));
        setAttr("page", page);
        render("wechat/replay_list.html");
    }


    public void doDelReplay() {
        Long id = getIdPara();
        replayService.deleteById(id);
        renderJson(Ret.ok());
    }

    public void doDelReplayByIds() {
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
        render(replayService.deleteByIds(idsSet.toArray()) ? Ret.ok() : Ret.fail());
    }


    @AdminMenu(text = "运营插件", groupId = JPressConstants.SYSTEM_MENU_WECHAT_PUBULIC_ACCOUNT, order = 99)
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


    public void replayWrite() {
        int id = getParaToInt(0, 0);
        if (id > 0) {
            WechatReplay wechatReplay = replayService.findById(id);
            setAttr("replay", wechatReplay);
        }
        render("wechat/replay_write.html");
    }

    public void doReplaySave() {
        WechatReplay replay = getBean(WechatReplay.class, "");
        replayService.saveOrUpdate(replay);
        redirect("/admin/wechat/keyword");
    }

}
