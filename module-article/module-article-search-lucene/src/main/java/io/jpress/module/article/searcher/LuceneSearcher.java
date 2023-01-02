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
package io.jpress.module.article.searcher;

import com.jfinal.kit.PathKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import io.jpress.commons.utils.CommonsUtils;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.service.search.ArticleSearcher;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.lionsoul.jcseg.ISegment;
import org.lionsoul.jcseg.analyzer.JcsegAnalyzer;
import org.lionsoul.jcseg.dic.DictionaryFactory;
import org.lionsoul.jcseg.segmenter.SegmenterConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LuceneSearcher implements ArticleSearcher {

    private static final Log LOG = Log.getLog(LuceneSearcher.class);

    public static String INDEX_PATH = "lucene/articles/";
    private static Directory directory;


    public LuceneSearcher() {
        File indexDir = new File(PathKit.getRootClassPath(), INDEX_PATH);
        if (!indexDir.exists()) {
            indexDir.mkdirs();
        }
        try {
            directory = NIOFSDirectory.open(indexDir.toPath());
        } catch (Exception e) {
            LOG.error(e.toString(), e);
        }
    }

    @Override
    public void addArticle(Article article) {
        IndexWriter writer = null;
        try {
            writer = createIndexWriter();
            Document doc = createDocument(article);
            writer.addDocument(doc);
        } catch (Exception e) {
            LOG.error(e.toString(), e);
        } finally {
            CommonsUtils.quietlyClose(writer);
        }
    }

    @Override
    public void deleteArticle(Object id) {
        IndexWriter writer = null;
        try {
            writer = createIndexWriter();
            writer.deleteDocuments(new Term("aid", id.toString()));
            writer.commit();
            writer.flush();
        } catch (Exception e) {
            LOG.error(e.toString(), e);
        } finally {
            CommonsUtils.quietlyClose(writer);
        }
    }

    @Override
    public void updateArticle(Article article) {
        deleteArticle(article.getId());
        addArticle(article);
    }


    @Override
    public Page<Article> search(String keyword, int pageNum, int pageSize) {
        IndexReader indexReader = null;
        try {
            //Bug fix ,查询关键字使用一下 QueryParser.escape(keyword),例如：keyword=I/O,否则buildQuery时会出现异常
            keyword = QueryParser.escape(keyword);
            indexReader = DirectoryReader.open(directory);
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            Query query = buildQuery(keyword);

            ScoreDoc lastScoreDoc = getLastScoreDoc(pageNum, pageSize, query, indexSearcher);
            TopDocs topDocs = indexSearcher.searchAfter(lastScoreDoc, query, pageSize);

            SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<font class=\"" + HIGH_LIGHT_CLASS + "\">", "</font>");
            Highlighter highlighter = new Highlighter(formatter, new QueryScorer(query));
            highlighter.setTextFragmenter(new SimpleFragmenter(100));

            List<Article> articles = toArticleList(indexSearcher, topDocs, highlighter, keyword);
            int totalRow = getTotalRow(indexSearcher, query);
            return newPage(pageNum, pageSize, totalRow, articles);
        } catch (Exception e) {
            LOG.error(e.toString(), e);
        } finally {
            CommonsUtils.quietlyClose(indexReader);
        }
        return null;
    }


    private static ScoreDoc getLastScoreDoc(int pageIndex, int pageSize, Query query,
                                            IndexSearcher indexSearcher) throws Exception {
        if (pageIndex == 1) {
            return null; // 如果是第一页返回空
        }
        int num = pageSize * (pageIndex - 1); // 获取上一页的数量
        TopDocs tds = indexSearcher.search(query, num);
        return tds.scoreDocs[num - 1];
    }

    public static int getTotalRow(IndexSearcher searcher, Query query)
            throws Exception {
        TopDocs topDocs = searcher.search(query, 1000);
        if (topDocs == null || topDocs.scoreDocs == null
                || topDocs.scoreDocs.length == 0) {
            return 0;
        }
        ScoreDoc[] docs = topDocs.scoreDocs;
        return docs.length;
    }


    private static Page<Article> newPage(int pageNum, int pageSize, int totalRow, List<Article> articles) {
        int totalPages;
        if ((totalRow % pageSize) == 0) {
            totalPages = totalRow / pageSize;
        } else {
            totalPages = totalRow / pageSize + 1;
        }

        return new Page<>(articles,
                pageNum, pageSize, totalPages, totalRow);
    }


    private static Document createDocument(Article article) {
        Document doc = new Document();
        doc.add(new StringField("aid", article.getId().toString(), Field.Store.YES));
        doc.add(new TextField("content", article.getContent(), Field.Store.YES));
        doc.add(new TextField("text", article.getText(), Field.Store.YES));
        doc.add(new TextField("title", article.getTitle(), Field.Store.YES));
        doc.add(new StringField("created", DateTools.dateToString(article.getCreated() == null ? new Date() : article.getCreated(), DateTools.Resolution.YEAR), Field.Store.NO));
        return doc;
    }

    private static IndexWriter createIndexWriter() throws Exception {
        Analyzer analyzer = createAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        return new IndexWriter(directory, iwc);
    }


    private static Analyzer createAnalyzer() {
        SegmenterConfig config = new SegmenterConfig(true);
        return new JcsegAnalyzer(ISegment.Type.NLP, config, DictionaryFactory.createSingletonDictionary(config));
    }

    private static Query buildQuery(String keyword) {
        try {

            Analyzer analyzer = createAnalyzer();
            //这里使用text，防止搜索出html的tag或者tag中属性
            QueryParser queryParser1 = new QueryParser("text", analyzer);
            Query termQuery1 = queryParser1.parse(keyword);
            BooleanClause booleanClause1 = new BooleanClause(termQuery1, BooleanClause.Occur.SHOULD);


            QueryParser queryParser2 = new QueryParser("title", analyzer);
            Query termQuery2 = queryParser2.parse(keyword);
            BooleanClause booleanClause2 = new BooleanClause(termQuery2, BooleanClause.Occur.SHOULD);

            BooleanQuery.Builder builder = new BooleanQuery.Builder();
            builder.add(booleanClause1).add(booleanClause2);

            return builder.build();
        } catch (ParseException e) {
            LOG.error(e.toString(), e);
        }
        return null;
    }


    private List<Article> toArticleList(IndexSearcher searcher, TopDocs topDocs, Highlighter highlighter, String keyword) throws Exception {
        List<Article> articles = new ArrayList<>();
        Analyzer analyzer = createAnalyzer();
        for (ScoreDoc item : topDocs.scoreDocs) {

            Document doc = searcher.doc(item.doc);

            Article article = new Article();
            String title = doc.get("title");
            String content = doc.get("content");
            article.setId(Long.valueOf(doc.get("aid")));
            article.setTitle(title);
            article.setContent(content);

            //关键字高亮
            try {
                String highlightTitle = highlighter.getBestFragment(analyzer,"title",title);
                article.setHighlightTitle(highlightTitle);
            } catch (InvalidTokenOffsetsException e) {
                // ignore
            }

            try {
                String text = article.getText();
                String highlightContent = highlighter.getBestFragment(analyzer,"content",text);
                article.setHighlightContent(highlightContent);
            } catch (InvalidTokenOffsetsException e) {
                // ignore
            }

            articles.add(article);
        }
        return articles;
    }

}
