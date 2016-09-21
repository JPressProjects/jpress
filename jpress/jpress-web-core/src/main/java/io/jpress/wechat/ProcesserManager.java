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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.log.Log;
import com.jfinal.weixin.sdk.msg.in.InMsg;
import com.jfinal.weixin.sdk.msg.out.OutMsg;

import io.jpress.utils.ClassUtils;
import io.jpress.utils.StringUtils;

public class ProcesserManager {
	private static final Log log = Log.getLog(ProcesserManager.class);
	private static Map<String, Class<? extends IMessageProcesser>> map = new HashMap<String, Class<? extends IMessageProcesser>>();

	private static ProcesserManager me;
	public static ProcesserManager me() {
		if (me == null){
			synchronized (ProcesserManager.class) {
				me = new ProcesserManager();
			}
		}
		return me;
	}
	
	private ProcesserManager() {
		List<Class<IMessageProcesser>> clist = ClassUtils.scanSubClass(IMessageProcesser.class, true);
		if (clist != null && clist.size() > 0) {
			for (Class<? extends IMessageProcesser> clazz : clist) {
				registerProcesser(clazz);
			}
		}
	}

	public OutMsg invoke(String replyContent, InMsg message) {
		IMessageProcesser processer = getProcesser(replyContent);
		return processer == null ? null : processer.process(message);
	}

	public void registerProcesser(Class<? extends IMessageProcesser> clazz) {
		MessageProcesser reply = clazz.getAnnotation(MessageProcesser.class);
		if (null != reply && StringUtils.isNotBlank(reply.key())) {
			map.put("[" + reply.key() + "]", clazz);
		}
	}


	private IMessageProcesser getProcesser(String replyContent) {

		String key = replyContent.substring(0, replyContent.indexOf("]") + 1);
		String config = replyContent.substring(replyContent.indexOf("]") + 1);
		Class<? extends IMessageProcesser> clazz = map.get(key);
		if (clazz == null) {
			return null;
		}
		try {
			IMessageProcesser processer = clazz.newInstance();
			processer.onInit(config);
			return processer;
		} catch (Exception e) {
			log.warn("wechat ProcesserInvoker getProcesser error", e);
		}
		return null;
	}

}
