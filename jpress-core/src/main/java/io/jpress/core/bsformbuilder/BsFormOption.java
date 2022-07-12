package io.jpress.core.bsformbuilder;

/**
 * bsFormBuilder 的选项内容
 */
public class BsFormOption {

    // 文本内容
    private String text;

    // 值
    private String value;

    public BsFormOption() {
    }

    public BsFormOption(String text, String value) {
        this.text = text;
        this.value = value;
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
}
