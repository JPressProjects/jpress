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

import com.jfinal.kit.LogKit;
import com.jfinal.plugin.activerecord.CPI;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.ConfigValue;
import io.jboot.utils.StrUtil;
import io.jpress.JPressOptions;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.service.search.ArticleSearcher;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.*;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ElasticSearcher implements ArticleSearcher {


    @ConfigValue("jpress.elasticsearch.index")
    private String index = "jpress-index";

    @ConfigValue("jpress.elasticsearch.type")
    private String type = "jpress-type";


    private RestHighLevelClient client;
    private RestClient restClient;

    public ElasticSearcher() {
        String host = JPressOptions.get("article_search_es_host");
        int port = JPressOptions.getAsInt("article_search_es_port", 9200);

        RestClientBuilder builder = RestClient.builder(new HttpHost(host, port));
        configUserNameAndPassowrd(builder);
        client = new RestHighLevelClient(builder);
        restClient = builder.build();

        tryCreateIndex();
    }


    private void configUserNameAndPassowrd(RestClientBuilder builder) {
        String username = JPressOptions.get("article_search_es_username");
        String password = JPressOptions.get("article_search_es_password");

        if (StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
            return;
        }

        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, username));
        builder.setHttpClientConfigCallback(httpAsyncClientBuilder ->
                httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
    }


    private void tryCreateIndex() {
        if (checkIndexExist()) {
            return;
        }

        CreateIndexRequest request = new CreateIndexRequest(index);
        try {
            CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
            if (LogKit.isDebugEnabled())LogKit.debug(response.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean checkIndexExist() {
        try {
            Response response = restClient.performRequest(new Request("HEAD", index));
            return response.getStatusLine().getReasonPhrase().equals("OK");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;

    }


    @Override
    public void addArticle(Article article) {
        IndexRequest indexRequest = new IndexRequest(index, type, article.getId().toString());
        indexRequest.source(article.toJson(), XContentType.JSON);
        try {
            IndexResponse response = client.index(indexRequest, RequestOptions.DEFAULT);
            if (LogKit.isDebugEnabled())LogKit.debug(response.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteArticle(Object id) {

        DeleteRequest request = new DeleteRequest(index, type, id.toString());
        try {
            DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
            if (LogKit.isDebugEnabled())LogKit.debug(response.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void updateArticle(Article article) {
        UpdateRequest updateRequest = new UpdateRequest(index, type, article.getId().toString());
        Map<String, Object> map = new HashMap<>();
        map.putAll(CPI.getAttrs(article));
        updateRequest.doc(map);

        try {
            UpdateResponse response = client.update(updateRequest, RequestOptions.DEFAULT);
            if (LogKit.isDebugEnabled())LogKit.debug(response.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public Page<Article> search(String keyword, int pageNum, int pageSize) {

        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.should(new MatchQueryBuilder("title", keyword));
        boolQueryBuilder.should(new MatchQueryBuilder("content", keyword));


        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.from(pageNum * pageSize - pageSize);
        sourceBuilder.size(pageSize);
        sourceBuilder.query(boolQueryBuilder);

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.source(sourceBuilder);
        searchRequest.indices(index);
        searchRequest.types(type);

        try {
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            if (response ==null || response.getHits() == null || response.getHits().totalHits <= 0){
                return null;
            }

            int total = (int) response.getHits().totalHits;

            List<Article> articles = new ArrayList<>();
            response.getHits().forEach(hit -> {
                Article article = new Article();
                article.put(hit.getSourceAsMap());
                articles.add(article);
            });

            return new Page<>(articles, pageNum, pageSize, total / pageSize, total);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
