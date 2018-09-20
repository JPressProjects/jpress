package io.jpress.module.article.wechat;

import com.jfinal.weixin.sdk.jfinal.MsgController;
import com.jfinal.weixin.sdk.msg.in.InMsg;
import com.jfinal.weixin.sdk.msg.in.InTextMsg;
import com.jfinal.weixin.sdk.msg.out.OutTextMsg;
import io.jpress.core.wechat.WechatAddonConfig;
import io.jpress.core.wechat.WechatAddonListener;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 用于文章查看的微信插件
 * @Description: 输入关键字内容为：文章:slug
 * @Package io.jpress.module.article.wechat
 */
@WechatAddonConfig(
        id = "ip.press.article",
        title = "文章查看",
        description = "输入文章：slug 返回文章内容",
        author = "海哥")
public class ArticleDetailWechatAddon implements WechatAddonListener {


    @Override
    public boolean onMatchingMessage(InMsg inMsg, MsgController msgController) {
        if (!(inMsg instanceof InTextMsg)) {
            return false;
        }

        InTextMsg inTextMsg = (InTextMsg) inMsg;
        String content = inTextMsg.getContent();
        return content != null && content.startsWith("文章:");
    }


    @Override
    public void onRenderMessage(InMsg inMsg, MsgController msgController) {
        OutTextMsg outTextMsg = new OutTextMsg(inMsg);
        outTextMsg.setContent("测试成功");
        msgController.render(outTextMsg);
    }
}
