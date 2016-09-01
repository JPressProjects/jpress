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

import java.io.IOException;
import java.io.Writer;
import java.math.BigInteger;
import java.util.Map;

import com.jfinal.log.Log;
import com.jfinal.render.FreeMarkerRender;

import freemarker.core.Environment;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;

public abstract class JTag implements TemplateDirectiveModel {

	private static final Log log = Log.getLog(JTag.class);

	private Environment mEnv;
	private Map<?, ?> mParams;

	private TemplateModel[] mTemplateModels;
	private TemplateDirectiveBody mBody;

	@SuppressWarnings("rawtypes")
	@Override
	public void execute(Environment env, Map params, TemplateModel[] templateModels, TemplateDirectiveBody body)
			throws TemplateException, IOException {

		this.mEnv = env;
		this.mParams = params;
		this.mTemplateModels = templateModels;
		this.mBody = body;
		onRender();

	}

	public abstract void onRender();

	protected void setVariable(String key, Object value) {
		try {
			mEnv.setVariable(key, FreeMarkerRender.getConfiguration().getObjectWrapper().wrap(value));
		} catch (TemplateModelException e) {
			log.error("setVariable(String key,Object value) is error!", e);
		}
	}
	
	protected void renderText(String text) {
		try {
			mEnv.getOut().write(text == null ? "null" : text);
		} catch (IOException e) {
			log.error("JTag renderText error", e);
		}
	}

	protected void renderBody() {
		try {
			mBody.render(mEnv.getOut());
		} catch (TemplateException e) {
			log.error("JTag renderBody is error!", e);
		} catch (IOException e) {
			log.error("JTag renderBody is error!", e);
		}
	}

	protected void renderBody(Writer writer) {
		try {
			mBody.render(writer);
		} catch (TemplateException e) {
			log.error("JTag renderBody(Writer writer) is error!", e);
		} catch (IOException e) {
			log.error("JTag renderBody(Writer writer) is error!", e);
		}
	}

	public TemplateModel[] getTemplateModels() {
		return mTemplateModels;
	}

	public TemplateDirectiveBody getBody() {
		return mBody;
	}

	public Writer getWriter() {
		return mEnv.getOut();
	}

	public String getParam(String key, String defaultValue) {
		String value = getParam(key);
		if (value != null)
			return value;

		return defaultValue;
	}

	public String getParam(String key) {
		TemplateModel model = (TemplateModel) mParams.get(key);
		if (model == null) {
			return null;
		}
		try {
			if (model instanceof TemplateScalarModel) {
				return ((TemplateScalarModel) model).getAsString();
			}
			if ((model instanceof TemplateNumberModel)) {
				return ((TemplateNumberModel) model).getAsNumber().toString();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return null;
	}

	public Long getParamToLong(String key, long defaultValue) {
		Long value = getParamToLong(key);
		if (value != null)
			return value;

		return defaultValue;
	}

	public Long getParamToLong(String key) {
		TemplateModel model = (TemplateModel) mParams.get(key);

		if (model == null) {
			return null;
		}

		try {
			if (model instanceof TemplateNumberModel) {
				return ((TemplateNumberModel) model).getAsNumber().longValue();
			}

			if (model instanceof TemplateScalarModel) {
				String string = ((TemplateScalarModel) model).getAsString();
				if (null == string || "".equals(string.trim())) {
					return null;
				}
				return Long.parseLong(string);
			}
		} catch (Exception e) {
			throw new RuntimeException("must number!", e);
		}

		return null;
	}
	
	public BigInteger getParamToBigInteger(String key, BigInteger defaultValue) {
		BigInteger value = getParamToBigInteger(key);
		if (value != null)
			return value;

		return defaultValue;
	}
	
	public BigInteger getParamToBigInteger(String key) {
		TemplateModel model = (TemplateModel) mParams.get(key);
		
		if (model == null) {
			return null;
		}
		
		try {
			if (model instanceof TemplateNumberModel) {
				long number = ((TemplateNumberModel) model).getAsNumber().longValue();
				return BigInteger.valueOf(number);
			}
			
			if (model instanceof TemplateScalarModel) {
				String string = ((TemplateScalarModel) model).getAsString();
				if (null == string || "".equals(string.trim())) {
					return null;
				}
				return new BigInteger(string);
			}
		} catch (Exception e) {
			throw new RuntimeException("must number!", e);
		}
		
		return null;
	}

	public Integer getParamToInt(String key, Integer defaultValue) {

		Integer value = getParamToInt(key);
		if (null != value)
			return value;

		return defaultValue;
	}

	public Integer getParamToInt(String key) {
		TemplateModel model = (TemplateModel) mParams.get(key);
		if (model == null) {
			return null;
		}

		try {
			if (model instanceof TemplateNumberModel) {
				return ((TemplateNumberModel) model).getAsNumber().intValue();
			}

			if (model instanceof TemplateScalarModel) {
				String string = ((TemplateScalarModel) model).getAsString();
				if (null == string || "".equals(string.trim())) {
					return null;
				}

				return Integer.parseInt(string);

			}
		} catch (Exception e) {
			throw new RuntimeException("must number!", e);
		}

		return null;

	}

	public Integer[] getParamToIntArray(String key) {
		String string = getParam(key);
		if (null == string || "".equals(string.trim())) {
			return null;
		}

		if (!string.contains(",")) {
			return new Integer[] { Integer.valueOf(string.trim()) };
		}

		String[] array = string.split(",");
		Integer[] ids = new Integer[array.length];
		int i = 0;
		try {
			for (String str : array) {
				ids[i++] = Integer.valueOf(str.trim());
			}
			return ids;
		} catch (NumberFormatException e) {
			throw e;
		}
	}

	public Long[] getParamToLongArray(String key) {
		String string = getParam(key);
		if (null == string || "".equals(string.trim())) {
			return null;
		}

		if (!string.contains(",")) {
			return new Long[] { Long.valueOf(string.trim()) };
		}

		String[] array = string.split(",");
		Long[] ids = new Long[array.length];
		int i = 0;
		try {
			for (String str : array) {
				ids[i++] = Long.valueOf(str.trim());
			}
			return ids;
		} catch (NumberFormatException e) {
			throw e;
		}
	}
	
	public BigInteger[] getParamToBigIntegerArray(String key) {
		String string = getParam(key);
		if (null == string || "".equals(string.trim())) {
			return null;
		}
		
		if (!string.contains(",")) {
			return new BigInteger[] { new BigInteger(string.trim()) };
		}
		
		String[] array = string.split(",");
		BigInteger[] ids = new BigInteger[array.length];
		int i = 0;
		try {
			for (String str : array) {
				ids[i++] = new BigInteger(str.trim());
			}
			return ids;
		} catch (NumberFormatException e) {
			throw e;
		}
	}

	public String[] getParamToStringArray(String key) {
		String string = getParam(key);
		if (null == string || "".equals(string.trim())) {
			return null;
		}

		if (!string.contains(",")) {
			return new String[] { string };
		}

		return string.split(",");
	}

	public Boolean getParamToBool(String key, Boolean defaultValue) {
		Boolean value = getParamToBool(key);
		if (value != null)
			return value;

		return defaultValue;
	}

	public Boolean getParamToBool(String key) {
		TemplateModel model = (TemplateModel) mParams.get(key);
		if (model == null) {
			return null;
		}

		try {
			if (model instanceof TemplateBooleanModel) {
				return ((TemplateBooleanModel) model).getAsBoolean();
			}

			if (model instanceof TemplateNumberModel) {
				return !(((TemplateNumberModel) model).getAsNumber().intValue() == 0);
			}

			if (model instanceof TemplateScalarModel) {
				String string = ((TemplateScalarModel) model).getAsString();
				if (null != string && !"".equals(string.trim())) {
					return !(string.equals("0") || string.equalsIgnoreCase("false"));
				} else {
					return null;
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("param must is \"0\",\"1\"  or \"true\",\"false\"", e);
		}

		return null;
	}

}
