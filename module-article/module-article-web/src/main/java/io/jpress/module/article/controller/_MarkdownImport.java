package io.jpress.module.article.controller;

import com.jfinal.kit.Ret;
import com.jfinal.upload.UploadFile;
import io.jboot.utils.FileUtils;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.commons.utils.AttachmentUtils;
import io.jpress.module.article.kit.markdown.MarkdownParser;
import io.jpress.module.article.model.Article;
import io.jpress.web.base.AdminControllerBase;

import java.io.File;
import java.text.ParseException;

/**
 * @author Ryan Wang（i@ryanc.cc）
 * @version V1.0
 * @Package io.jpress.module.article.controller
 */
@RequestMapping("/admin/setting/tools/markdown")
public class _MarkdownImport extends AdminControllerBase {

    public void index() {
        render("article/markdown.html");
    }


    public void doMarkdownImport() {

        UploadFile ufile = getFile();
        if (ufile == null) {
            renderJson(Ret.fail("message", "您还未选择Markdown文件"));
            return;
        }

        if (!".md".equals(FileUtils.getSuffix(ufile.getFileName()))) {
            renderJson(Ret.fail("message", "请选择Markdown格式的文件"));
            return;
        }

        String newPath = AttachmentUtils.moveFile(ufile);
        File mdFile = AttachmentUtils.file(newPath);

        MarkdownParser markdownParser = new MarkdownParser();
        markdownParser.parse(mdFile);

        Article article = null;

        try {
            article = markdownParser.getArticle();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (null != article) {
            article.setUserId(getLoginedUser().getId());
            article.save();
        }

        renderJson(Ret.ok());
    }
}
