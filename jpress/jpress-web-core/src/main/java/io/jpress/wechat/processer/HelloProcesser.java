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

import com.jfinal.weixin.sdk.msg.in.InMsg;
import com.jfinal.weixin.sdk.msg.out.OutMsg;
import com.jfinal.weixin.sdk.msg.out.OutTextMsg;

import io.jpress.wechat.IMessageProcesser;
import io.jpress.wechat.MessageProcesser;

@MessageProcesser(key = "hello")
public class HelloProcesser implements IMessageProcesser {

	@Override
	public void onInit(String configInfo) {
		
		
	}
	
	@Override
	public OutMsg process(InMsg message) {
		OutTextMsg out = new OutTextMsg(message);
		out.setContent("hello...欢迎使用JPress高级回复功能。");
		return out;
	}

	

}
