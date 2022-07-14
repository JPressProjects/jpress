package io.jpress.core.template;

import io.jpress.core.bsformbuilder.BsFormComponent;

public class BlockContainerComponent extends BsFormComponent {

    public BlockContainerComponent() {
        setTag("container");
        setName("板块");
        setDisableTools(true);
        setDragType("container");


        BsFormComponent.Prop idProp = new BsFormComponent.Prop("id");
        idProp.setDisabled("ture");
        idProp.setLabel("板块容器");

        addProp(idProp);


        addProp(new BsFormComponent.Prop("tag", "none"));
        addProp(new BsFormComponent.Prop("name", "none"));
        addProp(new BsFormComponent.Prop("label", "none"));

        setTemplate("<div class=\"m-3 pt-2 bsFormFilter\">\n" +
                "  <div class=\"card\">\n" +
                "    <div class=\"card-header\">\n" +
                "      <h3 class=\"card-title\">#(id ??)</h3>\n" +
                "      <div class=\"card-tools\">\n" +
                "        <button type=\"button\" class=\"btn btn-tool\" data-card-widget=\"collapse\">\n" +
                "          <i class=\"fas fa-minus\"></i>\n" +
                "        </button>\n" +
                "      </div>\n" +
                "    </div>\n" +
                "    <div class=\"card-body bsItemContainer\">#(children?.get(0) ??)</div>\n" +
                "  </div>\n" +
                "</div>\n");
    }


}
