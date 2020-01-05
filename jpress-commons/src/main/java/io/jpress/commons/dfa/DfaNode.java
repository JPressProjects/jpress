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

import java.util.HashMap;
import java.util.Map;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/1/5
 */
public class DfaNode {
    private final char _char;
    /**
     * 如果为true表示是敏感词的最后一个单词
     */
    private volatile boolean end;
    private volatile Map<Character, DfaNode> childes;

    public DfaNode(char _char) {
        this._char = _char;
    }

    public boolean isEnd() {
        return end;
    }

    public void setEnd(boolean end) {
        this.end = end;
    }

    public boolean isLeaf() {
        Map<Character, DfaNode> childesTemp = childes;
        return (childesTemp == null || childesTemp.isEmpty());
    }

    public char getChar() {
        return _char;
    }

    public synchronized void addChildIfNotPresent(DfaNode child) {
        Map<Character, DfaNode> childesTemp = childes;
        if (childesTemp != null && childesTemp.containsKey(child._char)) {
            return;
        }

        Map<Character, DfaNode> copyOnWriteMap;
        if (childesTemp == null) {
            copyOnWriteMap = new HashMap<>();
        } else {
            copyOnWriteMap = new HashMap<>(childesTemp);
        }

        copyOnWriteMap.put(child.getChar(), child);
        childes = copyOnWriteMap;

    }

    public DfaNode getChild(Character _char){
        Map<Character, DfaNode> childesTemp = childes;
        if (childesTemp == null || childesTemp.isEmpty()) {
            return null;
        }

        return childesTemp.get(_char);
    }

    @Override
    public String toString() {
        return _char +
                "(" + end +
                ") childes=" + childes;
    }

}