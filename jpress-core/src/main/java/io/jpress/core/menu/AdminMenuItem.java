package io.jpress.core.menu;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: JPress 的 module
 * @Package io.jpress.module
 */
public class AdminMenuItem {

    private String text;
    private String icon;
    private String groupId;
    private String url;
    private int order = 100;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }


    @Override
    public String toString() {
        return "AdminMenuItem{" +
                "text='" + text + '\'' +
                ", icon='" + icon + '\'' +
                ", groupId='" + groupId + '\'' +
                ", url='" + url + '\'' +
                ", order=" + order +
                '}';
    }
}
