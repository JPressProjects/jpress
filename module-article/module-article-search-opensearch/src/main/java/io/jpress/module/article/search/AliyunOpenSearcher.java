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
package io.jpress.module.article.search;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.opensearch.DocumentClient;
import com.aliyun.opensearch.OpenSearchClient;
import com.aliyun.opensearch.SearcherClient;
import com.aliyun.opensearch.sdk.generated.OpenSearch;
import com.aliyun.opensearch.sdk.generated.commons.OpenSearchResult;
import com.aliyun.opensearch.sdk.generated.search.Config;
import com.aliyun.opensearch.sdk.generated.search.SearchFormat;
import com.aliyun.opensearch.sdk.generated.search.SearchParams;
import com.aliyun.opensearch.sdk.generated.search.general.SearchResult;
import com.google.common.collect.Lists;
import com.jfinal.plugin.activerecord.Page;
import io.jpress.JPressOptions;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.service.search.ArticleSearcher;

import java.util.ArrayList;
import java.util.List;

/**
 * 帮助文档：https://help.aliyun.com/document_detail/52287.html
 */
public class AliyunOpenSearcher implements ArticleSearcher {


    private String tableName = "article";
    private String appName;
    private boolean autoSync;

    private OpenSearchClient client;
    private DocumentClient documentClient;
    private SearcherClient searcherClient;

    public AliyunOpenSearcher() {

        String accesskey = JPressOptions.get("article_search_aliopensearch_accesskey");
        String secret = JPressOptions.get("article_search_aliopensearch_secret");
        String host = JPressOptions.get("article_search_aliopensearch_host");

        OpenSearch config = new OpenSearch();
        config.setAccessKey(accesskey);
        config.setSecret(secret);
        config.setHost(host);

        appName = JPressOptions.get("article_search_aliopensearch_appname");
        autoSync = JPressOptions.getAsBool("article_search_aliopensearch_autosync");

        client = new OpenSearchClient(config);
        documentClient = new DocumentClient(client);
        searcherClient = new SearcherClient(client);

    }

    @Override
    public void addArticle(Article article) {
        if (autoSync) {
            return;
        }
        try {
            String json = Action.addAction(article).toJson();
            OpenSearchResult result = documentClient.push(json, appName, tableName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteArticle(Object id) {
        if (autoSync) {
            return;
        }
        try {
            String json = Action.delAction(id).toJson();
            OpenSearchResult result = documentClient.push(json, appName, tableName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateArticle(Article article) {
        if (autoSync) {
            return;
        }
        deleteArticle(article.getId());
        updateArticle(article);
    }

    @Override
    public Page<Article> search(String keyword, int pageNum, int pageSize) {
        Config config = new Config();
        config.addToAppNames(appName);
        config.setStart((pageNum - 1) * pageSize);
        config.setHits(pageSize);
        config.setSearchFormat(SearchFormat.JSON);
        config.setFetchFields(Lists.newArrayList("id", "title", "content"));

        SearchParams searchParams = new SearchParams(config);

        // query 组合搜索的文档：https://help.aliyun.com/document_detail/29191.html
//      // String query = "title:'"+keyword+"' OR content:'"+keyword+"'";
        String query = "default:'" + keyword + "'";
        searchParams.setQuery(query);
        try {
            SearchResult searchResult = searcherClient.execute(searchParams);
            String resultJson = searchResult.getResult();

            /**
             * {
             * "status": "OK",
             * "request_id": "154883085219726516242866",
             * "result": {
             * "searchtime": 0.004142,
             * "total": 1,
             * "num": 1,
             * "viewtotal": 1,
             * "compute_cost": [
             * {
             * "index_name": "apptest",
             * "value": 0.302
             * }
             * ],
             * "items": [
             * {
             * "content": "ddddd ",
             * "id": "109",
             * "title": "<em>aaaa</em>",
             * "index_name": "apptest"
             * }
             * ],
             * "facet": []
             * },
             * "errors": [],
             * "tracer": "",
             * "ops_request_misc": "%7B%22request%5Fid%22%3A%22154883085219726516242866%22%2C%22scm%22%3A%2220140713.160006631..%22%7D"
             * }
             */

            JSONObject jsonObject = JSONObject.parseObject(resultJson);
            if (!"ok".equalsIgnoreCase(jsonObject.getString("status"))) {
                return null;
            }

            JSONObject resultObject = jsonObject.getJSONObject("result");
            int total = resultObject.getInteger("total");

            List<Article> articles = new ArrayList<>();
            JSONArray jsonArray = resultObject.getJSONArray("items");
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                Article article = new Article();
                article.put(item.getInnerMap());
                articles.add(article);
            }

            return new Page<>(articles, pageNum, pageSize, total / pageSize, total);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
