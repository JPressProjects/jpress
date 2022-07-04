package io.jpress.core.bsformbuilder;

public class BsFromComponentData extends DataBase {


    public String getId(){
        return getString("id");
    }

    public String getTag(){
        return getString("tag");
    }

    public String getLabel(){
        return getString("label");
    }

    public String getName(){
        return getString("name");
    }


}
