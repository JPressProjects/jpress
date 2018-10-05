package io.jpress.commons.layer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 用于分层排序 或 用于进行树分层
 * @Package io.jpress.commons.layer
 */
public class SortKit {

    public static <M extends SortModel> void toLayer(List<M> models) {
        toTree(models);
        List<M> layerModelList = new ArrayList<>();
        treeToLayer(models, layerModelList);
        models.clear();
        models.addAll(layerModelList);
    }

    private static <M extends SortModel> void treeToLayer(List<M> treeList, List<M> layerList) {
        if (treeList == null || treeList.isEmpty()) {
            return;
        }

        for (M model : treeList) {
            layerList.add(model);
            treeToLayer(model.getChilds(), layerList);
        }
    }


    public static <M extends SortModel> void toTree(List<M> models) {
        List<M> temp = new ArrayList<>(models);
        models.clear();
        for (M model : temp) {
            if (model.isTop()) {
                model.setLayerNumber(0);
                fillChild(model, temp);
                models.add(model);
            }
        }
    }

    private static <M extends SortModel> void fillChild(M parent, List<M> models) {
        for (SortModel category : models) {
            if (category.getParentId().equals(parent.getId())) {
                category.setLayerNumber(parent.getLayerNumber() + 1);
                parent.addChild(category);
                fillChild(category, models);
            }
        }
    }
}
