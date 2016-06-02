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
package io.jpress.core.render.freemarker;

import java.math.BigInteger;
import java.util.List;

import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.StringModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

public abstract class JFunction implements TemplateMethodModelEx {

	private List<?> argList;

	@SuppressWarnings("rawtypes")
	@Override
	public Object exec(List args) throws TemplateModelException {

		argList = args;

		return onExec();
	}

	public abstract Object onExec();

	public Object get(int index) {
		if (null == argList || argList.size() == 0)
			return null;

		if (index > argList.size() - 1)
			return null;

		Object obj = argList.get(index);
		if (obj instanceof BeanModel) {
			return ((BeanModel) obj).getWrappedObject();
		}

		return null;
	}

	public StringModel getToStringModel(int index) {
		if (null == argList || argList.size() == 0)
			return null;

		if (index > argList.size() - 1)
			return null;

		return (StringModel) argList.get(index);
	}

	public String getToString(int index) {
		if (null == argList || argList.size() == 0)
			return null;

		if (index > argList.size() - 1)
			return null;

		if (argList.get(index) == null)
			return null;

		return argList.get(index).toString();
	}

	public String getToString(int index, String defaultValue) {
		if (null == argList || argList.size() == 0)
			return defaultValue;

		if (index > argList.size() - 1)
			return defaultValue;

		return argList.get(index).toString();
	}

	public Long getToLong(int index) {

		String stringValue = getToString(index);

		if (null == stringValue || "".equals(stringValue.trim())) {
			return null;
		}

		return Long.parseLong(stringValue);
	}

	public Long getToLong(int index, long defaultValue) {
		String stringValue = getToString(index);

		if (null == stringValue) {
			return defaultValue;
		}

		return Long.parseLong(stringValue);
	}

	public BigInteger getToBigInteger(int index) {

		String stringValue = getToString(index);

		if (null == stringValue || "".equals(stringValue.trim())) {
			return null;
		}

		return new BigInteger(stringValue);
	}

	public BigInteger getToBigInteger(int index, BigInteger defaultValue) {
		String stringValue = getToString(index);

		if (null == stringValue) {
			return defaultValue;
		}

		return new BigInteger(stringValue);
	}

}
