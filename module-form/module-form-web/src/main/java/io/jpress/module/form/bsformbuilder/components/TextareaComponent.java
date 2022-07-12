package io.jpress.module.form.bsformbuilder.components;

import io.jpress.core.bsformbuilder.BsFormComponent;

public class TextareaComponent extends BsFormComponent {

    public TextareaComponent() {
        setTag("textarea");
        setName("多行输入框");

        setTemplate("<div class=\"bsFormItem\">\n" +
                "  <div class=\"form-group clearfix\">\n" +
                "    <div class=\"form-label-left\">\n" +
                "      <label for=\"#(id ??)\">#(label ??)</label>\n" +
                "    </div>\n" +
                "    <div class=\"flex-auto\">\n" +
                "      <textarea\n" +
                "        name=\"#(name??)\"\n" +
                "        class=\"form-control\"\n" +
                "        id=\"#(id ??)\"\n" +
                "        rows=\"#(rows ??3)\"\n" +
                "        placeholder=\"#(placeholder ??)\"\n" +
                "      >#(value ??)</textarea\n" +
                "      >\n" +
                "    </div>\n" +
                "  </div>\n" +
                "</div>\n");
    }
}
