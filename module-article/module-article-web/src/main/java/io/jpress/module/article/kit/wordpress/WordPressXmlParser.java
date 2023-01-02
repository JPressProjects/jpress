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
package io.jpress.module.article.kit.wordpress;

import com.jfinal.log.Log;
import io.jboot.utils.StrUtil;
import io.jpress.JPressConsts;
import io.jpress.model.Attachment;
import io.jpress.module.article.model.Article;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.module.article.kit.wordpress
 */
public class WordPressXmlParser extends DefaultHandler {
    private static final Log log = Log.getLog(WordPressXmlParser.class);
    private List<Article> articles = new ArrayList<>();
    private List<Attachment> attachments = new ArrayList<>();

    //单个节点的值
    private StringBuilder elementValue = null;

    private String title;
    private String post_type;
    private String attachment_url;
    private String content_encoded;
    private String status;

    public List<Article> getArticles() {
        return articles;
    }


    public List<Attachment> getAttachments() {
        return attachments;
    }


    public void parse(File wordpressXml) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            // Xerces 2 only - http://xerces.apache.org/xerces2-j/features.html#disallow-doctype-decl
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

            // If you can't completely disable DTDs, then at least do the following:
            // Xerces 1 - http://xerces.apache.org/xerces-j/features.html#external-general-entities
            // Xerces 2 - http://xerces.apache.org/xerces2-j/features.html#external-general-entities

            // JDK7+ - http://xml.org/sax/features/external-general-entities
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);

            // Xerces 1 - http://xerces.apache.org/xerces-j/features.html#external-parameter-entities
            // Xerces 2 - http://xerces.apache.org/xerces2-j/features.html#external-parameter-entities

            // JDK7+ - http://xml.org/sax/features/external-parameter-entities
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

            // Disable external DTDs as well
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

            // and these as well, per Timothy Morgan's 2014 paper: "XML Schema, DTD, and Entity Attacks"
            factory.setXIncludeAware(false);

            SAXParser parser = factory.newSAXParser();
            parser.parse(wordpressXml, this);
        } catch (Exception e) {
            log.warn("WordPressXmlParser exception", e);
        }

    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        elementValue = new StringBuilder();
        super.startElement(uri, localName, qName, attributes);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if ("item".equalsIgnoreCase(qName)) {
            addContent();
            clear();
        } else if ("title".equalsIgnoreCase(qName)) {
            title = elementValue.toString();
        } else if ("wp:post_type".equalsIgnoreCase(qName)) {
            post_type = elementValue.toString();
        } else if ("content:encoded".equalsIgnoreCase(qName)) {
            content_encoded = elementValue.toString();
        } else if ("wp:status".equalsIgnoreCase(qName)) {
            status = elementValue.toString();
        } else if ("wp:attachment_url".equalsIgnoreCase(qName)) {
            attachment_url = elementValue.toString();
        }

    }

    private void addContent() {
        if (StrUtil.isBlank(post_type)) {
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
        if (StrUtil.isBlank(attachment_url)) {
            return;
        }

        Attachment attachment = new Attachment(attachment_url);
        attachments.add(attachment);
    }


    private void addArticle() {
        if (StrUtil.isBlank(title) || StrUtil.isBlank(content_encoded)) {
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
        this.content_encoded = null;
        this.status = null;
        this.elementValue = new StringBuilder();
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        elementValue.append(new String(ch, start, length).trim());
    }
}