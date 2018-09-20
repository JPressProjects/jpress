package io.jpress.web.wechat;

import com.jfinal.weixin.sdk.jfinal.MsgControllerAdapter;
import com.jfinal.weixin.sdk.msg.in.InMsg;
import com.jfinal.weixin.sdk.msg.in.InTextMsg;
import com.jfinal.weixin.sdk.msg.in.event.InFollowEvent;
import com.jfinal.weixin.sdk.msg.in.event.InMenuEvent;
import com.jfinal.weixin.sdk.msg.out.OutTextMsg;
import io.jboot.utils.StringUtils;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.core.wechat.WechatAddon;
import io.jpress.core.wechat.WechatAddonManager;
import io.jpress.service.OptionService;

import javax.inject.Inject;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web.wechat
 */
@RequestMapping("/wechat/msg")
public class WechatMsgNotifyController extends MsgControllerAdapter {

    @Inject
    private OptionService optionService;

    @Override
    public void index() {
        WechatAddon addon = doMathingAddon();
        if (addon == null) {
            super.index();
            return;
        }

        addon.getListener().onRenderMessage(getInMsg(), this);
    }

    /**
     * 新用户关注了公众号
     *
     * @param inFollowEvent
     */
    @Override
    protected void processInFollowEvent(InFollowEvent inFollowEvent) {
        renderOptionValue("wechat_replay_user_subscribe", "");
    }

    /**
     * 用户发送了文字消息
     *
     * @param inTextMsg
     */
    @Override
    protected void processInTextMsg(InTextMsg inTextMsg) {

    }

    /**
     * 用户点击了 事件菜单（备注：微信菜单分为多种类型，其实一种是事件类型）
     *
     * @param inMenuEvent
     */
    @Override
    protected void processInMenuEvent(InMenuEvent inMenuEvent) {

    }


    @Override
    protected void renderDefault() {
        renderOptionValue("wechat_replay_unknow", "");
    }

    private void renderOptionValue(String optionKey, String defaultText) {
        String text = optionService.findByKey(optionKey);
        if (StringUtils.isBlank(text)) {
            renderText(defaultText);
            return;
        }

        InMsg msg = getInMsg();
        OutTextMsg outMsg = new OutTextMsg(msg);
        outMsg.setContent(text);

        render(outMsg);
    }

    protected WechatAddon doMathingAddon() {
        List<WechatAddon> enableAddons = WechatAddonManager.me().getEnableWechatAddons();
        if (enableAddons == null || enableAddons.isEmpty()) {
            return null;
        }

        for (WechatAddon addon : enableAddons) {
            if (addon.getListener().onMatchingMessage(getInMsg(), this)) {
                return addon;
            }
        }

        return null;
    }
}
