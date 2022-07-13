package io.jpress.module.form.bsformbuilder;

import io.jpress.core.bsformbuilder.BsFormManager;
import io.jpress.module.form.bsformbuilder.components.InputComponent;
import io.jpress.module.form.bsformbuilder.components.TextareaComponent;

public class FormManager extends BsFormManager {

    private static final FormManager me = new FormManager();

    private FormManager() {
        initComponents();
    }

    private void initComponents() {
        addComponent(new InputComponent());
        addComponent(new TextareaComponent());
    }


    public static FormManager me() {
        return me;
    }

}
