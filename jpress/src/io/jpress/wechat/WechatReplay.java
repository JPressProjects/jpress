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

import io.jpress.model.Content;

/**
 * @author michael
 */
public class WechatReplay {

	private long id;
	private String key;
	private String content;
	private String trigger;
	private boolean enable;

	public static final String MODULE = "wechat_replay";

	public WechatReplay() {
	}

	public WechatReplay(Content c) {
		this.id = c.getId();
		this.key = c.getTitle();
		this.content = c.getText();
		this.trigger = c.getFlag();
		this.enable = Content.STATUS_NORMAL.equals(c.getStatus()) ? true : false;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTrigger() {
		return trigger;
	}

	public void setTrigger(String trigger) {
		this.trigger = trigger;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public Content toContent() {
		Content c = new Content();
		c.setId(id);
		c.setText(content);
		c.setTitle(key);
		c.setModule(MODULE);
		c.setFlag(trigger);
		c.setStatus(enable ? Content.STATUS_NORMAL : Content.STATUS_DRAFT);
		return c;
	}

}
