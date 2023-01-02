/**
 * Copyright (c) 2016-2023, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jpress.base;


import com.alibaba.fastjson.JSON;
import io.jboot.db.model.JbootModel;
import io.jboot.utils.StrUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于扩展设置有Json字段的Model
 *
 * @param <M>
 */
public class BaseOptionsModel<M extends BaseOptionsModel<M>> extends JbootModel<M> {

    private static final String DEFAULT_OPTIONS_FIELDNAME = "options";

    private Map<String, String> optionMap;

    public void putOption(String key, String value) {
        if (optionMap == null) {
            optionMap = getOptionMap();
        }
        optionMap.put(key, value);
    }

    public String getOption(String key) {
        if (optionMap == null) {
            optionMap = getOptionMap();
        }
        return optionMap.get(key);
    }

    public Boolean getBoolOption(String key) {
        String option = getOption(key);
        return StrUtil.isBlank(option) ? null : Boolean.valueOf(option);
    }

    public boolean getBoolOption(String key, boolean defaultValue) {
        String option = getOption(key);
        return StrUtil.isBlank(option) ? defaultValue : Boolean.valueOf(option);
    }

    public Integer getIntOption(String key) {
        String option = getOption(key);
        return StrUtil.isBlank(option) ? null : Integer.valueOf(option);
    }

    public int getIntOption(String key, int defaultValue) {
        String option = getOption(key);
        return StrUtil.isBlank(option) ? defaultValue : Integer.valueOf(option);
    }

    public Map<String, String> getOptionMap() {
        String optionJson = getOptions();
        return StrUtil.isBlank(optionJson) ? new HashMap<>() : JSON.parseObject(optionJson, HashMap.class);
    }


    public void setOptions(java.lang.String options) {
        set(getOptionsFieldName(), options);
    }

    public java.lang.String getOptions() {
        return getStr(getOptionsFieldName());
    }

    protected String getOptionsFieldName() {
        return DEFAULT_OPTIONS_FIELDNAME;
    }

    @Override
    public boolean save() {
        if (optionMap != null) {
            setOptions(JSON.toJSONString(optionMap));
        }
        return super.save();
    }

    @Override
    public boolean update() {
        if (optionMap != null) {
            setOptions(JSON.toJSONString(optionMap));
        }
        return super.update();
    }
}
