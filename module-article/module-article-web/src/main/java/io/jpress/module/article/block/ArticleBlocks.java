package io.jpress.module.article.block;

import io.jpress.core.template.HtmlBlock;
import io.jpress.core.template.BlockManager;

public class ArticleBlocks  {

    public static void init(){

        //文章列表
        HtmlBlock articlesBlock = new HtmlBlock();
        articlesBlock.setId("system_articles");
        articlesBlock.setTitle("文章列表");
        articlesBlock.setType(HtmlBlock.DRAG_TYPE_SYSTEM);
        articlesBlock.setTemplate("<div> 文章列表 </idv>");

        BlockManager.me().addBlock(articlesBlock);
    }
}
