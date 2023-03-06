package io.jpress.base;


import io.jpress.commons.layer.SortModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author michael yang (fuhai999@gmail.com)
 */
public class BaseSortOptionsModel<M extends BaseSortOptionsModel<M>> extends BaseOptionsModel<M> implements SortModel<M> {


    private int layerNumber;
    private M parent;
    private List<M> children;

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

//    @Override
//    public void setChilds(List childs) {
//        this.children = childs;
//    }

    @Override
    public void setChildren(List<M> children) {
        this.children = children;
    }

    @Override
    public void addChild(M child) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(child);
    }

    @Override
    public List<M> getChildren() {
        return children;
    }

    public boolean hasChild() {
        return children != null && !children.isEmpty();
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
                sb.append("|— ");
            } else {
                sb.append("— ");
            }
        }
        return sb.toString();
    }

    public boolean isMyChild(long id) {
        if (children == null || children.isEmpty()) {
            return false;
        }

        return isMyChild(children, id);
    }

    private boolean isMyChild(List<M> children, long id) {
        for (M child : children) {
            if (child.getId().equals(id)) {
                return true;
            }

            if (child.getChildren() != null) {
                boolean isChild = isMyChild(child.getChildren(), id);
                if (isChild) {
                    return true;
                }
            }
        }
        return false;
    }

}
