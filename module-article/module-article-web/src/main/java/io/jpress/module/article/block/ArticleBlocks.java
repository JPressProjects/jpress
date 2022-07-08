package io.jpress.module.article.block;

import io.jpress.core.template.HtmlBlock;
import io.jpress.core.template.BlockManager;

public class ArticleBlocks  {

    public static void init(){

        //文章列表
        HtmlBlock articlesBlock = new HtmlBlock();
        articlesBlock.setId("system_articles");
        articlesBlock.setName("文章列表");
        articlesBlock.setType(HtmlBlock.DRAG_TYPE_SYSTEM);

        String template = "<div class=\"article-card #blockOption('Class样式')\"\n" +
                "  style=\"#blockOption('Style样式:textarea' )\"\n" +
                ">\n" +
                "  <div class=\"article-card-title\">#blockOption(\"标题\",\"最新文章\")</div>\n" +
                "  <ul class=\"article-card-list\">\n" +
                "    #articles(count=blockOption(\"文章数量\",10),orderBy=blockOption(\"排序方式\",'created')) " +
                "     #for(article : articles)\n" +
                "    <li class=\"article-card-item\">\n" +
                "      <a href=\"#(article.url ??)\"> #(article.title ??) </a>\n" +
                "    </li>\n" +
                "    #end #end\n" +
                "  </ul>\n" +
                "</div>\n";
        articlesBlock.setTemplate(template);

        BlockManager.me().addBlock(articlesBlock);
    }
}
