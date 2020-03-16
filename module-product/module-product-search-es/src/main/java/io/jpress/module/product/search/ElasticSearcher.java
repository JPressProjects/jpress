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
package io.jpress.module.product.search;

import com.jfinal.kit.LogKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.CPI;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.utils.StrUtil;
import io.jpress.JPressOptions;
import io.jpress.module.product.model.Product;
import io.jpress.module.product.service.search.ProductSearcher;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ElasticSearcher implements ProductSearcher {

    private static final Log LOG = Log.getLog(ElasticSearcher.class);


    private String index = "jpress-product-index";
    private String type = "jpress-product-type";


    private RestHighLevelClient client;
    private RestClient restClient;

    public ElasticSearcher() {
        String host = JPressOptions.get("product_search_es_host");
        int port = JPressOptions.getAsInt("product_search_es_port", 9200);

        RestClientBuilder builder = RestClient.builder(new HttpHost(host, port));
        configUserNameAndPassowrd(builder);
        client = new RestHighLevelClient(builder);
        restClient = builder.build();

        tryCreateIndex();
    }


    private void configUserNameAndPassowrd(RestClientBuilder builder) {
        String username = JPressOptions.get("product_search_es_username");
        String password = JPressOptions.get("product_search_es_password");

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
            if (LogKit.isDebugEnabled()) {
                LogKit.debug(response.toString());
            }
        } catch (Exception e) {
            LOG.error(e.toString(), e);
        }
    }

    public boolean checkIndexExist() {
        try {
            Response response = restClient.performRequest(new Request("HEAD", index));
            return response.getStatusLine().getReasonPhrase().equals("OK");
        } catch (Exception e) {
            LOG.error(e.toString(), e);
        }

        return false;

    }


    @Override
    public void addProduct(Product product) {
        IndexRequest indexRequest = new IndexRequest(index, type, product.getId().toString());
        indexRequest.source(product.toJson(), XContentType.JSON);
        try {
            IndexResponse response = client.index(indexRequest, RequestOptions.DEFAULT);
            if (LogKit.isDebugEnabled()) {
                LogKit.debug(response.toString());
            }
        } catch (Exception e) {
            LOG.error(e.toString(), e);
        }
    }

    @Override
    public void deleteProduct(Object id) {

        DeleteRequest request = new DeleteRequest(index, type, id.toString());
        try {
            DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
            if (LogKit.isDebugEnabled()) {
                LogKit.debug(response.toString());
            }
        } catch (Exception e) {
            LOG.error(e.toString(), e);
        }

    }

    @Override
    public void updateProduct(Product product) {
        UpdateRequest updateRequest = new UpdateRequest(index, type, product.getId().toString());
        Map<String, Object> map = new HashMap<>();
        map.putAll(CPI.getAttrs(product));
        updateRequest.doc(map);

        try {
            UpdateResponse response = client.update(updateRequest, RequestOptions.DEFAULT);
            if (LogKit.isDebugEnabled()) {
                LogKit.debug(response.toString());
            }
        } catch (Exception e) {
            LOG.error(e.toString(), e);
        }
    }


    @Override
    public Page<Product> search(String keyword, int pageNum, int pageSize) {

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
            if (response == null || response.getHits() == null || response.getHits().getTotalHits().value <= 0) {
                return null;
            }

            int total = (int) response.getHits().getTotalHits().value;

            List<Product> products = new ArrayList<>();
            response.getHits().forEach(hit -> {
                Product product = new Product();
                product.put(hit.getSourceAsMap());
                products.add(product);
            });

            return new Page<>(products, pageNum, pageSize, total / pageSize, total);

        } catch (Exception e) {
            LOG.error(e.toString(), e);
        }
        return null;
    }


}
