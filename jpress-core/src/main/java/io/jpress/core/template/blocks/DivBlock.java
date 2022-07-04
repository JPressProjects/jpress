package io.jpress.core.template.blocks;

import io.jpress.core.bsformbuilder.BsFormComponent;
import io.jpress.core.template.HtmlBlock;

public class DivBlock extends HtmlBlock {

    public DivBlock() {
        setId("div");
    }

    @Override
    public String getTemplate() {
        return "<div class=\"#(className ??)\">\n" +
                "    <div class=\"bsItemContainer\">#(children?.get(0) ??)</div>\n" +
                "</div>\n";
    }


    @Override
    public BsFormComponent toBsFormComponent() {
        BsFormComponent component = new BsFormComponent();
        component.setName("DIV");
        component.setTag("div");
        component.setDragType(DRAG_TYPE_LAYOUT);
        component.setDragIcon("bi bi-fullscreen");
        return component;
    }
}
