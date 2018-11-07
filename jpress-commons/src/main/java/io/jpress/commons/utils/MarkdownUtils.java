/**
 * Copyright (c) 2016-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jpress.commons.utils;

import io.jboot.utils.StrUtils;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: Markdown 工具类
 * @Package io.jpress.commons.utils
 */
public class MarkdownUtils {

    public static String toHtml(String markdown) {
        if (StrUtils.isBlank(markdown)) return markdown;
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }

    public static void main(String[] args) {
        long ctime = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            toHtml("This is *Sparta*");
        }
        System.out.println("100000 times : " + (System.currentTimeMillis() - ctime));
        System.out.println(toHtml("This is *Sparta*"));
    }
}
