package io.jpress.addon.wechat;

import com.jfinal.weixin.sdk.jfinal.MsgController;
import com.jfinal.weixin.sdk.msg.in.InMsg;
import io.jpress.core.wechat.WechatAddon;

/**
 * 微信插件
 *
 * @author Eric.Huang
 * @date 2019-03-13 17:53
 * @package io.jpress.addon.wechat
 **/

public class WechatMangeAddon implements WechatAddon {
    /**
     * 用来匹配是否由该插件执行
     *
     * @param inMsg
     * @param msgController
     * @return
     */
    @Override
    public boolean onMatchingMessage(InMsg inMsg, MsgController msgController) {
        return false;
    }

    /**
     * 执行回复逻辑
     *
     * @param inMsg
     * @param msgController
     */
    @Override
    public boolean onRenderMessage(InMsg inMsg, MsgController msgController) {
        return false;
    }
}
