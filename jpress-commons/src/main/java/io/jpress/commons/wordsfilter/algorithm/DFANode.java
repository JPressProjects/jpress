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

import java.util.HashMap;
import java.util.Map;


public class DFANode {

    protected final char ch;

    protected boolean leaf;

    protected String word = null;

    protected Comparable ext;

    protected DFANode parent;

    /**
     * chat -> node
     */
    private Map<Character, DFANode> cMap = new HashMap<>(0);

    /**
     * string -> node
     * 用于拼音
     */
    private Map<String, DFANode> sMap = new HashMap<>(0);

    public Map<Character, DFANode> getcMap() {
        return cMap;
    }

    public Map<String, DFANode> getsMap() {
        return sMap;
    }

    public DFANode getNode(char c) {
        return this.cMap.get(c);
    }

    public DFANode getNode(String s) {
        return this.sMap.get(s);
    }

    public void putNode(char c, DFANode node) {
        this.cMap.put(c, node);
        node.parent = this;
    }

    public void putNode(String s, DFANode node) {
        this.sMap.put(s, node);
        node.parent = this;
    }

    public DFANode(char ch, boolean leaf) {
        this.ch = ch;
        this.leaf = leaf;
    }
}
