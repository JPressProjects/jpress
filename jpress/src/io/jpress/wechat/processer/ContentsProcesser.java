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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.jfinal.weixin.sdk.msg.in.InMsg;
import com.jfinal.weixin.sdk.msg.out.News;
import com.jfinal.weixin.sdk.msg.out.OutMsg;
import com.jfinal.weixin.sdk.msg.out.OutNewsMsg;

import io.jpress.model.Content;
import io.jpress.model.Option;
import io.jpress.utils.StringUtils;
import io.jpress.wechat.IMessageProcesser;
import io.jpress.wechat.MessageProcesser;

@MessageProcesser(key = "contents")
public class ContentsProcesser implements IMessageProcesser {

	List<BigInteger> contentIds;

	@Override
	public void onInit(String configInfo) {
		if (StringUtils.isNotBlank(configInfo)) {
			String[] ids = configInfo.split(",");
			if (ids != null && ids.length > 0) {
				contentIds = new ArrayList<BigInteger>();
				for(String id : ids){
					try {
						contentIds.add(new BigInteger(id.trim()));
					} catch (Exception e) {}
				}
			}
		}
	}

	@Override
	public OutMsg process(InMsg message) {
		List<Content> contents = new ArrayList<Content>();
		
		if(contentIds!=null && contentIds.size() > 0){
			for(BigInteger id : contentIds){
				contents.add(Content.DAO.findById(id));
			}
		}
		
		OutNewsMsg out = new OutNewsMsg(message);
		if(!contents.isEmpty()){
			for (Content content : contents) {
				News news = new News();
				news.setTitle(content.getTitle());
				news.setDescription(content.getSummary());
				news.setPicUrl(content.getFirstImage());
				news.setUrl(content.getUrl());
				out.addNews(news);
			}
		}else{
			News news = new News();
			news.setTitle("配置错误，暂为找到相应内容！");
			news.setDescription("配置错误，请管理员查看JPress帮助文档....");
			news.setUrl("http://jpress.io");
			news.setPicUrl(Option.findValue("web_domain")+"/static/jpress/admin/image/nothunmbnail.jpg");
			out.addNews(news);
		}
		
		return out;
	}

}
