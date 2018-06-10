/**
 * Copyright 2018-2019 Debmalya Jash
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dao;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.google.gson.JsonObject;

public class ElasticSearchDAO {
	
	private RestClient restClient;

    private RestHighLevelClient restHighLevelClient;

    public ElasticSearchDAO(ElasticHost... elasticHosts) throws UnknownHostException {
        int noOfHosts = elasticHosts.length;
        HttpHost[] hosts = new HttpHost[elasticHosts.length];

        for (int i = 0; i < noOfHosts; i++) {
            hosts[i] = elasticHosts[i].getHttpHost();

        }
        restClient = RestClient.builder(hosts).build();
        restHighLevelClient = new RestHighLevelClient(
                RestClient.builder(hosts));

    }


    public RestClient getRestClient() {
        return restClient;
    }

    /**
     * @param collectionName   -
     * @param objectToBeStored - JSON object to be stored.
     * @return status code of operation
     * @throws IOException
     */
    public int store(final String collectionName, final JsonObject objectToBeStored, final String key) throws IOException {
        HttpEntity httpEntity = new StringEntity(objectToBeStored.toString(), ContentType.APPLICATION_JSON);
        Map<String, String> params = new HashMap<>();
        Header basicHeader = new BasicHeader("name", "dashboard");
        Response response = restClient.performRequest("POST",  collectionName+"/"+key, params, httpEntity, basicHeader);
        return response.getStatusLine().getStatusCode();
    }

    public void search(final String type) throws IOException {
        SearchRequest searchRequest = new SearchRequest("posts");
        searchRequest.types(type);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        String[] includeFields = new String[]{"ColumnHeaders", "Rows"};
        String[] excludeFields = new String[]{"_type"};
        sourceBuilder.fetchSource(includeFields, excludeFields);
        sourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(sourceBuilder);
        searchRequest.indicesOptions(IndicesOptions.lenientExpandOpen());
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);


        System.out.println(searchResponse);
    }

    public void cleanIndex(String... indexName) throws IOException {
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest();
        deleteIndexRequest.indices(indexName);
        Header header = new BasicHeader("index", indexName[0]);
        restHighLevelClient.indices().delete(deleteIndexRequest,header);
    }

    public void shutdown() throws IOException {
        if (restClient != null){
            restClient.close();
        }

        if (restHighLevelClient != null){
            restHighLevelClient.close();
        }
    }

}
