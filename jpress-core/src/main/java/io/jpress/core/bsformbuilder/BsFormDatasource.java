package io.jpress.core.bsformbuilder;

/**
 * bsFromBuilder 的数据源定义
 */
public class BsFormDatasource {

    // 数据源名称（显示文本内容）
    private String text;

    // 数据源标识
    private String value;

    //数据源内容
    // 1、可以是一个 String 类型的 URL
    // 2、也可以是一个 List<BsFormOption>
    private Object options;

    public BsFormDatasource() {
    }

    public BsFormDatasource(String text, String value) {
        this.text = text;
        this.value = value;
    }

    public BsFormDatasource(String text, String value, Object options) {
        this.text = text;
        this.value = value;
        this.options = options;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Object getOptions() {
        return options;
    }

    public void setOptions(Object options) {
        this.options = options;
    }
}
