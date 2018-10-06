/**
 * Copyright (c) 2016-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jpress.module.article.wechat;

import com.jfinal.weixin.sdk.jfinal.MsgController;
import com.jfinal.weixin.sdk.msg.in.InMsg;
import com.jfinal.weixin.sdk.msg.in.InTextMsg;
import com.jfinal.weixin.sdk.msg.out.OutTextMsg;
import io.jpress.core.wechat.WechatAddonConfig;
import io.jpress.core.wechat.WechatAddon;

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
        description = "输入 article:slug 返回文章内容",
        author = "海哥")
public class ArticleDetailWechatAddon implements WechatAddon {


    @Override
    public boolean onMatchingMessage(InMsg inMsg, MsgController msgController) {
        if (!(inMsg instanceof InTextMsg)) {
            return false;
        }

        InTextMsg inTextMsg = (InTextMsg) inMsg;
        String content = inTextMsg.getContent();
        return content != null && content.startsWith("article:");
    }


    @Override
    public void onRenderMessage(InMsg inMsg, MsgController msgController) {
        OutTextMsg outTextMsg = new OutTextMsg(inMsg);
        outTextMsg.setContent("测试成功");
        msgController.render(outTextMsg);
    }
}
