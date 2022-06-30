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
import com.jfinal.render.RenderManager;
import com.jfinal.template.Engine;
import io.jboot.utils.JsonUtil;
import io.jboot.utils.StrUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockManager {


    //系统自带的 html 模块
    private List<BlockHtml> systemBlockHtmls = new ArrayList<>();

    private static final BlockManager me = new BlockManager();

    private BlockManager() {
    }


    public static BlockManager me() {
        return me;
    }


    public String renderBlockContainer(JSONArray containerDatas, boolean withEdit) {
        if (containerDatas == null || containerDatas.isEmpty()){
            return null;
        }

        StringBuilder html = new StringBuilder();
        for (int i = 0; i < containerDatas.size(); i++) {
            JSONObject componentData = containerDatas.getJSONObject(i);
            html.append(renderComponentDataToHtml(componentData,withEdit));
        }

        return null;
    }



    public String renderComponentDataToHtml(JSONObject componentData, boolean withEdit) {
        String tag = JsonUtil.getString(componentData, "component.tag", "");
        if (StrUtil.isBlank(tag)) {
            return "";
        }

        String htmlText = getTagHtml(tag);

        Map<Object, Object> datas = new HashMap<>();
        datas.putAll(componentData);

        JSONObject children = componentData.getJSONObject("children");
        if (children != null && !children.isEmpty()) {
            for (String key : children.keySet()) {
                JSONArray dataArray = children.getJSONArray(key);
                StringBuilder childrenHtml = new StringBuilder();
                for (Object childComponentData : dataArray) {
                    childrenHtml.append(renderComponentDataToHtml((JSONObject) childComponentData, withEdit));
                }
                datas.put("children" + key, childrenHtml.toString());
            }
        }


        String htmlResult = getEngine().getTemplateByString(htmlText).renderToString(datas);
        if (withEdit) {
            htmlResult = appendIdAndBsFormItemClass(htmlResult, componentData.getString("elementId"));
        }
        return htmlResult;
    }


    private String appendIdAndBsFormItemClass(String html, String id) {
        if (StrUtil.isBlank(html)) {
            //在某些情况下，比如 block 片段是以 #if()... 开头，可能渲染整个html 得到的结果为空
            html = "<div></div>";
        }
        Document fragment = Jsoup.parseBodyFragment(html);
        Element body = fragment.body();
        Element htmlElement = body.child(0);
        htmlElement.attr("id", id);
        htmlElement.addClass("bsFormItem");
        return htmlElement.toString();
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
