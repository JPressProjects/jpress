package io.jpress.base;

import io.jboot.db.model.JbootModel;
import io.jpress.commons.layer.SortModel;

import java.util.ArrayList;
import java.util.List;


public class BaseSortModel<M extends BaseSortModel<M>> extends JbootModel<M> implements SortModel<M> {


    private int layerNumber;
    private M parent;
    private List<M> childs;

    @Override
    public boolean isTop() {
        return getParentId() != null && getParentId() == 0;
    }

    @Override
    public Long getId() {
        return get("id");
    }

    @Override
    public Long getParentId() {
        return get("pid");
    }

    @Override
    public void setParent(M parent) {
        this.parent = parent;
    }

    @Override
    public M getParent() {
        return parent;
    }

    @Override
    public void setChilds(List childs) {
        this.childs = childs;
    }

    @Override
    public void addChild(M child) {
        if (childs == null) {
            childs = new ArrayList<>();
        }
        childs.add(child);
    }

    @Override
    public List<M> getChilds() {
        return childs;
    }

    public boolean hasChild() {
        return childs != null && !childs.isEmpty();
    }

    @Override
    public void setLayerNumber(int layerNumber) {
        this.layerNumber = layerNumber;
    }

    @Override
    public int getLayerNumber() {
        return layerNumber;
    }


    public String getLayerString() {
        if (layerNumber == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < layerNumber; i++) {
            if (i == 0) {
                sb.append("|—");
            } else {
                sb.append("—");
            }
        }
        return sb.toString();
    }

    public boolean isMyChild(long id) {
        if (childs == null || childs.isEmpty()) {
            return false;
        }

        return isMyChild(childs, id);
    }

    private boolean isMyChild(List<M> childs, long id) {
        for (M child : childs) {
            if (child.getId().equals(id)) {
                return true;
            }

            if (child.getChilds() != null) {
                boolean isChild = isMyChild(child.getChilds(), id);
                if (isChild) {
                    return true;
                }
            }
        }
        return false;
    }

}
