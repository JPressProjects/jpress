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
package io.jpress.wechat.processer;

import java.util.List;

import com.jfinal.weixin.sdk.msg.in.InMsg;
import com.jfinal.weixin.sdk.msg.out.News;
import com.jfinal.weixin.sdk.msg.out.OutMsg;
import com.jfinal.weixin.sdk.msg.out.OutNewsMsg;
import com.jfinal.weixin.sdk.msg.out.OutTextMsg;

import io.jpress.model.Content;
import io.jpress.model.query.ContentQuery;
import io.jpress.model.query.OptionQuery;
import io.jpress.utils.StringUtils;
import io.jpress.wechat.IMessageProcesser;
import io.jpress.wechat.MessageProcesser;

@MessageProcesser(key = "newestContents")
public class NewestContentsProcesser implements IMessageProcesser {

	String module;

	@Override
	public void onInit(String configInfo) {
		module = configInfo;
	}

	@Override
	public OutMsg process(InMsg message) {

		String domain = OptionQuery.me().findValue("web_domain");
		if (StringUtils.isBlank(domain)) {
			OutTextMsg otm = new OutTextMsg(message);
			otm.setContent("您还没有配置您的域名，请先在后台的【设置】>【常规】里配置您的网站域名！");
			return otm;
		}

		if (StringUtils.isBlank(module)) {
			OutTextMsg otm = new OutTextMsg(message);
			otm.setContent("配置错误，请添加正确的内容ID。");
			return otm;
		}

		List<Content> contents = ContentQuery.me().findListInNormal(1, 10, module);

		if (contents == null || contents.isEmpty()) {
			OutTextMsg otm = new OutTextMsg(message);
			otm.setContent("暂未找到相应内容！请联系管理员");
			return otm;
		}

		OutNewsMsg out = new OutNewsMsg(message);
		for (Content content : contents) {
			News news = new News();
			news.setTitle(content.getTitle());
			news.setDescription(content.getSummary());
			news.setPicUrl(domain + content.getImage());
			news.setUrl(domain + content.getUrl());
			out.addNews(news);
		}
		return out;
	}

}
