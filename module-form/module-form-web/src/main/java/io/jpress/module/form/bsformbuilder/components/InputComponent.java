package io.jpress.module.form.bsformbuilder.components;

import io.jpress.core.bsformbuilder.BsFormComponent;

public class InputComponent extends BsFormComponent {

    public InputComponent() {
        setTag("input");
        setName("输入框");

        setTemplate("<div class=\"bsFormItem\">\n" +
                "  <div class=\"form-group clearfix\">\n" +
                "    <div class=\"form-label-left\">\n" +
                "      <label for=\"label\">#(label ??)</label>\n" +
                "    </div>\n" +
                "    <div class=\"flex-auto\">\n" +
                "      <input\n" +
                "        type=\"text\"\n" +
                "        class=\"form-control\"\n" +
                "        id=\"#(id ??)\"\n" +
                "        placeholder=\"#(placeholder ??)\"\n" +
                "        value=\"#(value ??)\"\n" +
                "      />\n" +
                "    </div>\n" +
                "  </div>\n" +
                "</div>");
    }
}
