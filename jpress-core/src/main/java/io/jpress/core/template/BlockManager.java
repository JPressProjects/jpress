/**
 * Copyright (c) 2016-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jpress.core.template;

import io.jpress.core.bsformbuilder.BsFormComponent;
import io.jpress.core.bsformbuilder.BsFormManager;
import io.jpress.core.template.blocks.ContainerBlock;
import io.jpress.core.template.blocks.DivBlock;

import java.util.ArrayList;
import java.util.List;

public class BlockManager extends BsFormManager {

    private static final BlockManager me = new BlockManager();

    private BlockManager() {
        initSystemBlockHtmls();
    }

    private void initSystemBlockHtmls() {
        addComponent(new ContainerBlock().toBsFormComponent());
        addComponent(new DivBlock().toBsFormComponent());
        addComponent(new DivBlock().toBsFormComponent());
    }


    public static BlockManager me() {
        return me;
    }



    public void addBlock(HtmlBlock block) {
        addComponent(block.toBsFormComponent());
    }



    @Override
    public List<BsFormComponent> getAllComponents() {
        List<BsFormComponent> allComponents = new ArrayList<>(super.getAllComponents());

        Template currentTemplate = TemplateManager.me().getCurrentTemplate();
        List<HtmlBlock> templateBlockHtmls = currentTemplate.getHtmlBlocks();

        if (templateBlockHtmls != null && !templateBlockHtmls.isEmpty()) {
            templateBlockHtmls.forEach(htmlBlock -> allComponents.add(htmlBlock.toBsFormComponent()));
        }

        return allComponents;
    }





    @Override
    protected BsFormComponent getComponentByTag(String tag) {
        BsFormComponent component = super.getComponentByTag(tag);
        if (component != null){
            return component;
        }


        Template currentTemplate = TemplateManager.me().getCurrentTemplate();
        List<HtmlBlock> templateBlockHtmls = currentTemplate.getHtmlBlocks();
        for (HtmlBlock block : templateBlockHtmls) {
            if (tag.equals(block.getId())) {
                return block.toBsFormComponent();
            }
        }

        return null;
    }




}
