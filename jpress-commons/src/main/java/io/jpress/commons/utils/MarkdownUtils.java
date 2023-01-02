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
package io.jpress.commons.utils;

import io.jboot.utils.StrUtil;
import org.commonmark.Extension;
import org.commonmark.ext.front.matter.YamlFrontMatterExtension;
import org.commonmark.ext.front.matter.YamlFrontMatterVisitor;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @author Ryan Wang（i@ryanc.cc）
 * @version V1.1
 * @Title: Markdown 工具类
 * @Package io.jpress.commons.utils
 */
public class MarkdownUtils {

    /**
     * Front-matter插件
     */
    private static final Set<Extension> EXTENSIONS_YAML = Collections.singleton(YamlFrontMatterExtension.create());

    /**
     * Table插件
     */
    private static final Set<Extension> EXTENSIONS_TABLE = Collections.singleton(TablesExtension.create());

    /**
     * 解析Markdown文档
     */
    private static final Parser PARSER = Parser.builder()
            .extensions(EXTENSIONS_YAML)
            .extensions(EXTENSIONS_TABLE)
            .build();

    /**
     * 渲染HTML文档
     */
    private static final HtmlRenderer RENDERER = HtmlRenderer.builder()
//            .nodeRendererFactory(context -> new IndentedCodeBlockNodeRenderer(context))
            .extensions(EXTENSIONS_YAML)
            .extensions(EXTENSIONS_TABLE)
            .build();


    /**
     * 渲染html
     *
     * @param markdown markdown格式文本
     * @return html文本
     */
    public static String toHtml(String markdown) {
        if (StrUtil.isBlank(markdown)) {
            return markdown;
        }
        Node document = PARSER.parse(markdown);
        return RENDERER.render(document);
    }

    /**
     * 获取元数据
     *
     * @param content content
     * @return Map
     */
    public static Map<String, List<String>> getFrontMatter(String content) {
        YamlFrontMatterVisitor visitor = new YamlFrontMatterVisitor();
        Node document = PARSER.parse(content);
        document.accept(visitor);
        return visitor.getData();
    }


    public static void main(String[] args) {

        String markdown = "This is *Sparta*  : `aaa` \n\n```\naaa\n```\n\n";

        long ctime = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            toHtml(markdown);
        }
        System.out.println("100000 times : " + (System.currentTimeMillis() - ctime));
        System.out.println(toHtml(markdown));
    }
}
