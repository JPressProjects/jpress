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
package io.jpress.commons.wordsfilter;

import com.jfinal.kit.Base64Kit;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.PathKit;
import io.jboot.codegen.CodeGenHelpler;
import io.jboot.utils.FileUtil;
import io.jboot.utils.StrUtil;
import io.jpress.JPressOptions;
import io.jpress.commons.wordsfilter.algorithm.DFAConfig;
import io.jpress.commons.wordsfilter.algorithm.DFAFilter;
import io.jpress.commons.wordsfilter.algorithm.DFAMatch;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/1/5
 * <p>
 * https://github.com/aijingsun6/sensitive-words-filter
 */
public class WordFilterUtil {

    private static DFAConfig config = new DFAConfig.Builder()
            .setIgnoreCase(true)
            .setSupportPinyin(true)
            .setSupportStopWord(true)
            .setSupportDbc(true)
            .setSupportSimpleTraditional(true)
            .setStopWord("、,.。￥$%*&!@#-|[]【】")
            .build();

    private static DFAFilter sysFilter = new DFAFilter(config);

    private static String dynamicFilterTexts;
    private static DFAFilter dynamicFilter;

    /**
     * 需要在 App 启动的时候调用 init 进行初始化，方可使用
     */
    public static void init() {
        File sysSensitiveWordsFile = new File(PathKit.getWebRootPath(), "WEB-INF/other/sys_sensitive_words.txt");
        if (sysSensitiveWordsFile.exists()) {
            initFromWordsFile(sysSensitiveWordsFile);
        }
    }

    public static void initFromWordsFile(File sensitiveWordsFile) {
        String filterWordsBase64 = FileUtil.readString(sensitiveWordsFile);
        String[] lines = Base64Kit.decodeToStr(filterWordsBase64).split("\n");
        for (String line : lines) {
            if (line.startsWith("--") || StrUtil.isBlank(line)) {
                continue;
            }
            sysFilter.putWord(line.trim(), 1);
        }
    }


    public static boolean isMatchedFilterWords(String content) {

        String filterContent = JPressOptions.get("text_filter_content");

        if (StrUtil.isNotBlank(filterContent)) {
            if (Objects.equals(dynamicFilterTexts, filterContent)) {
                if (isMatchedFilterWords(dynamicFilter, content)) {
                    return true;
                }
            } else {
                dynamicFilterTexts = filterContent;
                DFAConfig config = new DFAConfig.Builder()
                        .setIgnoreCase(true)
                        .setSupportPinyin(true)
                        .setSupportStopWord(true)
                        .setSupportDbc(true)
                        .setSupportSimpleTraditional(true)
                        .setStopWord("、,.。￥$%*&!@#-|[]【】")
                        .build();
                dynamicFilter = new DFAFilter(config);
                Set<String> filterTexts = StrUtil.splitToSet(dynamicFilterTexts, ",");
                for (String keyword : filterTexts) {
                    if (StrUtil.isNotBlank(keyword)) {
                        dynamicFilter.putWord(keyword, 1);
                    }
                }
                if (isMatchedFilterWords(dynamicFilter, content)) {
                    return true;
                }
            }
        }

        return isMatchedFilterWords(sysFilter, content);
    }

    private static boolean isMatchedFilterWords(DFAFilter filter, String content) {
        if (filter == null || StrUtil.isBlank(content)) {
            return false;
        }
        List<DFAMatch> matches = filter.matchWord(content);
        printDFAMatches(matches);
        return matches != null && matches.size() > 0;
    }


    private static void printDFAMatches(List<DFAMatch> matches) {
        if (matches == null || matches.isEmpty()) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (DFAMatch match : matches) {
            if (match != null) {
                sb.append(match.getWord());
            }
            if (i < matches.size() - 1) {
                sb.append(",");
            }
            i++;
        }
        LogKit.error("Matched Sensitive Words : " + sb.toString());
    }


    public static void main(String[] args) {
        String text = "";
        File file = new File(CodeGenHelpler.getUserDir(),"/jpress-web/src/main/webapp/WEB-INF/other/sys_sensitive_words.txt");
        initFromWordsFile(file);
        System.out.println(isMatchedFilterWords(text));
    }


}
