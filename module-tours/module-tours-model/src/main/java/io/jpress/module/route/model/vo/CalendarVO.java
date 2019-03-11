package io.jpress.module.route.model.vo;

/**
 * 日历 VO
 *
 * @author Eric.Huang
 * @date 2019-03-10 11:10
 * @package io.jpress.module.route.model.vo
 **/

public class CalendarVO {

    private String id;
    private String title;
    private Boolean allDay;
    private String start;

    private String end;
    private String url;
    private String className;
    private Boolean editable;

    private String color;
    private String constraint;
    private String borderColor;
    private String textColor;

    private String backgroundColor;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getAllDay() {
        return allDay;
    }

    public void setAllDay(Boolean allDay) {
        this.allDay = allDay;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Boolean getEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getConstraint() {
        return constraint;
    }

    public void setConstraint(String constraint) {
        this.constraint = constraint;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(String borderColor) {
        this.borderColor = borderColor;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    @Override
    public String toString() {
        return "CalendarVO: {" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", allDay=" + allDay +
                ", start='" + start + '\'' +
                ", end='" + end + '\'' +
                ", url='" + url + '\'' +
                ", className='" + className + '\'' +
                ", editable=" + editable +
                ", color='" + color + '\'' +
                ", constraint='" + constraint + '\'' +
                ", backgroundColor='" + backgroundColor + '\'' +
                ", borderColor='" + borderColor + '\'' +
                ", textColor='" + textColor + '\'' +
                '}';
    }
}
