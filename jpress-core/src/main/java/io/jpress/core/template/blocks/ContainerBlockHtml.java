package io.jpress.core.template.blocks;

import io.jpress.core.template.BlockHtml;
import io.jpress.core.bsformbuilder.BsFormComponent;

public class ContainerBlockHtml extends BlockHtml {

    public ContainerBlockHtml() {
        setId("block");
    }

    @Override
    public String getTemplate() {
        return "<div class=\"m-3 pt-2 bsFormFilter\">\n" +
                "  <div class=\"card\">\n" +
                "    <div class=\"card-header\">\n" +
                "      <h3 class=\"card-title\">#(id ??)</h3>\n" +
                "      <div class=\"card-tools\">\n" +
                "        <button type=\"button\" class=\"btn btn-tool\" data-card-widget=\"collapse\">\n" +
                "          <i class=\"fas fa-plus\"></i>\n" +
                "        </button>\n" +
                "      </div>\n" +
                "    </div>\n" +
                "    <div class=\"card-body bsItemContainer\">#(children?.get(0) ??)</div>\n" +
                "  </div>\n" +
                "</div>\n";
    }


    @Override
    public BsFormComponent toBsFormComponent() {
        BsFormComponent component = new BsFormComponent();
        component.setName("板块");
        component.setTag("block");
        component.setDisableTools(true);
        component.setDragType("block");
        return component;
    }
}
