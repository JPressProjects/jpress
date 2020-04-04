/**
 * Copyright (c) 2016-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jpress.commons.dfa;

import com.jfinal.kit.LogKit;
import com.jfinal.kit.PathKit;
import io.jboot.utils.StrUtil;
import io.jpress.JPressOptions;
import io.jpress.commons.dfa.algorithm.DFAConfig;
import io.jpress.commons.dfa.algorithm.DFAFilter;
import io.jpress.commons.dfa.algorithm.DFAMatch;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/1/5
 * <p>
 * https://github.com/aijingsun6/sensitive-words-filter
 */
public class DFAUtil {

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
        initBy(sysSensitiveWordsFile);
    }

    public static void initBy(File sensitiveWordsFile) {
        try {
            List<String> lines = FileUtils.readLines(sensitiveWordsFile, "utf-8");
            for (String line : lines) {
                if (line.startsWith("--") || StrUtil.isBlank(line)) {
                    continue;
                }
                sysFilter.putWord(line.trim(), 1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static boolean isContainsSensitiveWords(String content) {

        String filterContent = JPressOptions.get("text_filter_content");

        if (StrUtil.isNotBlank(filterContent)) {
            if (!Objects.equals(dynamicFilterTexts, filterContent)) {
                if (isContainsSensitiveWords(dynamicFilter, content)) {
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
                if (isContainsSensitiveWords(dynamicFilter, content)) {
                    return true;
                }
            }
        }

        return isContainsSensitiveWords(sysFilter, content);
    }

    private static boolean isContainsSensitiveWords(DFAFilter filter, String content) {
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
        File file = new File(PathKit.getRootClassPath(),"../../../jpress-web/src/main/webapp/WEB-INF/other/sys_sensitive_words.txt");
        initBy(file);
        System.out.println(isContainsSensitiveWords(text));
    }


}
