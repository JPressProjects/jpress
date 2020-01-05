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

import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/1/5
 */
public class DfaFilter  {

    private final DfaNode rootNode = new DfaNode(' ');


    /**
     * 匹配到敏感词的回调接口
     */
    protected interface Callback {

        /**
         * 匹配掉敏感词回调
         * @param word 敏感词
         * @return true 立即停止后续任务并返回，false 继续执行
         */
        boolean call(String word);
    }


    public boolean contains(String content) {
        return processor(true, content, word -> {
            return true; // 有敏感词立即返回
        });
    }

    /**
     * 返回第一个敏感词
     * @param content
     * @return
     */
    public String getWord(String content) {
        final AtomicReference<String> ref = new AtomicReference<>();

        processor(true, content, word -> {
            ref.set(word);
            return true; // 匹配到任意一个敏感词后停止继续匹配
        });

        return ref.get();
    }

    /**
     * 返回所有的敏感词
     * @param partMatch
     * @param content
     * @return
     */
    public Set<String> getWords(boolean partMatch, String content) {
        final Set<String> words = new HashSet<>();

        processor(partMatch, content, word -> {
            words.add(word);
            return false; // 继续匹配后面的敏感词
        });

        return words;
    }


    /**
     * 对敏感词进行过滤
     * @param content
     * @param replaceChar
     * @return
     */
    public String filter(String content, char replaceChar) {
        Set<String> words = this.getWords(true, content);

        Iterator<String> iter = words.iterator();
        while (iter.hasNext()) {
            String word = iter.next();
            content = content.replaceAll(word, repeatAppend(String.valueOf(replaceChar), word.length()));
        }

        return content;
    }

    private String repeatAppend(String str, int repeatNums){
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<repeatNums;i++) {
            sb.append(str);
        }

        return sb.toString();
    }

    public boolean put(String word) {
        if (StringUtils.isBlank(word)) {
            return false;
        }

        word = StringUtils.trim(word);

        DfaNode node = rootNode;
        for (int i = 0; i < word.length(); i++) {
            Character nextChar = word.charAt(i);

            DfaNode nextNode = node.getChild(nextChar);
            if (nextNode == null) {
                nextNode = new DfaNode(nextChar);
            }

            if (i == word.length() - 1) {
                nextNode.setEnd(true);
            }

            node.addChildIfNotPresent(nextNode);
            node = nextNode;
        }

        return true;
    }

    /**
     * 判断一段文字包含敏感词语，支持敏感词结果回调
     * @param partMatch 是否部分匹配; 比如content为"中国民",敏感词库中有两个敏感词:"中国","国民",则如果partMatch=true，
     *                  ：["中国"], 反之匹配到:["中国"，"国民"],也就是说partMatch=false会匹配到重叠的部分
     * @param content 被匹配内容
     * @param callback 回调接口
     * @return 是否匹配到的词语
     */
    protected boolean processor(boolean partMatch, String content, Callback callback) {
        if (StringUtils.isBlank(content)) {
            return false;
        }

        content = StringUtils.trim(content);

        for (int index = 0; index < content.length();index++) {
            char firstChar = content.charAt(index);

            DfaNode node = rootNode.getChild(firstChar);
            if (node == null) {
                continue;
            }

            if (node.isEnd()) {
                if (callback.call(new String(new char[]{firstChar}))) {
                    return true;
                }
                if(partMatch) {
                    continue;
                }
            }

            int charCount = 1;
            boolean found = false;
            for (int i = index + 1; i < content.length(); i++) {
                char wordChar = content.charAt(i);

                node = node.getChild(wordChar);
                if (node != null) {
                    charCount++;
                } else {
                    break;
                }

                if (partMatch && node.isEnd()) {
                    found = true;
                    if (callback.call(StringUtils.substring(content, index, index + charCount))) {
                        return true;
                    }
                    break;
                } else if (node.isEnd()) {
                    found = true;
                    if (callback.call(StringUtils.substring(content, index, index + charCount))) {
                        return true;
                    }
                }

                if (node.isLeaf()) {
                    break;
                }
            }

            if (partMatch && found) {
                index += (charCount - 1);//最后要i++，所以这里要-1
            }
        }

        return false;
    }

}
