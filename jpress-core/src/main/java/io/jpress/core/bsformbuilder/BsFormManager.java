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
package io.jpress.core.bsformbuilder;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.JFinal;
import com.jfinal.kit.LogKit;
import com.jfinal.render.RenderManager;
import com.jfinal.template.Engine;
import io.jboot.utils.StrUtil;
import io.jpress.core.template.Template;
import io.jpress.core.template.TemplateManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

public class BsFormManager {

    private static String contextPath = JFinal.me().getContextPath();

    private List<BsFormComponent> allComponents = new ArrayList<>();

    private ThreadLocal<Map<String, Object>> TL = new ThreadLocal<>();


    public void addComponent(BsFormComponent component) {
        allComponents.add(component);
    }

    public List<BsFormComponent> getAllComponents() {
        return allComponents;
    }

    public Object getCurrentDataByKey(String key) {
        Map<String, Object> stringObjectMap = TL.get();
        return stringObjectMap == null ? null : stringObjectMap.get(key);
    }


    public String renderAll(JSONArray componentBuilderJsonArray, Map values, boolean withEdit) {
        if (componentBuilderJsonArray == null || componentBuilderJsonArray.isEmpty()) {
            return null;
        }

        componentBuilderJsonArray.sort(Comparator.comparingInt(o -> ((JSONObject) o).getInteger("index")));

        StringBuilder html = new StringBuilder();
        for (int i = 0; i < componentBuilderJsonArray.size(); i++) {
            JSONObject componentData = componentBuilderJsonArray.getJSONObject(i);
            html.append(renderComponentDataToHtml(componentData, values, withEdit));
        }

        return html.toString();
    }


    public String renderComponentDataToHtml(JSONObject componentData, Map values, boolean withEdit) {
        String tag = componentData.getString("tag");
        if (StrUtil.isBlank(tag)) {
            return "";
        }

        //把每个 component 的数据放大 datas 里，这样，在 jfinal 里的指令里
        //可以通过 scope 去获取数据
        Map<String, Object> datas = new HashMap<>();
        datas.put("withEdit", withEdit);
        datas.put("CPATH", JFinal.me().getContextPath());

        for (String key : componentData.keySet()) {
            Object value = componentData.get(key);
            if (value instanceof String && StrUtil.isNumeric((String) value) && ((String) value).length() < 5) {
                value = Integer.parseInt((String) value);
            }
            datas.put(key, value);
        }


        JSONObject children = componentData.getJSONObject("children");
        if (children != null && !children.isEmpty()) {
            Map<Integer, String> htmls = new HashMap<>();
            for (String key : children.keySet()) {
                JSONArray dataArray = children.getJSONArray(key);
                dataArray.sort(Comparator.comparingInt(o -> ((JSONObject) o).getInteger("index")));
                StringBuilder childrenHtml = new StringBuilder();
                for (Object childComponentData : dataArray) {
                    childrenHtml.append(renderComponentDataToHtml((JSONObject) childComponentData, values, withEdit));
                }
                htmls.put(Integer.parseInt(key), childrenHtml.toString());
            }
            datas.put("children", htmls);
        }

        if (values != null && datas.containsKey("field")) {
            //设置 datas 的 value 值
            Object value = values.get(datas.get("field"));

            //复选框
            if (value != null && isArrayValueComponent(datas.get("tag"))) {
                value = value.toString().split(";");
            }

            datas.put("value", value);
        }

        try {

            TL.set(datas);

            BsFormComponent component = getComponentByTag(tag);
            String template = null;

            if (component != null) {
                template = component.template();
            } else {
                LogKit.error("未表单找到组件：" + tag);
                template = "<div class=\"none-component\">没有找到相关组件</div>";
            }

            String htmlResult = renderTemplate(template, datas);
            if (withEdit) {
                htmlResult = appendEditAttrs(htmlResult, componentData.getString("elementId"));
            }

            return htmlResult;

        } finally {
            TL.remove();
        }
    }


    private static boolean isArrayValueComponent(Object tag) {
        return "checkbox".equals(tag) || "image-upload".equals(tag);
    }


    private String renderTemplate(String htmlString, Map<String, Object> datas) {
        try {
            if (!datas.containsKey("options")) {
                JSONObject component = (JSONObject) datas.get("component");
                if (component != null) {
                    Boolean withOptions = component.getBoolean("withOptions");
                    if (withOptions != null && withOptions) {
                        datas.put("options", component.getJSONArray("defaultOptions"));
                    }
                }
            }
            return getEngine().getTemplateByString(htmlString).renderToString(datas);
        } catch (Exception ex) {
            LogKit.error(htmlString);
            LogKit.error(ex.toString(), ex);
            return "<div> 渲染错误 </div>";
        }
    }


    private String appendEditAttrs(String html, String id) {
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


    protected BsFormComponent getComponentByTag(String tag) {
        for (BsFormComponent block : allComponents) {
            if (tag.equals(block.getTag())) {
                return block;
            }
        }
        return null;
    }


    private static Engine engine;

    private Engine getEngine() {
        if (engine == null) {
            engine = RenderManager.me().getEngine();
        }
        return engine;
    }


}
