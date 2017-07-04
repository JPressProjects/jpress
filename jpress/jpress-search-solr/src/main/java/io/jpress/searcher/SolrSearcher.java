package io.jpress.searcher;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import io.jpress.plugin.search.ISearcher;
import io.jpress.plugin.search.SearcherBean;
import io.jpress.searcher.common.SearchEmum;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.MapSolrParams;
import org.apache.solr.common.params.SolrParams;

import java.io.IOException;
import java.util.*;

/**
 * Created by liwei.b on 2017/6/23.
 */
public class SolrSearcher implements ISearcher {
    static final Log log = Log.getLog(SolrSearcher.class);
    //    static final String solrUrl = "http://192.168.222.130:8983/solr/demoworldblogs";
    static final String solrUrl = "http://localhost:8983/solr/demoworldblogs";
    final int defaultPageSize = 10;

    @Override
    public void init() {

    }

    @Override
    public void addBean(SearcherBean bean) {
        SolrInputDocument tmpDoc = getInputDocBySearcherBean(bean);
        if(tmpDoc==null){
            return;
        }
        Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
        docs.add(tmpDoc);
        UpdateResponse tmpResponse = null;
        SolrClient solrClient = null;
        try {
            solrClient = getSolrClient();
            tmpResponse = solrClient.add(docs);
            solrClient.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                solrClient.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void deleteBean(String beanId) {
        if (beanId == null || beanId.length() == 0) {
            return;
        }
        SolrClient solrClient = null;
        try {
            solrClient = getSolrClient();
            solrClient.deleteById(beanId);
            solrClient.commit();
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                solrClient.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void updateBean(SearcherBean bean) {
        addBean(bean);
    }

    @Override
    public Page<SearcherBean> search(String keyword, String module) {
        return this.search(keyword, module, 1, defaultPageSize);
    }

    @Override
    public Page<SearcherBean> search(String keyword, String module, int pageNum, int pageSize) {
        Page<SearcherBean> tmpResult = new Page<SearcherBean>(new ArrayList<SearcherBean>(), 1, pageSize, 0, 0);
        String strKeyword = formatKeywords(keyword);
        if (strKeyword == null) {
            return tmpResult;
        }
        Map<String, String> tmpMap = new HashMap<>();
        tmpMap.put("indent", "off");//是否格式化输出结果， 如果你在网页上测试， 可以选择 on， 如果是api调用，请选择关闭， 减少数据传输
        tmpMap.put("q", String.format("*%s*", strKeyword));
        tmpMap.put("wt", "json");//输出格式
        int start = calcStartIndexByPageNumber(pageNum, pageSize);
        tmpMap.put("start", String.valueOf(start));
        tmpMap.put("rows", String.valueOf(pageSize));
        SolrParams tmpParams = new MapSolrParams(tmpMap);
        QueryResponse tmpResponse = null;
        SolrClient solrClient = null;
        try {
            solrClient = getSolrClient();
            tmpResponse = solrClient.query(tmpParams);
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                solrClient.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (tmpResponse != null && tmpResponse.getResults() != null) {
            tmpResult = trans(tmpResponse.getResults(), pageNum, pageSize);
        }
        return tmpResult;
    }

    //分页方法的转换
    private static int calcPageNumberByStartIndex(int startIndex, int pageSize) {
        //startIndex = (pageNumber-1) * pageSize, 通过这个公式计算 pageNumber
        return startIndex / pageSize + 1;//这里只考虑从第一条数据 按照整页分页查询的方式， 比如从第二条开始按照每页10条查询不支持
    }

    //分页方法的转换
    private static int calcStartIndexByPageNumber(int pageNumber, int pageSize) {
        //startIndex = (pageNumber-1) * pageSize, 通过这个公式计算 pageNumber
        return (pageNumber - 1) * pageSize;//这里只考虑从第一条数据 按照整页分页查询的方式， 比如从第二条开始按照每页10条查询不支持
    }

    //solr结果转化
    private Page<SearcherBean> trans(SolrDocumentList results, int pageNumber, int pageSize) {
        if (results == null || results.isEmpty()) {
            return new Page<SearcherBean>(new ArrayList<SearcherBean>(), 1, pageSize, 0, 0);
        }
        long totalPage = results.getNumFound() % pageSize > 0 ? (results.getNumFound() / pageSize + 1) : results.getNumFound() / pageSize;
        Page<SearcherBean> tmpResult = new Page<SearcherBean>(new ArrayList<SearcherBean>(), pageNumber, pageSize, (int) totalPage, (int) results.getNumFound());
        SearcherBean tmpItem = null;
        Object tmpValue = null;
        for (SolrDocument result : results) {
            tmpItem = trans1(result);
            if (tmpItem == null) {
                continue;
            }
            tmpResult.getList().add(tmpItem);
        }
        return tmpResult;
    }

    private SearcherBean trans1(SolrDocument resultDoc) {
        if (resultDoc == null || resultDoc.isEmpty()) {
            return null;
        }
        Map<String, Object> tmpValues = resultDoc.getFieldValueMap();
        if (tmpValues == null) {
            return null;
        }
        boolean flag = !tmpValues.containsKey("id") || !tmpValues.containsKey("title") ||
                !tmpValues.containsKey("content") || !tmpValues.containsKey("url") || !tmpValues.containsKey("description");
        if (flag) {
            return null;
        }
        SearcherBean tmpItem = new SearcherBean();
        tmpItem.setContent(tmpValues.get("content").toString());
        tmpItem.setTitle(tmpValues.get("title").toString());
        tmpItem.setSid(tmpValues.get("id").toString());
        tmpItem.setUrl(tmpValues.get("url").toString());
        tmpItem.setDescription(tmpValues.get("description").toString());
        tmpItem.setCreated(new Date(Long.valueOf(tmpValues.get("created").toString())));
        return tmpItem;
    }

    private SolrInputDocument getInputDocBySearcherBean(SearcherBean bean) {
        if (bean == null || bean.getSid() == null || bean.getSid().length() == 0) {
            return null;
        }
        if (bean.getContent() == null || bean.getContent().length() == 0) {
            return null;
        }
        if (bean.getDescription() == null || bean.getDescription().length() == 0) {
            return null;
        }
        if (bean.getTitle() == null || bean.getTitle().length() == 0) {
            return null;
        }
        if (bean.getUrl() == null || bean.getUrl().length() == 0) {
            return null;
        }
        if (bean.getCreated() == null) {
            return null;
        }
        SolrInputDocument tmpDoc = new SolrInputDocument();
        tmpDoc.addField("title", bean.getTitle());
        tmpDoc.addField("description", bean.getDescription());
        tmpDoc.addField("content", bean.getContent());
        tmpDoc.addField("url", bean.getUrl());
        tmpDoc.addField("created", bean.getCreated() != null ? bean.getCreated().getTime() : System.currentTimeMillis());
        tmpDoc.addField("id", bean.getSid());

        if (bean.getData() != null) {
            Map<String, Object> tmpData = (Map<String, Object>) bean.getData();
            if (tmpData != null) {
                if (tmpData.containsKey(SearchEmum.ArtileTags)) {
                    tmpDoc.addField("tags", tmpData.get(SearchEmum.ArtileTags));
                }
            }
        }
        return tmpDoc;
    }

    //将全部的逗号分隔， 制表符分隔，多个空格 替换为一个空格隔开的关键序列， 方便solr搜索
    public static String formatKeywords(String keywords) {
        if (keywords == null || keywords.length() == 0) {
            return null;
        }
        String strKeywords = keywords.trim().replace("，", " ").replace(",", " ").replaceAll("\t", " ").replaceAll("[\\ ]{2,}", " ");
        if (strKeywords.length() == 0) {
            return null;
        }
        return strKeywords;
    }

    private static SolrClient getSolrClient() {
        return new HttpSolrClient.Builder(solrUrl).build();
    }
}
