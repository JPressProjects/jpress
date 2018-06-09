package io.jpress.admin;

import io.jboot.utils.StringUtils;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.admin
 */
public class LeftMenuItem {


    public static final String TYPE_GROUP = "_group";
    public static final String TYPE_ITEM = "_item";
    public static final String TYPE_BLOCK = "_block";

    private String id;
    private String url;
    private String text;
    private String iconClass;
    private String type = TYPE_GROUP;

    public LeftMenuItem(String text) {
        this.id = StringUtils.uuid();

        text = text.trim();
        if ("".equals(text)) {
            this.type = TYPE_BLOCK;
        } else if (text.startsWith("-")) {
            this.type = TYPE_ITEM;
            String[] infos = text.split(":");
            this.text = safeGetArrayIndex(0, infos);
            this.url = safeGetArrayIndex(1, infos);
        } else {
            this.type = TYPE_GROUP;
            String[] infos = text.split(":");
            this.text = safeGetArrayIndex(0, infos);
            this.iconClass = safeGetArrayIndex(1, infos);
        }
    }

    private String safeGetArrayIndex(int index, String[] arrays) {
        if (arrays == null || arrays.length == 0) {
            return null;
        }

        if (index >= arrays.length) {
            return null;
        }

        return arrays[index];
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getIconClass() {
        return iconClass;
    }

    public void setIconClass(String iconClass) {
        this.iconClass = iconClass;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    @Override
    public String toString() {
        return "LeftMenuItem{" +
                "id='" + id + '\'' +
                ", url='" + url + '\'' +
                ", text='" + text + '\'' +
                ", iconClass='" + iconClass + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
