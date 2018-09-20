package io.jpress.web.wechat;

import com.jfinal.weixin.sdk.jfinal.MsgController;
import com.jfinal.weixin.sdk.msg.in.InMsg;
import com.jfinal.weixin.sdk.msg.in.InTextMsg;
import com.jfinal.weixin.sdk.msg.out.OutTextMsg;
import io.jpress.core.wechat.WechatAddon;
import io.jpress.core.wechat.WechatAddonConfig;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: (请输入文件名称)
 * @Description: (用一句话描述该文件做什么)
 * @Package io.jpress.web.wechat
 */
@WechatAddonConfig(
        id = "ip.press.helloaddon",
        title = "Hello World",
        description = "这是一个 Hello World 微信插件，方便开发参考。用户输入 hello，返回 world",
        author = "海哥")
public class HelloWechatAddon implements WechatAddon {


    @Override
    public boolean onMatchingMessage(InMsg inMsg, MsgController msgController) {
        if (!(inMsg instanceof InTextMsg)) {
            return false;
        }

        InTextMsg inTextMsg = (InTextMsg) inMsg;
        String content = inTextMsg.getContent();
        return content != null && content.equalsIgnoreCase("hello");
    }


    @Override
    public void onRenderMessage(InMsg inMsg, MsgController msgController) {
        OutTextMsg outTextMsg = new OutTextMsg(inMsg);
        outTextMsg.setContent("world");
        msgController.render(outTextMsg);
    }
}
