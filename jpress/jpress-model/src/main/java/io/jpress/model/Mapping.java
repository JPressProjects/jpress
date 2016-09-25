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
package io.jpress.model;

import java.math.BigInteger;

import io.jpress.message.Message;
import io.jpress.message.MessageListener;
import io.jpress.message.annotation.Listener;
import io.jpress.model.base.BaseMapping;
import io.jpress.model.core.Table;

@Table(tableName = "mapping", primaryKey = "id")
@Listener(action = { Content.ACTION_ADD, Content.ACTION_DELETE, Content.ACTION_UPDATE }, async = false)
public class Mapping extends BaseMapping<Mapping> implements MessageListener {

	private static final long serialVersionUID = 1L;

	@Override
	public void onMessage(Message message) {
		if (message.getAction().equals(Content.ACTION_DELETE) || (message.getAction().equals(Content.ACTION_UPDATE))) {
			Content c = message.getData();
			removeCache(buildKeyByContentId(c.getId()));
		}

	}

	public static String buildKeyByContentId(BigInteger contentId) {
		return "content:" + contentId;
	}

}
