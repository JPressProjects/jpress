package io.jpress.searcher;

import com.jfinal.plugin.activerecord.Page;
import io.jpress.plugin.search.ISearcher;
import io.jpress.plugin.search.SearcherBean;
import io.jpress.utils.StringUtils;
import io.redisearch.Document;
import io.redisearch.Query;
import io.redisearch.Schema;
import io.redisearch.SearchResult;
import io.redisearch.client.Client;

import java.util.*;

/**
 * Created by francis on 2017/7/13.
 */
public class Redisearcher implements ISearcher {
    public static final int port = 6380;
    public static final String ip = "45.77.33.95";
    private static final int poolSize = 20;
    private static final int timeOut = 1000;
    private static final String indexName = "demoworld";
    public static final String password = "";
    public static final String tagFieldName = "tags";

    private Client client = null;

    public Redisearcher() {
        client = new Client(this.indexName, this.ip, this.port, this.timeOut, this.poolSize);
        if (!Redisearcher.isInitFinished()) {
            initSchema();
            System.out.println("redisearch init ok");
        }
    }

    @Override
    public void init() {

    }

    @Override
    public void addBean(SearcherBean bean) {
        this.updateBean(bean);
    }

    @Override
    public void deleteBean(String beanId) {
        if (StringUtils.isBlank(beanId)) {
            return;
        }
        boolean flag = client.deleteDocument(beanId);
        System.out.println("delete successfully? " + flag + "," + beanId);
    }

    @Override
    public void updateBean(SearcherBean bean) {
        if (bean == null) {
            return;
        }
        Map<String, Object> tmpFields = this.getDocumentFields(bean);
        client.replaceDocument(bean.getSid(), 1.0, tmpFields);
    }

    @Override
    public Page<SearcherBean> search(String keyword, String module) {
        return this.search(keyword, module, 1, 10);
    }

    @Override
    public Page<SearcherBean> search(String queryString, String module, int pageNum, int pageSize) {
        int offset = calcStartIndexByPageNumber(pageNum, pageSize);
        String strKeyword = makeRedisearchQueryString(queryString);
        Query q = new Query(strKeyword).setSortBy("created", false).limit(offset, pageSize);
        SearchResult res = client.search(q);

        Page<SearcherBean> tmpResult = trans(res, pageNum, pageSize);
        return tmpResult;
    }

    private Map<String, Object> getDocumentFields(SearcherBean bean) {
        Map<String, Object> tmpResult = new HashMap<>();
        if (bean == null) {
            return tmpResult;
        }
        tmpResult.put("content", bean.getContent());
        tmpResult.put("title", bean.getTitle());
        tmpResult.put("url", bean.getUrl());
        tmpResult.put("description", bean.getDescription());
        tmpResult.put("created", bean.getCreated().getTime());

        if (bean.getData() != null) {
            Map<String, Object> tmpData = (Map<String, Object>) bean.getData();
            if (tmpData != null) {
                if (tmpData.containsKey(tagFieldName)) {
                    tmpResult.put(tagFieldName, tmpData.get(tagFieldName));
                }
            }
        }

        return tmpResult;
    }

    //solr结果转化
    private Page<SearcherBean> trans(SearchResult results, int pageNumber, int pageSize) {
        if (results == null || results.totalResults == 0) {
            return new Page<>(new ArrayList<SearcherBean>(), 1, pageSize, 0, 0);
        }
        long totalPage = results.totalResults % pageSize > 0 ? (results.totalResults / pageSize + 1) : results.totalResults / pageSize;
        Page<SearcherBean> tmpResult = new Page<>(new ArrayList<SearcherBean>(), pageNumber, pageSize, (int) totalPage, (int) results.totalResults);
        SearcherBean tmpItem = null;
        for (Document doc : results.docs) {
            tmpItem = trans1(doc);
            if (tmpItem == null) {
                continue;
            }
            tmpResult.getList().add(tmpItem);
        }
        return tmpResult;
    }

    private SearcherBean trans1(Document resultDoc) {
        if (resultDoc == null) {
            return null;
        }
        boolean flag = !resultDoc.hasProperty("title") ||
                !resultDoc.hasProperty("content") || !resultDoc.hasProperty("url") || !resultDoc.hasProperty("description");
        if (flag) {
            return null;
        }
        SearcherBean tmpItem = new SearcherBean();
        tmpItem.setContent(resultDoc.get("content").toString());
        tmpItem.setTitle(resultDoc.get("title").toString());
        tmpItem.setSid(resultDoc.getId());
        tmpItem.setUrl(resultDoc.get("url").toString());
        tmpItem.setDescription(resultDoc.get("description").toString());
        if (resultDoc.hasProperty("created")) {
            tmpItem.setCreated(new Date(Long.valueOf(resultDoc.get("created").toString())));
        } else {
            tmpItem.setCreated(new Date());
        }
        return tmpItem;
    }

    private String makeRedisearchQueryString(String keyWord) {
        return String.format("@title|" + tagFieldName + "|description|content:(%s)", formatKeywords(keyWord));
    }

    //分页方法的转换
    public static int calcStartIndexByPageNumber(int pageNumber, int pageSize) {
        //startIndex = (pageNumber-1) * pageSize, 通过这个公式计算 pageNumber
        return (pageNumber - 1) * pageSize;//这里只考虑从第一条数据 按照整页分页查询的方式， 比如从第二条开始按照每页10条查询不支持
    }

    //将全部的逗号分隔， 制表符分隔，多个空格 替换为一个空格隔开的关键序列， 方便solr搜索
    public static String formatKeywords(String keywords) {
        if (keywords == null || keywords.length() == 0) {
            return null;
        }
        String strKeywords = keywords.trim().replace("，", " ").replace(",", " ").replaceAll("\t", " ").replaceAll("[\\ ]{1,}", "|");
        if (strKeywords.length() == 0) {
            return null;
        }
        return strKeywords;
    }

    private void initSchema() {
        Schema schema = new Schema()
                .addTextField("title", 0.9)
                .addTextField(tagFieldName, 0.8)
                .addTextField("description", 0.8)
                .addTextField("content", 0.7)
                .addTextField("url", 1.0)
                .addNumericField("created");
        client.createIndex(schema, Client.IndexOptions.Default());
    }

    private static boolean isInitFinished() {
        String strCheckKey = "idx.demoworld.flag";
        long ret = RedisHelper.INSTANCE.setnx(strCheckKey, "ok");
        return ret != 1;
    }
}
