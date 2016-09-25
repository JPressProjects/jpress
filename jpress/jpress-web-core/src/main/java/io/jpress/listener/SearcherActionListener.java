/**
 * Copyright (c) 2015-2016, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jpress.listener;

import io.jpress.message.Message;
import io.jpress.message.MessageListener;
import io.jpress.message.annotation.Listener;
import io.jpress.model.Content;
import io.jpress.plugin.search.SearcherBean;
import io.jpress.plugin.search.SearcherKit;

@Listener(action = { Content.ACTION_ADD, Content.ACTION_DELETE, Content.ACTION_UPDATE })
public class SearcherActionListener implements MessageListener {

	@Override
	public void onMessage(Message message) {
		// 文章添加到数据库
		if (Content.ACTION_ADD.equals(message.getAction())) {
			SearcherKit.add(createSearcherBean(message));
		}
		// 文章被更新
		else if (Content.ACTION_UPDATE.equals(message.getAction())) {
			SearcherKit.update(createSearcherBean(message));
		}
		// 文章被删除
		else if (Content.ACTION_DELETE.equals(message.getAction())) {
			Content content = message.getData();
			SearcherKit.delete(String.valueOf(content.getId()));
		}
	}

	private SearcherBean createSearcherBean(Message message) {
		Content content = message.getData();

		SearcherBean bean = new SearcherBean();

		bean.setSid(String.valueOf(content.getId()));
		bean.setTitle(content.getTitle());
		bean.setDescription(content.getSummary());
		bean.setContent(content.getText());
		bean.setUrl(content.getUrl());
		bean.setCreated(content.getCreated());
		bean.setData(content);

		return bean;
	}

}
