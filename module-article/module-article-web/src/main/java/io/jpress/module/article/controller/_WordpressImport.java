package io.jpress.module.article.controller;

import com.jfinal.kit.Ret;
import com.jfinal.log.Log;
import com.jfinal.upload.UploadFile;
import io.jboot.utils.FileUtils;
import io.jboot.utils.StrUtils;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConsts;
import io.jpress.commons.utils.AttachmentUtils;
import io.jpress.model.Attachment;
import io.jpress.model.User;
import io.jpress.module.article.model.Article;
import io.jpress.web.base.AdminControllerBase;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.module.article.controller
 */
@RequestMapping("/admin/setting/tools/wordpress")
public class _WordpressImport extends AdminControllerBase {

    public void index() {
        render("article/wordpress.html");
    }


    public void doWordPressImport() {

        keepPara();


        UploadFile ufile = getFile();
        if (ufile == null) {
            renderJson(Ret.fail("message", "您还未选择WordPress文件"));
            return;
        }

        if (!".xml".equals(FileUtils.getSuffix(ufile.getFileName()))) {
            renderJson(Ret.fail("message", "请选择从WordPress导出的XML文件"));
            return;
        }

        String newPath = AttachmentUtils.moveFile(ufile);
        File xmlFile = AttachmentUtils.file(newPath);

        List<Article> contents = null;//WordPressUtils.parse(xmlFile);
        if (contents == null || contents.size() == 0) {
            renderJson(Ret.fail("message", "无法解析WordPress格式，可能是导出有误"));
            return;
        }


        for (Article c : contents) {
            if (c.getCreated() == null) {
                c.setCreated(new Date());
            }

            String slug = StrUtils.isBlank(c.getSlug()) ? c.getTitle() : c.getSlug();
            c.setSlug(slug);

            if (c.getUserId() == null) {
                User user = getLoginedUser();
                c.setUserId(user.getId());
            }

//            if (c.getModule() == null) {
//                c.setModule(moduelName);
//            }

            c.save();
        }

        renderJson(Ret.ok());
    }


    public static class WordPressUtils extends DefaultHandler {
        private static final Log log = Log.getLog(WordPressUtils.class);
        private List<Article> articles = new ArrayList<>();
        private List<Attachment> attachments = new ArrayList<>();
        //
        private String elementValue = null;

        private String title;
        private String post_type;
        private String attachment_url;
        //        private String post_date;
        private String content_encoded;
        private String status;


        public void startParse(File wordpressXml) {
            try {
                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser parser = factory.newSAXParser();
                parser.parse(wordpressXml, this);
            } catch (Exception e) {
                log.warn("ConfigParser parser exception", e);
            }

        }

        public static void parse(File wordpressXml) {
            new WordPressUtils().startParse(wordpressXml);
        }


        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {

            if ("item".equalsIgnoreCase(qName)) {
                addContent();
                clear();
            } else if ("title".equalsIgnoreCase(qName)) {
                title = elementValue;
            } else if ("wp:post_type".equalsIgnoreCase(qName)) {
                post_type = elementValue;
            } else if ("content:encoded".equalsIgnoreCase(qName)) {
                content_encoded = elementValue;
            } else if ("wp:status".equalsIgnoreCase(qName)) {
                status = elementValue;
            } else if ("wp:attachment_url".equalsIgnoreCase(qName)) {
                attachment_url = elementValue;
            }

        }

        private void addContent() {
            if (StrUtils.isBlank(post_type)) {
                //unknow type,do nothing
                return;
            }

            switch (post_type) {
                case "attachment":
                    addAttachement();
                    break;
                case "post":
                    addArticle();
                    break;
            }

        }

        private void addAttachement() {
            if (StrUtils.isBlank(attachment_url)) {
                return;
            }

            Attachment attachment = new Attachment(attachment_url);
            attachments.add(attachment);
        }


        private void addArticle() {
            if (StrUtils.isBlank(title) || StrUtils.isBlank(content_encoded)) {
                return;
            }

            Article article = new Article();
            article.setTitle(title);
            article.setContent(content_encoded);
            article.setEditMode(JPressConsts.EDIT_MODE_HTML);
            if ("publish".equals(status)) {
                article.setStatus(Article.STATUS_NORMAL);
            } else if ("draft".equals(status)) {
                article.setStatus(Article.STATUS_DRAFT);
            } else {
                article.setStatus(Article.STATUS_DRAFT);
            }

            articles.add(article);
        }

        private void clear() {

            this.title = null;
            this.post_type = null;
            this.attachment_url = null;
//            this.post_date = null;
            this.content_encoded = null;
            this.status = null;
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            elementValue = new String(ch, start, length);
        }
    }


}
