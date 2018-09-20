package io.jpress.core.wechat;

import com.jfinal.weixin.sdk.jfinal.MsgController;
import com.jfinal.weixin.sdk.msg.in.InMsg;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.core.wechat
 */
public interface WechatAddon {

    /**
     * 用来匹配是否由该插件执行
     *
     * @param inMsg
     * @param msgController
     * @return
     */
    public boolean onMatchingMessage(InMsg inMsg, MsgController msgController);
    

    /**
     * 执行回复逻辑
     *
     * @param inMsg
     * @param msgController
     */
    public void onRenderMessage(InMsg inMsg, MsgController msgController);

}
