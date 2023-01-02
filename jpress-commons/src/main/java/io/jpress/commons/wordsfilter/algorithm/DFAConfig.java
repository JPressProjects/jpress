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
package io.jpress.commons.wordsfilter.algorithm;

import java.util.HashSet;
import java.util.Set;


public class DFAConfig {

    /**
     * 是否支持拼音，配置汉字只能识别汉字，不能识别拼音
     */
    private boolean supportPinyin = false;
    /**
     * 忽略大小写，默认大小写敏感
     */
    private boolean ignoreCase = false;
    /**
     * 支持简体，繁体，默认不支持，配置简体只能识别简体
     */
    private boolean supportSimpleTraditional = false;
    /**
     * 支持半角全角，默认不支持，配置半角只能识别半角
     */
    private boolean supportDbc = false;
    /**
     * 支持停顿词，默认不支持，字之间使用别的字符隔开不识别
     */
    private boolean supportStopWord = false;

    private String stopWord = "";

    private Set<Character> stopWordCharSet = new HashSet<>();

    public boolean isSupportPinyin() {
        return supportPinyin;
    }

    public void setSupportPinyin(boolean supportPinyin) {
        this.supportPinyin = supportPinyin;
    }

    public boolean isIgnoreCase() {
        return ignoreCase;
    }

    public void setIgnoreCase(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }

    public boolean isSupportSimpleTraditional() {
        return supportSimpleTraditional;
    }

    public void setSupportSimpleTraditional(boolean supportSimpleTraditional) {
        this.supportSimpleTraditional = supportSimpleTraditional;
    }

    public boolean isSupportDbc() {
        return supportDbc;
    }

    public void setSupportDbc(boolean supportDbc) {
        this.supportDbc = supportDbc;
    }

    public boolean isSupportStopWord() {
        return supportStopWord;
    }

    public void setSupportStopWord(boolean supportStopWord) {
        this.supportStopWord = supportStopWord;
    }

    public String getStopWord() {
        return stopWord;
    }

    public void setStopWord(String stopWord) {
        this.stopWord = stopWord;
        this.stopWordCharSet.clear();
        if (this.stopWord != null) {
            for (char c : this.stopWord.toCharArray()) {
                stopWordCharSet.add(c);
            }
        }
    }

    public boolean containsStopChar(char ch) {
        return this.stopWordCharSet.contains(ch);
    }

    public DFAConfig() {

    }

    public static class Builder {

        private boolean supportPinyin = false;

        private boolean ignoreCase = false;

        private boolean supportSimpleTraditional = false;

        private boolean supportDbc = false;

        private boolean supportStopWord = false;

        private String stopWord = "";

        public Builder() {

        }

        /**
         * 是否支持拼音，配置汉字只能识别汉字，不能识别拼音
         */
        public Builder setSupportPinyin(boolean support) {
            this.supportPinyin = support;
            return this;
        }

        /**
         * 忽略大小写，默认大小写敏感
         */
        public Builder setIgnoreCase(boolean ignoreCase) {
            this.ignoreCase = ignoreCase;
            return this;
        }

        /**
         * 支持简体，繁体，默认不支持，配置简体只能识别简体
         */
        public Builder setSupportSimpleTraditional(boolean supportSimpleTraditional) {
            this.supportSimpleTraditional = supportSimpleTraditional;
            return this;
        }

        /**
         * 支持半角全角，默认不支持，配置半角只能识别半角
         */
        public Builder setSupportDbc(boolean supportDbc) {
            this.supportDbc = supportDbc;
            return this;
        }

        /**
         * 支持停顿词，默认不支持，字之间使用别的字符隔开不识别
         */
        public Builder setSupportStopWord(boolean supportStopWord) {
            this.supportStopWord = supportStopWord;
            return this;
        }

        /**
         * 设置停顿词
         */
        public Builder setStopWord(String stopWord) {
            this.stopWord = stopWord;
            return this;
        }

        public DFAConfig build() {
            DFAConfig config = new DFAConfig();
            config.setIgnoreCase(this.ignoreCase);
            config.setSupportStopWord(this.supportStopWord);
            config.setStopWord(this.stopWord);
            config.setSupportDbc(this.supportDbc);
            config.setSupportPinyin(this.supportPinyin);
            config.setSupportSimpleTraditional(this.supportSimpleTraditional);
            return config;
        }
    }
}
