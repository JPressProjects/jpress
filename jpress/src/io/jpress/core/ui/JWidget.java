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
package io.jpress.core.ui;

import java.util.HashMap;

import com.alibaba.fastjson.JSON;

public abstract class JWidget {

	private WidgetConfig config;

	public JWidget() {
		config = new WidgetConfig();
	}
	
	public void put(String key,Object value){
		config.put(key, value);
	}

	public String getFrontEndHtml() {
		return onRenderHtml(config);
	}

	public WidgetConfig getConfig() {
		return config;
	}

	public void setConfig(WidgetConfig config) {
		this.config = config;
	}

	public abstract String onRenderHtmlForSetting(WidgetConfig config);

	public abstract String onRenderHtml(WidgetConfig config);

	/**
	 * @title WidgetConfig 在后台的配置信息
	 * @author Michael Yang （http://www.yangfuhai.com）
	 * @version 1.0
	 * @created 2015年10月27日 下午7:04:26
	 */
	public static class WidgetConfig extends HashMap<String, Object> {
		private static final long serialVersionUID = 1L;

		public String getString(String key) {
			return String.valueOf(get(key));
		}

		public int getInt(String key) {
			Object value = get(key);
			return value == null ? 0 : Integer.valueOf(value.toString());
		}

		public boolean getBoolean(String key) {
			Object value = get(key);
			return value == null ? false : Boolean.valueOf(value.toString());
		}
	}
	
	public static class WidgetStorage{
		private String className;
		private String data;
		
		public WidgetStorage(JWidget widget) {
			className = widget.getClass().getName();
			data = JSON.toJSONString(widget);
		}
		
		public String getClassName() {
			return className;
		}
		public void setClassName(String className) {
			this.className = className;
		}
		public String getData() {
			return data;
		}
		public void setData(String data) {
			this.data = data;
		}
		
		@SuppressWarnings("unchecked")
		public JWidget getWidget(){
			Class<? extends JWidget> clazz = null;
			try {
				clazz = (Class<? extends JWidget>) Class.forName(className);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			if(clazz != null){
				return JSON.parseObject(data, clazz);
			}
			return null;
		}
		
	}

}
