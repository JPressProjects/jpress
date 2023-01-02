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
package io.jpress.core.template;

import com.jfinal.kit.PathKit;
import io.jpress.core.bsformbuilder.BsFormComponent;
import io.jpress.core.bsformbuilder.BsFormManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BlockManager extends BsFormManager {

    private static final BlockManager me = new BlockManager();

    private BlockManager() {
        initComponents();
    }

    private void initComponents() {
        addComponent(new BlockContainerComponent());

        //根据路径来添加
        addBlocksByPath("","/WEB-INF/views/admin/template/blocks");
    }


    public static BlockManager me() {
        return me;
    }



    public void addBlock(HtmlBlock block) {
        addComponent(block.toBsFormComponent());
    }


    public void addBlocksByPath(String componentPrefix,String path) {
        File blocksPathDir =  new File(PathKit.getWebRootPath(),path);
        if (!blocksPathDir.exists()){
            return;
        }

        File[] blockFiles = blocksPathDir.listFiles(pathname -> pathname.getName().endsWith(".html"));
        if (blockFiles == null || blockFiles.length == 0){
            return;
        }

        for (File blockFile : blockFiles) {
            String blockFileName = blockFile.getName();

            HtmlBlock htmlBlock = new HtmlBlock();

            htmlBlock.setId(componentPrefix + blockFileName.substring(0, blockFileName.length() - 5));

            //fill blockHtml attrs
            TemplateUtil.readAndFillHtmlBlock(blockFile, htmlBlock);

            addComponent(htmlBlock.toBsFormComponent());
        }
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
