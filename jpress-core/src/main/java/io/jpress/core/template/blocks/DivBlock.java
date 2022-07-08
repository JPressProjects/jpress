package io.jpress.core.template.blocks;

import io.jpress.core.template.HtmlBlock;

public class DivBlock extends HtmlBlock {

    public DivBlock() {
        setId("div");

        setTemplate("<div class=\"#blockOption('Class样式')\" style=\"#blockOption('Style样式:textarea')\">\n" +
                "    <div class=\"bsItemContainer\">#(children?.get(0) ??)</div>\n" +
                "</div>");

        setName("DIV");

        setType(DRAG_TYPE_LAYOUT);
        setIcon("bi bi-fullscreen");
    }

}
