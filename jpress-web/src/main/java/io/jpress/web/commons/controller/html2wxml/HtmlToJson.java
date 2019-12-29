package io.jpress.web.commons.controller.html2wxml;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.StrKit;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;

import java.util.Collections;
import java.util.List;

/**
 * html转Json 适配html2wxml
 *
 * @author 山东-小木 https://my.oschina.net/imhoodoo
 */
public class HtmlToJson {

    private String html;// 待转Html
    private Params params;
    private int idx;// 图片资源idx html2wxml里存在这个值
    private boolean needAbsUrl = false;

    public static HtmlToJson by(String html, Params params) {
        return new HtmlToJson(html, params);
    }

    private HtmlToJson(String html, Params params) {
        this.html = html;
        this.params = params;
        this.idx = 0;
        this.needAbsUrl = StrKit.notBlank(params.getBaseUri());
        this.clean();
    }


    /**
     * 获取转换后的JSON 数组 有多级子节点
     *
     * @return
     */
    public String get() {
        if (StrKit.isBlank(html)) {
            return null;
        }
        Document document = null;
        try {
            //判断是否需要绝对路径URL
            if (needAbsUrl) {
                document = Jsoup.parseBodyFragment(html, params.getBaseUri());
            } else {
                document = Jsoup.parseBodyFragment(html);
            }
        } finally {
            if (document == null) {
                return null;
            }
        }
        //以前使用Element元素会把无标签包裹的textNode节点给抛弃，这里改为选用Node节点实现
        //Node节点可以将一段html的所有标签和无标签文本区分 都转为Node
        List<Node> nodes = document.body().childNodes();
        //如果获取的Node为空 直接返回Null不处理
        if (nodes.isEmpty()) {
            return null;
        }
        //整个HTML转为JSON其实是转为一个JSON数组传递给前端html2wxml组件模板 循环解析
        JSONArray array = new JSONArray();
        JSONObject jsonObject = null;
        String tag = null;
        for (Node node : nodes) {
            tag = node.nodeName().toLowerCase();
            jsonObject = convertNodeToJsonObject(node, tag, "pre".equals(tag));
            if (jsonObject != null) {
                array.add(jsonObject);
            }
        }
        return array.toJSONString();
    }

    /**
     * 判断是否为TextNode
     *
     * @param tag
     * @return
     */
    public boolean isTextNode(String tag) {
        return "#text".equals(tag);
    }

    /**
     * 判断是否为script tag标签节点
     *
     * @param tag
     * @return
     */
    public boolean isScriptNode(String tag) {
        return "script".equals(tag);
    }

    /**
     * 判断是否为DateNode
     *
     * @param tag
     * @return
     */
    public boolean isDataNode(String tag) {
        return "#data".equals(tag);
    }


    /**
     * 将一个节点元素转为JsonObject
     *
     * @param needClass
     * @return
     */
    private JSONObject convertNodeToJsonObject(Node node, String tag, boolean needClass) {
        /*if(isScriptNode(tag)){
            return null;
		}*/
        if (isTextNode(tag)) {
            return processTextNode(tag, ((TextNode) node).getWholeText());
        }
        if (isDataNode(tag)) {
            return processDataNode(tag, ((DataNode) node).getWholeData());
        }
        //后面用Element操作更方便一点 转一下

        //如果这个元素没有内容 忽略掉
        /*if (elementIsEmpty(element)) {
			return null;
		}*/
        JSONObject eleJsonObj = new JSONObject();
        if (tag.equals("pre")) {
            Element element = (Element) node;
            processTagPre(eleJsonObj, element);
        } else {
            // 1、处理主要的tag type attr
            if (isTextNode(tag) || isDataNode(tag) || isCommentNode(tag)) {
                eleJsonObj.put("tag", "#text");
                eleJsonObj.put("type", "inline");
            } else {
                Element element = (Element) node;
                processMain(element, tag, eleJsonObj);
            }
            // 2、处理Style
            processStyle(node, tag, eleJsonObj);
            if (needClass) {
                processClass(node, tag, eleJsonObj);
            }
            // 3、处理子节点
            processChildNodes(node, tag, eleJsonObj, needClass);
        }

        return eleJsonObj;
    }

    /**
     * 是否是注释
     *
     * @param tag
     * @return
     */
    private boolean isCommentNode(String tag) {
        return "#comment".equals(tag);
    }

    private JSONObject processDataNode(String tag, String data) {
        //暂不实现
        return null;
    }

    private JSONObject processTextNode(String tag, String text) {
        JSONObject jsonObject = new JSONObject();
        String line = System.lineSeparator();
        jsonObject.put("tag", tag);
        jsonObject.put("text", text.replaceAll(line + "|\r|\n", ""));
        return jsonObject;
    }

    /**
     * 判断空元素
     * 条件是 没有文本型内容 不包含图片、视频、音频数据、还没有Style或者class的标签
     *
     * @param element
     * @return
     */
    private boolean elementIsEmpty(Element element) {
        return element.hasText() == false &&
                element.selectFirst("img") == null &&
                element.selectFirst("video") == null &&
                element.selectFirst("audio") == null &&
                element.hasAttr("style") == false &&
                element.hasAttr("class") == false;
    }

    /**
     * 处理主要的tab type attr a和img特殊处理href和src
     *
     * @param element
     * @param tag
     * @param eleJsonObj
     */
    private void processMain(Element element, String tag, JSONObject eleJsonObj) {
        eleJsonObj.put("tag", tag);
        eleJsonObj.put("type", element.isBlock() ? "block" : "inline");
        if (tag.equals("a")) {
            processTagA(eleJsonObj, element);
        } else if (tag.equals("img")) {
            processTagImg(eleJsonObj, element);
        } else if (tag.equals("video") || tag.equals("audio")) {
            processTagVideoOrAudio(eleJsonObj, tag, element);
        }
    }

    /**
     * 处理Video标签或者audio标签
     *
     * @param node
     * @param element
     */
    private void processTagVideoOrAudio(JSONObject node, String tag, Element element) {
        JSONObject attr = new JSONObject();
        String src = element.attr("src");
        if (needAbsUrl) {
            if (src.startsWith("http")) {
                attr.put("src", src);
            } else {
                attr.put("src", element.absUrl("src"));
            }
        } else {
            attr.put("src", src);
        }

        if (element.hasAttr("controls")) {
            attr.put("controls", "controls");
        }
        if (element.hasAttr("autoplay")) {
            attr.put("autoplay", "autoplay");
        }
        if (element.hasAttr("loop")) {
            attr.put("loop", "loop");
        }
        if (element.hasAttr("muted") && tag.equals("video")) {
            attr.put("muted", "muted");
        }
        if (tag.equals("audio")) {
            if (element.hasAttr("name")) {
                attr.put("name", element.attr("name"));
            }
            if (element.hasAttr("author")) {
                attr.put("author", element.attr("author"));
            }
        }

        if (element.hasAttr("poster")) {
            String poster = element.attr("poster");
            if (needAbsUrl) {
                if (src.startsWith("http")) {
                    attr.put("poster", poster);
                } else {
                    attr.put("poster", element.absUrl("poster"));
                }
            } else {
                attr.put("poster", poster);
            }
        }

        node.put("attr", attr);
    }


    /**
     * 处理代码高亮
     *
     * @param node
     * @param element
     */
    private void processTagPre(JSONObject node, Element element) {
        node.put("tag", "pre");
        node.put("type", "block");
        JSONObject attr = new JSONObject();
        attr.put("class", "hljs");
        node.put("attr", attr);

        //使用highlighter库 将代码进行高亮转换
//        final Highlighter highlighter = new Highlighter(new RendererFactory());
//        final Highlighter.HighlightResult result = highlighter.highlightAuto(element.html(), null);
//        final CharSequence styledCode = result.getResult();
//        if (styledCode != null) {
//            element.html(styledCode.toString());
//        }
        // 处理Class
        processClass(element, "pre", node);
        //pre标签里的代码内容 需要创建ol和li去包裹每一行转换后的代码 行号
        JSONObject olNode = new JSONObject();
        olNode.put("tag", "ol");
        olNode.put("type", "block");
        JSONArray olArray = new JSONArray();
        olArray.add(olNode);
        node.put("nodes", olArray);
        //从处理OL开始
        processPreOl(element, olNode);
    }

    /**
     * 处理代码linenumbers
     *
     * @param element
     * @param olNode
     */
    private void processPreOl(Element element, JSONObject olNode) {
        String html = element.html();
        //拿到的代码需要按行分割字符串
        String[] lines = html.split(System.lineSeparator());
        //没有数据就put空list
        if (lines != null && lines.length > 0) {
            int size = lines.length;
            JSONArray nodesArray = new JSONArray();
            JSONObject liJsonObject = null;
            //有数据 就逐行处理代码
            for (int i = 0; i < size; i++) {
                //目的就是一行li就是一个JSONObject 每个object的nodes就是他的子节点
                liJsonObject = processPreLi(lines[i], i);
                if (liJsonObject != null) {
                    nodesArray.add(liJsonObject);
                }
            }
            olNode.put("nodes", nodesArray);
        } else {
            olNode.put("nodes", Collections.emptyList());
        }

    }

    /**
     * 处理每一行代码段
     *
     * @param lineHtml
     * @param i
     * @return
     */
    private JSONObject processPreLi(String lineHtml, int i) {
        JSONObject liNode = new JSONObject();
        liNode.put("tag", "li");
        liNode.put("type", "block");
        liNode.put("idx", i);
        //开始处理nodes 找到子节点解析出来
        JSONArray jsonNodes = new JSONArray();
        //如果一行数据里开头是有一段空格 需要单独处理成空格Text tag
        if (lineHtml.startsWith(" ")) {
            String trimtext = lineHtml.trim();
            int index = lineHtml.indexOf(trimtext);
            String whiteSpaces = lineHtml.substring(0, index);
            lineHtml = lineHtml.substring(index);
            processMutilTextNode(jsonNodes, "#text", whiteSpaces);
        }
        //剩下的数据 按照左侧无空格方式处理生成Nodes
        processLiWithoutLeftWhiteSpace(lineHtml, liNode, jsonNodes);
        //最终返回一个包装好的liNode
        return liNode;


    }

    /**
     * 剩下的数据 按照左侧无空格方式处理生成Nodes
     *
     * @param lineHtml
     * @param liNode
     * @param jsonNodes
     */
    private void processLiWithoutLeftWhiteSpace(String lineHtml, JSONObject liNode, JSONArray jsonNodes) {
        List<Node> nodes = Jsoup.parse(lineHtml).selectFirst("body").childNodes();
        if (nodes.isEmpty()) {
            liNode.put("nodes", Collections.emptyList());
        } else {
            JSONObject jsonObject = null;
            for (Node node : nodes) {
                String tag = node.nodeName();
                if (isTextNode(tag)) {
                    processMutilTextNode(jsonNodes, tag, ((TextNode) node).getWholeText());
                } else {
                    if (isNotNullSpan(node)) {
                        jsonObject = convertNodeToJsonObject(node, node.nodeName(), true);
                        if (jsonObject != null) {
                            jsonNodes.add(jsonObject);
                        }
                    }
                }

            }
            liNode.put("nodes", jsonNodes);
        }
    }

    private boolean isNotNullSpan(Node node) {
        return !(node.hasAttr("class") && node.attr("class").equals("null"));
    }

    /**
     * 处理textNode类型的节点 里面可能带着左侧空格的 都需要把空格转为空格节点
     *
     * @param jsonNodes
     * @param tag
     * @param wholeText
     */
    private void processMutilTextNode(JSONArray jsonNodes, String tag, String wholeText) {
        if (wholeText.startsWith(" ") && wholeText.length() > 1 && StrKit.notBlank(wholeText)) {
            String trimText = wholeText.trim();
            int index = wholeText.indexOf(trimText);
            jsonNodes.add(processTextNode(tag, wholeText.substring(0, index)));
            jsonNodes.add(processTextNode(tag, wholeText.substring(index)));
        } else {
            jsonNodes.add(processTextNode(tag, wholeText));
        }
    }


    /**
     * 处理style属性
     *
     * @param tag
     * @param eleJsonObj
     */
    private void processStyle(Node node, String tag, JSONObject eleJsonObj) {
        if (node.hasAttr("style")) {
            String style = node.attr("style");
            if (StrKit.notBlank(style)) {
                JSONObject attr = eleJsonObj.getJSONObject("attr");
                if (attr == null) {
                    attr = new JSONObject();
                    eleJsonObj.put("attr", attr);
                }
                attr.put("style", style);
            }
        }
    }

    /**
     * 处理class属性
     *
     * @param node
     * @param tag
     * @param eleJsonObj
     */
    private void processClass(Node node, String tag, JSONObject eleJsonObj) {
        if (node.hasAttr("class")) {
            String style = node.attr("class");
            if (StrKit.notBlank(style)) {
                JSONObject attr = eleJsonObj.getJSONObject("class");
                if (attr == null) {
                    attr = new JSONObject();
                    eleJsonObj.put("attr", attr);
                }
                attr.put("class", style);
            }
        }
    }

    /**
     * 处理子节点
     *
     * @param tag
     * @param eleJsonObj
     * @param needClass
     */
    private void processChildNodes(Node node, String tag, JSONObject eleJsonObj, boolean needClass) {
        if (tag.toLowerCase().equals("img")) {
            return;
        }
        List<Node> sonNodes = node.childNodes();
        JSONArray nodes = new JSONArray();
        if (sonNodes.isEmpty() == false) {
            JSONObject soneleJsonObj = null;
            for (Node son : sonNodes) {
                soneleJsonObj = convertNodeToJsonObject(son, son.nodeName(), needClass);
                if (soneleJsonObj != null) {
                    nodes.add(soneleJsonObj);
                }

            }
        }
        eleJsonObj.put("nodes", nodes);
    }

    /**
     * 处理超链接
     *
     * @param element
     */
    private void processTagA(JSONObject node, Element element) {
        JSONObject attr = new JSONObject();
        String href = element.attr("href");
        if (needAbsUrl) {
            if (href.startsWith("http")) {
                attr.put("href", href);
            } else {
                attr.put("href", element.absUrl("href"));
            }
        } else {
            attr.put("href", href);
        }
        node.put("attr", attr);
    }

    /**
     * 处理图片
     *
     * @param element
     */
    private void processTagImg(JSONObject node, Element element) {
        JSONObject attr = new JSONObject();
        String href = element.attr("src");
        if (needAbsUrl) {
            if (href.startsWith("http")) {
                attr.put("src", href);
            } else {
                attr.put("src", element.absUrl("src"));
            }
        } else {
            attr.put("src", href);
        }
        node.put("attr", attr);
        node.put("idx", idx);
        idx++;
    }

    /**
     * 处理不安全html返回安全html
     *
     * @return
     */
    private void clean() {
        //TODO 这里可以在执行转换前过滤数据
    }

}
