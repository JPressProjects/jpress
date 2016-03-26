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
package io.jpress.plugin.message;

import java.util.EventObject;

public class Message extends EventObject {
	
	private static final long serialVersionUID = 1L;
	
	private final long timestamp;
	private String action;

	
	public Message(String action ,Object data) {
		super(data);
		this.action = action;
		this.timestamp = System.currentTimeMillis();
	}
	
	
	@SuppressWarnings("unchecked")
	public <M> M getData(){
		return (M) getSource();
	}
	
	public String getAction(){
		return action;
	}

	
	public long getTimestamp() {
		return this.timestamp;
	}


}
