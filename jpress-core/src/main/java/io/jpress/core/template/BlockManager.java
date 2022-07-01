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
import io.jpress.core.template.blocks.ContainerBlockHtml;
import io.jpress.core.template.bsformbuilder.BsFormComponent;
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
        initSystemBlockHtmls();
    }

    private void initSystemBlockHtmls() {
        systemBlockHtmls.add(new ContainerBlockHtml());
    }


    public static BlockManager me() {
        return me;
    }

    private ThreadLocal<Map<String, Object>> TL = new ThreadLocal<>();


    public List<BsFormComponent> getBsFromComponents() {

        List<BlockHtml> allBlocks = new ArrayList<>();

        Template currentTemplate = TemplateManager.me().getCurrentTemplate();
        List<BlockHtml> templateBlockHtmls = currentTemplate.getBlockHtmls();

        if (templateBlockHtmls != null && !templateBlockHtmls.isEmpty()) {
            allBlocks.addAll(templateBlockHtmls);
        }

        allBlocks.addAll(systemBlockHtmls);


        List<BsFormComponent> components = new ArrayList<>();
        for (BlockHtml blockHtml : allBlocks) {
            components.add(blockHtml.toBsFormComponent());
        }

        return components;
    }



    /**
     * 渲染一个 block 容器（container）
     *
     * @param containerDatas 容器里的数据
     * @param withEdit
     * @return
     */
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

        //把每个 component 的数据放大 datas 里，这样，在 jfinal 里的指令里
        //可以通过 scope 去获取数据
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

            String htmlString = getBlockHtmlString(tag);
            String htmlResult = getEngine().getTemplateByString(htmlString).renderToString(datas);
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


    public void buildNormalHtml(Document doc) {

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


    private String getBlockHtmlString(String tag) {
        for (BlockHtml block : systemBlockHtmls) {
            if (tag.equals(block.getId())) {
                return block.getTemplate();
            }
        }

        Template currentTemplate = TemplateManager.me().getCurrentTemplate();
        List<BlockHtml> templateBlockHtmls = currentTemplate.getBlockHtmls();
        for (BlockHtml block : templateBlockHtmls) {
            if (tag.equals(block.getId())) {
                return block.getTemplate();
            }
        }

        return "<div>暂无内容</div>";
    }


    private static Engine engine;

    private Engine getEngine() {
        if (engine == null) {
            engine = RenderManager.me().getEngine();
        }
        return engine;
    }


}
