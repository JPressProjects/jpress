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

import com.jfinal.kit.PathKit;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/1/5
 */
public class DfaUtil {

    private static DfaFilter filter = new DfaFilter();
    private static String dynamicFilterTexts;

    static {
        File sysSensitiveWordsFile = new File(PathKit.getWebRootPath(),"WEB-INF/other/sys_sensitive_words.txt");
        try {
            List<String> lines = FileUtils.readLines(sysSensitiveWordsFile,"utf-8");
            for (String line : lines){
                if (line.startsWith("--")){
                    continue;
                }
                filter.put(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static boolean isContainsSensitiveWords(String content){
//       if (StrUtil.isNotBlank())


        return true;
    }








}
