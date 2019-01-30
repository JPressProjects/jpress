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

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jfinal.plugin.activerecord.Page;
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
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;


public class ElasticSearcher implements ArticleSearcher {


    private RestHighLevelClient client;

    public ElasticSearcher(){
        String host = JPressOptions.get("article_search_es_host");
        int port = JPressOptions.getAsInt("article_search_es_port", 9200);

        RestClientBuilder builder = RestClient.builder(new HttpHost(host, port));
        configUserNameAndPassowrd(builder);
        client = new RestHighLevelClient(builder);
    }




    private void configUserNameAndPassowrd(RestClientBuilder builder) {
        String username = JPressOptions.get("article_search_es_username");
        String password = JPressOptions.get("article_search_es_password");

        if (StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
            return;
        }

        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, username));
        builder.setHttpClientConfigCallback(httpAsyncClientBuilder -> httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
    }


    @Override
    public void addArticle(Article article) {

    }

    @Override
    public void deleteArticle(Object id) {

    }

    @Override
    public void updateArticle(Article article) {

    }


    @Override
    public Page<Article> search(String keyword, int pageNum, int pageSize) {
        return null;
    }

    public boolean createIndex(String index) {
        //index名必须全小写，否则报错
        CreateIndexRequest request = new CreateIndexRequest(index);
        try {
            CreateIndexResponse indexResponse = client.indices().create(request, RequestOptions.DEFAULT);
            return indexResponse.isAcknowledged();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String addData(String index, String type, String id, JSONObject object) {
        IndexRequest indexRequest = new IndexRequest(index, type, id);
        try {
            indexRequest.source(new ObjectMapper().writeValueAsString(object), XContentType.JSON);
            IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
            return indexResponse.getId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
