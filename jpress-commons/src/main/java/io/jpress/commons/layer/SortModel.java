package io.jpress.commons.layer;


import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 用于可以排序和父子分类等模型
 * @Package io.jpress.commons.layer
 */
public interface SortModel<M extends SortModel> {

    public boolean isTop();

    public Long getId();

    public Long getParentId();

    public void setParent(M parent);

    public M getParent();

    public void setChilds(List<M> childs);

    public void addChild(M child);

    public List<M> getChilds();

    public void setLayerNumber(int layerNumber);

    public int getLayerNumber();


}
