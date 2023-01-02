/**
 * Copyright (c) 2016-2023, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jpress.web.wechat;

import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.log.Log;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.MediaApi;
import com.jfinal.weixin.sdk.jfinal.MsgControllerAdapter;
import com.jfinal.weixin.sdk.jfinal.MsgInterceptor;
import com.jfinal.weixin.sdk.msg.in.InMsg;
import com.jfinal.weixin.sdk.msg.in.InTextMsg;
import com.jfinal.weixin.sdk.msg.in.event.EventInMsg;
import com.jfinal.weixin.sdk.msg.in.event.InFollowEvent;
import com.jfinal.weixin.sdk.msg.in.event.InMenuEvent;
import com.jfinal.weixin.sdk.msg.in.event.InQrCodeEvent;
import com.jfinal.weixin.sdk.msg.out.OutImageMsg;
import com.jfinal.weixin.sdk.msg.out.OutTextMsg;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.commons.utils.AttachmentUtils;
import io.jpress.core.wechat.WechatAddon;
import io.jpress.core.wechat.WechatAddonInfo;
import io.jpress.core.wechat.WechatAddonManager;
import io.jpress.model.WechatReply;
import io.jpress.service.OptionService;
import io.jpress.service.WechatReplyService;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web.wechat
 */
@RequestMapping("/wechat/msg")
public class WechatMsgNotifyController extends MsgControllerAdapter {

    private static final Log LOG = Log.getLog(WechatMsgNotifyController.class);

    @Inject
    private OptionService optionService;

    @Inject
    private WechatReplyService wechatReplyService;

    @Override
    @Before(MsgInterceptor.class)
    public void index() {

        //找到可以接收该消息的微信插件
        List<WechatAddonInfo> addons = getMathingAddon();
        if (addons == null || addons.isEmpty()) {
            //找不到，走默认流程
            super.index();
            return;
        }

        for (WechatAddonInfo addon : addons) {
            try {
                //只要有一个插件成功处理，则不再让下一个插件去处理
                //但是处理不成功，下个插件继续处理该消息
                if (addon.getAddon().onRenderMessage(getInMsg(), this)) {
                    return;
                }
            } catch (Exception ex) {
                LOG.error(ex.toString(), ex);
            }
        }

        //如果所有插件都 处理不成功
        //或者插件本身不做处理，走默认流程
        super.index();
    }



    /**
     * 新用户关注了公众号
     *
     * @param inFollowEvent
     */
    @Override
    protected void processInFollowEvent(InFollowEvent inFollowEvent) {
        if (InFollowEvent.EVENT_INFOLLOW_SUBSCRIBE.equals(inFollowEvent.getEvent())) {
            renderOptionValue("wechat_reply_user_subscribe", "");
        } else {
            renderNull();
        }
    }

    /**
     * 用户扫码了带参数二维码，但是没有任何插件去处理的时候
     * 关注事件等同于关注公众号了
     *
     * @param inQrCodeEvent
     */
    @Override
    protected void processInQrCodeEvent(InQrCodeEvent inQrCodeEvent) {
        if (InQrCodeEvent.EVENT_INQRCODE_SUBSCRIBE.equals(inQrCodeEvent.getEvent())) {
            renderOptionValue("wechat_reply_user_subscribe", "");
        } else {
            super.processInQrCodeEvent(inQrCodeEvent);
        }
    }

    /**
     * 用户发送了文字消息
     *
     * @param inTextMsg
     */
    @Override
    protected void processInTextMsg(InTextMsg inTextMsg) {
        String key = inTextMsg.getContent();
        renderTextMsg(inTextMsg, key);

    }


    private void renderTextMsg(InMsg inMsg, String key) {
        if (StrUtil.isBlank(key)) {
            renderDefault();
            return;
        }

        WechatReply wechatReply = wechatReplyService.findByKey(key);
        if (wechatReply != null && StrUtil.isNotBlank(wechatReply.getContent())) {
            renderWechatReply(inMsg, wechatReply);
        }

        // renderWechatReply(inMsg, wechatReply) 已经渲染成功
        if (getRender() != null) {
            return;
        }

        // 其他情况
        renderDefault();
    }


    private void renderWechatReply(InMsg inMsg, WechatReply wechatReply) {

        if (!wechatReply.isJson()) {
            OutTextMsg outTextMsg = new OutTextMsg(inMsg);
            outTextMsg.setContent(wechatReply.getContent());
            render(outTextMsg);
        }

        /**
         * 发送文本
         */
        else if (wechatReply.isTextType()) {
            OutTextMsg outTextMsg = new OutTextMsg(inMsg);
            outTextMsg.setContent(wechatReply.getText());
            render(outTextMsg);
        }

        /**
         * 发送图片
         */
        else if (wechatReply.isImageType()) {

            File imageFile = AttachmentUtils.file(wechatReply.getImage());
            if (!imageFile.exists()) {
                LOG.error("wechat reply image not exists : " + imageFile);
                renderNull();
                return;
            }

            /**
             * 上传临时素材
             * {"type":"TYPE","media_id":"MEDIA_ID","created_at":123456789}
             */
            ApiResult apiResult = MediaApi.uploadMedia(MediaApi.MediaType.IMAGE, imageFile);
            if (!apiResult.isSucceed()) {
                LOG.error("MediaApi.uploadMedia..." + imageFile + " \n" + apiResult.toString());
                renderNull();
                return;
            }

            String mediaId = apiResult.get("media_id");
            OutImageMsg outImageMsg = new OutImageMsg(inMsg);
            outImageMsg.setMediaId(mediaId);
            render(outImageMsg);
        }

        /**
         * 发送文字 + 图片
         */
        else if (wechatReply.isTextAndImageType()) {

            OutTextMsg outTextMsg = new OutTextMsg(inMsg);
            outTextMsg.setContent(wechatReply.getText());
            render(outTextMsg);

            WechatMsgUtil.sendImageAsync(inMsg.getFromUserName(), wechatReply.getImage());
        }

        /**
         * 发送微信小程序
         */
        else if (wechatReply.isMiniprogramType()) {

            if (!wechatReply.isConfigMiniprogramOk()) {
                OutTextMsg outTextMsg = new OutTextMsg(inMsg);
                outTextMsg.setContent("微信小程序未配置正确。");
                render(outTextMsg);
                return;
            }

            WechatMsgUtil.sendMiniprogram(
                    inMsg.getFromUserName()
                    , wechatReply.getMiniprogramTitle()
                    , wechatReply.getMiniprogramAppId()
                    , wechatReply.getMiniprogramPage()
                    , AttachmentUtils.file(wechatReply.getMiniprogramCover())
            );

            renderNull();
        }
    }

    /**
     * 用户点击了 事件菜单（备注：微信菜单分为多种类型，其实一种是事件类型）
     *
     * @param inMenuEvent
     */
    @Override
    protected void processInMenuEvent(InMenuEvent inMenuEvent) {
        String key = inMenuEvent.getEventKey();
        renderTextMsg(inMenuEvent, key);
    }


    @Override
    protected void renderDefault() {
        //不处理事件消息，否则可能会出现用户进行扫描带参数二维码等回复此内容
        if (getInMsg() instanceof EventInMsg) {
            renderNull();
        } else {
            renderOptionValue("wechat_reply_unknow", "");
        }
    }

    private void renderOptionValue(String optionKey, String defaultText) {
        String text = optionService.findByKey(optionKey);
        if (StrUtil.isBlank(text)) {
            renderText(defaultText);
            return;
        }

        if (text.startsWith("key:") && text.length() > 4) {
            String key = text.substring(4);
            WechatReply wechatReply = wechatReplyService.findByKey(key);
            if (wechatReply != null && StrUtil.isNotBlank(wechatReply.getContent())) {
                renderWechatReply(getInMsg(), wechatReply);
            }
        }

        //以上内容没有进行渲染
        if (getRender() == null) {
            InMsg msg = getInMsg();
            OutTextMsg outMsg = new OutTextMsg(msg);
            outMsg.setContent(text);
            render(outMsg);
        }

    }


    protected List<WechatAddonInfo> getMathingAddon() {
        Collection<WechatAddonInfo> enableAddons = WechatAddonManager.me().getEnableWechatAddons();
        if (enableAddons == null || enableAddons.isEmpty()) {
            return null;
        }

        List<WechatAddonInfo> list = new ArrayList<>();
        for (WechatAddonInfo addonInfo : enableAddons) {
            try {
                WechatAddon addon = addonInfo.getAddon();
                if (addon != null && addon.onMatchingMessage(getInMsg(), this)) {
                    list.add(addonInfo);
                }
            } catch (Exception e) {
                LOG.error(e.toString(), e);
            }
        }

        return list;
    }
}
