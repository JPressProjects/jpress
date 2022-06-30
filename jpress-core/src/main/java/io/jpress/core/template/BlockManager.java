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
package io.jpress.core.template;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.JFinal;
import com.jfinal.render.RenderManager;
import com.jfinal.template.Engine;
import io.jboot.utils.StrUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

public class BlockManager {

    private static String contextPath = JFinal.me().getContextPath();

    //系统自带的 html 模块
    private List<BlockHtml> systemBlockHtmls = new ArrayList<>();

    private static final BlockManager me = new BlockManager();

    private BlockManager() {
    }


    public static BlockManager me() {
        return me;
    }

    private ThreadLocal<Map<String, Object>> TL = new ThreadLocal<>();


    public String renderBlockContainer(JSONArray containerDatas, boolean withEdit) {
        if (containerDatas == null || containerDatas.isEmpty()) {
            return null;
        }

        containerDatas.sort(Comparator.comparingInt(o -> ((JSONObject) o).getInteger("index")));

        StringBuilder html = new StringBuilder();
        for (int i = 0; i < containerDatas.size(); i++) {
            JSONObject componentData = containerDatas.getJSONObject(i);
            html.append(renderComponentDataToHtml(componentData, withEdit));
        }

        return html.toString();
    }


    public Object getCurrentDataByKey(String key) {
        Map<String, Object> stringObjectMap = TL.get();
        return stringObjectMap == null ? null : stringObjectMap.get(key);
    }


    public String renderComponentDataToHtml(JSONObject componentData, boolean withEdit) {
        String tag = componentData.getString("tag");
        if (StrUtil.isBlank(tag)) {
            return "";
        }

        String htmlText = getTagHtml(tag);

        Map<String, Object> datas = new HashMap<>(componentData);

        JSONObject children = componentData.getJSONObject("children");
        if (children != null && !children.isEmpty()) {
            for (String key : children.keySet()) {
                JSONArray dataArray = children.getJSONArray(key);
                dataArray.sort(Comparator.comparingInt(o -> ((JSONObject) o).getInteger("index")));
                StringBuilder childrenHtml = new StringBuilder();
                for (Object childComponentData : dataArray) {
                    childrenHtml.append(renderComponentDataToHtml((JSONObject) childComponentData, withEdit));
                }
                datas.put("children" + key, childrenHtml.toString());
            }
        }

        try {
            TL.set(datas);

            String htmlResult = getEngine().getTemplateByString(htmlText).renderToString(datas);
            if (withEdit) {
                htmlResult = appendIdAndBsFormItemClass(htmlResult, componentData.getString("elementId"));
            }
            return htmlResult;

        } finally {
            TL.remove();
        }
    }


    private String appendIdAndBsFormItemClass(String html, String id) {
        if (StrUtil.isBlank(html)) {
            //在某些情况下，比如 block 片段是以 #if()... 开头，可能渲染整个html 得到的结果为空
            html = "<div></div>";
        }
        Document fragment = Jsoup.parseBodyFragment(html);
        buildNormalHtml(fragment);
        Element body = fragment.body();
        Element htmlElement = body.child(0);
        htmlElement.attr("id", id);
        htmlElement.addClass("bsFormItem");
        return htmlElement.toString();
    }





    public void buildNormalHtml(Document doc)  {

        Elements jsElements = doc.select("script");
        replace(jsElements, "src");

        Elements imgElements = doc.select("img");
        replace(imgElements, "src");

        Elements sourceElements = doc.select("source");
        replace(sourceElements, "src");

        Elements linkElements = doc.select("link");
        replace(linkElements, "href");
    }

    private void replace(Elements elements, String attrName) {
         Template currentTemplate = TemplateManager.me().getCurrentTemplate();

        Iterator<Element> iterator = elements.iterator();
        while (iterator.hasNext()) {

            Element element = iterator.next();
            String url = element.attr(attrName);

            if (StrUtil.isBlank(url)
                    || url.startsWith("//")
                    || url.toLowerCase().startsWith("http")
                    || (attrName.equals("src") && url.startsWith("data:image/"))
                    || element.hasAttr("cdn-exclude")) {
                continue;
            }

            // 以 / 开头的，需要添加 contextPath
            if (url.startsWith("/")) {
                if (contextPath.length() > 0 && url.startsWith(contextPath + "/") == false) {
                    url = contextPath + url;
                }
            }

            // 以 ./ 开头的文件，需要添加模板路径
            else if (url.startsWith("./")) {
                url = contextPath + currentTemplate.getRelativePath() + url.substring(1);
            }

            // 直接是文件目录名开头
            else {
                url = contextPath + currentTemplate.getRelativePath() + "/" + url;
            }

            element.attr(attrName, url);
        }
    }


    private String getTagHtml(String tag) {
        switch (tag) {
            case "block":
                return "<div class=\"m-3 pt-2 bsFormFilter\">\n" +
                        "  <div class=\"card\">\n" +
                        "    <div class=\"card-header\">\n" +
                        "      <h3 class=\"card-title\">#(id ??)</h3>\n" +
                        "      <div class=\"card-tools\">\n" +
                        "        <button type=\"button\" class=\"btn btn-tool\" data-card-widget=\"collapse\">\n" +
                        "          <i class=\"fas fa-plus\"></i>\n" +
                        "        </button>\n" +
                        "      </div>\n" +
                        "    </div>\n" +
                        "    <div class=\"card-body bsItemContainer\">#(children0 ??)</div>\n" +
                        "  </div>\n" +
                        "</div>\n";

            default:
                Template currentTemplate = TemplateManager.me().getCurrentTemplate();
                List<BlockHtml> blockHtmls = currentTemplate.getBlockHtmls();
                for (BlockHtml blockHtml : blockHtmls) {
                    if (blockHtml.getId().equals(tag)) {
                        return blockHtml.getTemplate();
                    }
                }
                return "<div>暂无内容</div>";
        }
    }


    private static Engine engine;

    private Engine getEngine() {
        if (engine == null) {
            engine = RenderManager.me().getEngine();
        }
        return engine;
    }


}
