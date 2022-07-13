package io.jpress.module.form;

import com.jfinal.kit.PathKit;
import io.jboot.utils.FileUtil;
import io.jpress.core.bsformbuilder.BsFormComponent;
import io.jpress.core.bsformbuilder.BsFormManager;

import java.io.File;

public class FormManager extends BsFormManager {

    private static final FormManager me = new FormManager();

    private FormManager() {
        initComponents();
    }

    private void initComponents() {
        File componentDir =  new File(PathKit.getWebRootPath(),"/WEB-INF/views/admin/form/components");
        if (!componentDir.exists()){
            return;
        }

        File[] componentFiles = componentDir.listFiles(pathname -> pathname.getName().endsWith(".html"));
        if (componentFiles == null){
            return;
        }

        for (File componentFile : componentFiles) {
            String template = FileUtil.readString(componentFile);
            String tag = componentFile.getName().substring(0,componentFile.getName().length() - 5); //remove .html

            BsFormComponent component = new BsFormComponent();
            component.setTag(tag);
            component.setTemplate(template);

            addComponent(component);
        }
    }


    public static FormManager me() {
        return me;
    }

}
