package io.jpress.web.commons.controller.html2wxml;

public class Params {
    public static final String TYPE_HTML = "html";
    public static final String TYPE_MD = "md";
    public static final String TYPE_MARKDOWN = "markdown";

    
    private String type;//类型 html markdown md
    private Boolean highlight;//是否开启pre代码高亮
    private Boolean linenums;//是否开启显示pre代码行号
    private String baseUri;//超链接或者图片的根URL

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getHighlight() {
        return highlight;
    }

    public void setHighlight(Boolean highlight) {
        this.highlight = highlight;
    }

    public Boolean getLinenums() {
        return linenums;
    }

    public void setLinenums(Boolean linenums) {
        this.linenums = linenums;
    }

    public String getBaseUri() {
        return baseUri;
    }

    public void setBaseUri(String baseUri) {
        this.baseUri = baseUri;
    }
}
