/**
 * Copyright (c) 2016-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
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

import com.aliyun.opensearch.DocumentClient;
import com.aliyun.opensearch.OpenSearchClient;
import com.aliyun.opensearch.SearcherClient;
import com.aliyun.opensearch.sdk.generated.OpenSearch;
import com.aliyun.opensearch.sdk.generated.search.Config;
import com.aliyun.opensearch.sdk.generated.search.SearchFormat;
import com.aliyun.opensearch.sdk.generated.search.SearchParams;
import com.aliyun.opensearch.sdk.generated.search.general.SearchResult;
import com.jfinal.plugin.activerecord.Page;
import io.jpress.JPressOptions;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.service.search.ArticleSearcher;

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

    @Override
    public void init() {

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
    public void addArticle(Article bean) {
        if (autoSync) return;
        try {
            String json = Action.addAction(bean).toJson();
            documentClient.push(json, appName, tableName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteArticle(Object id) {
        if (autoSync) return;
        try {
            String json = Action.delAction(id).toJson();
            documentClient.push(json, appName, tableName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateArticle(Article bean) {
        if (autoSync) return;
        deleteArticle(bean.getId());
        updateArticle(bean);
    }

    @Override
    public Page<Article> search(String keyword) {
        return search(keyword, 1, 10);
    }

    @Override
    public Page<Article> search(String keyword, int pageNum, int pageSize) {
        Config config = new Config();
        config.addToAppNames(appName);
        config.setStart((pageNum - 1) * pageSize);
        config.setHits(pageSize);
        config.setSearchFormat(SearchFormat.JSON);

        SearchParams searchParams = new SearchParams(config);

        //query 组合搜索的文档：https://help.aliyun.com/document_detail/29191.html?
        String query = String.format("title:'%s' OR content:'%s'",keyword);
        searchParams.setQuery(query);
        try {
            SearchResult searchResult = searcherClient.execute(searchParams);
            String resultJson = searchResult.getResult();


        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
