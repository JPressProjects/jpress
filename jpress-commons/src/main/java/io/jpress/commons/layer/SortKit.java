/**
 * Copyright (c) 2016-2023, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

    public static <M extends SortModel> void fillParentAndChild(List<M> models) {
        if (models == null || models.size() == 0) {
            return;
        }
        for (M child : models) {
            M parent = getParent(models, child);
            if (parent != null) {
                child.setParent(parent);
                if (parent.getChilds() == null || !parent.getChilds().contains(child)) {
                    parent.addChild(child);
                }
            }
        }
    }

    private static <M extends SortModel> M getParent(List<M> models, M child) {
        for (M m : models) {
            if (child.getParentId() != null && child.getParentId().equals(m.getId())) {
                return m;
            }
        }
        return null;
    }

    public static <M extends SortModel> void toLayer(List<M> models) {
        if (models == null || models.isEmpty()) {
            return;
        }
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
        if (models == null || models.isEmpty()) {
            return;
        }
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
        for (M model : models) {
            if (parent.getId() != null && parent.getId().equals(model.getParentId())) {
                model.setParent(parent);
                model.setLayerNumber(parent.getLayerNumber() + 1);
                if (parent.getChilds() == null || !parent.getChilds().contains(model)) {
                    parent.addChild(model);
                }
                fillChild(model, models);
            }
        }
    }
}
