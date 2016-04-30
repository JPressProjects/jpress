/**
 * Copyright (c) 2015-2016, Michael Yang 杨福海 (fuhai999@gmail.com).
 *
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jpress.wechat;

import java.util.List;

import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.jfinal.MsgController;
import com.jfinal.weixin.sdk.msg.in.InImageMsg;
import com.jfinal.weixin.sdk.msg.in.InLinkMsg;
import com.jfinal.weixin.sdk.msg.in.InLocationMsg;
import com.jfinal.weixin.sdk.msg.in.InMsg;
import com.jfinal.weixin.sdk.msg.in.InShortVideoMsg;
import com.jfinal.weixin.sdk.msg.in.InTextMsg;
import com.jfinal.weixin.sdk.msg.in.InVideoMsg;
import com.jfinal.weixin.sdk.msg.in.InVoiceMsg;
import com.jfinal.weixin.sdk.msg.in.event.InCustomEvent;
import com.jfinal.weixin.sdk.msg.in.event.InFollowEvent;
import com.jfinal.weixin.sdk.msg.in.event.InLocationEvent;
import com.jfinal.weixin.sdk.msg.in.event.InMassEvent;
import com.jfinal.weixin.sdk.msg.in.event.InMenuEvent;
import com.jfinal.weixin.sdk.msg.in.event.InPoiCheckNotifyEvent;
import com.jfinal.weixin.sdk.msg.in.event.InQrCodeEvent;
import com.jfinal.weixin.sdk.msg.in.event.InShakearoundUserShakeEvent;
import com.jfinal.weixin.sdk.msg.in.event.InTemplateMsgEvent;
import com.jfinal.weixin.sdk.msg.in.event.InVerifyFailEvent;
import com.jfinal.weixin.sdk.msg.in.event.InVerifySuccessEvent;
import com.jfinal.weixin.sdk.msg.in.speech_recognition.InSpeechRecognitionResults;
import com.jfinal.weixin.sdk.msg.out.News;
import com.jfinal.weixin.sdk.msg.out.OutMsg;
import com.jfinal.weixin.sdk.msg.out.OutNewsMsg;
import com.jfinal.weixin.sdk.msg.out.OutTextMsg;

import io.jpress.core.Jpress;
import io.jpress.core.annotation.UrlMapping;
import io.jpress.model.Content;
import io.jpress.model.Option;
import io.jpress.template.Module;
import io.jpress.utils.StringUtils;

@UrlMapping(url = "/wechat")
public class WechatMessageController extends MsgController {

	@Override
	public ApiConfig getApiConfig() {
		ApiConfig config = new ApiConfig();
		config.setAppId(Option.findValue("wechat_appid"));
		config.setAppSecret(Option.findValue("wechat_appsecret"));
		config.setToken(Option.findValue("wechat_token"));
		return config;
	}

	// 处理接收到的文本消息
	protected void processInTextMsg(InTextMsg inTextMsg) {
		String text = inTextMsg.getContent();
		processTextReplay(inTextMsg, text);
	}

	// 处理接收到点击菜单事件
	protected void processInMenuEvent(InMenuEvent inMenuEvent) {
		if (InMenuEvent.EVENT_INMENU_CLICK.equals(inMenuEvent.getEvent())) {
			String text = inMenuEvent.getEventKey();
			processTextReplay(inMenuEvent, text);
		}

		renderNull();
	}

	// 处理接收到的图片消息
	protected void processInImageMsg(InImageMsg inImageMsg) {
		processDefaultReplay("wechat_processInImageMsg", inImageMsg);
	}

	// 处理接收到的语音消息
	protected void processInVoiceMsg(InVoiceMsg inVoiceMsg) {
		processDefaultReplay("processInVoiceMsg", inVoiceMsg);
	}

	// 处理接收到的视频消息
	protected void processInVideoMsg(InVideoMsg inVideoMsg) {
		processDefaultReplay("processInVideoMsg", inVideoMsg);
	}

	// 处理接收到的视频消息
	protected void processInShortVideoMsg(InShortVideoMsg inShortVideoMsg) {
		processDefaultReplay("processInShortVideoMsg", inShortVideoMsg);
	}

	// 处理接收到的地址位置消息
	protected void processInLocationMsg(InLocationMsg inLocationMsg) {
		processDefaultReplay("processInLocationMsg", inLocationMsg);
	}

	// 处理接收到的链接消息
	protected void processInLinkMsg(InLinkMsg inLinkMsg) {
		processDefaultReplay("processInLinkMsg", inLinkMsg);
	}

	// 处理接收到的多客服管理事件
	protected void processInCustomEvent(InCustomEvent inCustomEvent) {
		processDefaultReplay("processInCustomEvent", inCustomEvent);
	}

	// 处理接收到的关注/取消关注事件
	protected void processInFollowEvent(InFollowEvent inFollowEvent) {
		processDefaultReplay("processInFollowEvent", inFollowEvent);
	}

	// 处理接收到的扫描带参数二维码事件
	protected void processInQrCodeEvent(InQrCodeEvent inQrCodeEvent) {
		processDefaultReplay("processInQrCodeEvent", inQrCodeEvent);
	}

	// 处理接收到的上报地理位置事件
	protected void processInLocationEvent(InLocationEvent inLocationEvent) {
		processDefaultReplay("processInLocationEvent", inLocationEvent);
	}

	// 处理接收到的群发任务结束时通知事件
	protected void processInMassEvent(InMassEvent inMassEvent) {
		processDefaultReplay("processInMassEvent", inMassEvent);
	}

	// 处理接收到的语音识别结果
	protected void processInSpeechRecognitionResults(InSpeechRecognitionResults inSpeechRecognitionResults) {
		processDefaultReplay("processInSpeechRecognitionResults", inSpeechRecognitionResults);
	}

	// 处理接收到的模板消息是否送达成功通知事件
	protected void processInTemplateMsgEvent(InTemplateMsgEvent inTemplateMsgEvent) {
		processDefaultReplay("processInTemplateMsgEvent", inTemplateMsgEvent);
	}

	// 处理微信摇一摇事件
	protected void processInShakearoundUserShakeEvent(InShakearoundUserShakeEvent inShakearoundUserShakeEvent) {
		processDefaultReplay("processInShakearoundUserShakeEvent", inShakearoundUserShakeEvent);
	}

	// 资质认证成功 || 名称认证成功 || 年审通知 || 认证过期失效通知
	protected void processInVerifySuccessEvent(InVerifySuccessEvent inVerifySuccessEvent) {
		processDefaultReplay("processInVerifySuccessEvent", inVerifySuccessEvent);
	}

	// 资质认证失败 || 名称认证失败
	protected void processInVerifyFailEvent(InVerifyFailEvent inVerifyFailEvent) {
		processDefaultReplay("processInVerifyFailEvent", inVerifyFailEvent);
	}

	// 门店在审核事件消息
	protected void processInPoiCheckNotifyEvent(InPoiCheckNotifyEvent inPoiCheckNotifyEvent) {
		processDefaultReplay("processInPoiCheckNotifyEvent", inPoiCheckNotifyEvent);
	}

	private void processTextReplay(InMsg message, String userInput) {

		// 是否进入多客服
		{
			String string = Option.findValue("wechat_dkf_key");
			// 进入多客服
			if (userInput.equals(string)) {

				return;
			}
		}

		// 是否是搜索
		{
			List<Module> modules = Jpress.currentTemplate().getModules();
			if (modules != null && modules.size() > 0) {
				for (Module module : modules) {
					Boolean bool = Option.findValueAsBool(String.format("wechat_search_%s_enable", module.getName()));
					if (bool != null && bool) {
						String prefix = Option.findValue(String.format("wechat_search_%s_prefix", module.getName()));

						String searcheKey = null;

						if (StringUtils.isNotBlank(prefix)) {
							if (userInput.startsWith(prefix)) {
								searcheKey = userInput.substring(prefix.length());
							}
						} else {
							searcheKey = userInput;
						}

						// 开始搜索
						if (searcheKey != null) {
							Integer count = Option.findValueAsInteger(String.format("wechat_search_%s_count", module.getName()));
							if(count == null || count <= 0 || count >10){
								count = 10;
							}
								
							List<Content> contents = Content.DAO.findByModuleAndTitle(module.getName(), searcheKey, count);
							if (contents != null && contents.size() > 0) {
								OutNewsMsg out = new OutNewsMsg(message);
								for (Content content : contents) {
									News news = new News();
									news.setTitle(content.getTitle());
									news.setDescription(content.getSummary());
									news.setPicUrl(content.getFirstImageUrl());
									news.setUrl(content.getUrl());
									out.addNews(news);
								}
								render(out);
							} else {
								processDefaultReplay("wechat_search_none_content", message);
							}

							return;
						}
					}
				}
			}
		}

		String replyContent = null;

		// 是否是高级回复
		textOrSeniorRender(message, replyContent);

	}


	private void processDefaultReplay(String optionKey, InMsg message) {
		
		String replyContent = Option.findValue(optionKey);
		
		if (!StringUtils.isNotBlank(replyContent)) {
			renderNull();
			return;
		}
		
		textOrSeniorRender(message, replyContent);
	}
	
	private void textOrSeniorRender(InMsg message, String replyContent) {
		if (isSeniorReplay(replyContent)) {
			OutMsg outMsg = ProcesserInvoker.invoke(replyContent, message);
			if (outMsg != null) {
				render(outMsg);
			} else {
				renderConfigErrorMessage(message, replyContent);
			}
		} else {
			OutTextMsg outTextMsg = new OutTextMsg(message);
			outTextMsg.setContent(replyContent);
			render(outTextMsg);
		}
	}

	private void renderConfigErrorMessage(InMsg message, String replyContent) {
		OutTextMsg outTextMsg = new OutTextMsg(message);
		outTextMsg.setContent("配置错误，没有高级回复 " + replyContent + ",请联系网站管理员。");
		render(outTextMsg);
	}

	private static final boolean isSeniorReplay(String string) {
		return string != null && string.startsWith("[") && string.contains("]");
	}
}
