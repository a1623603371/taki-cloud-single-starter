package com.taki.cloud.es.template;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.taki.cloud.es.result.EsResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.*;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.awt.image.ReplicateScaleFilter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @ClassName EsTemplateImpl
 * @Description es 操作 模板
 * @Author Long
 * @Date 2023/6/12 17:50
 * @Version 1.0
 */
@Slf4j
@AllArgsConstructor
public class EsTemplateImpl implements EsTemplate{

    /**
     * es 客户端
     */
    private RestHighLevelClient restHighLevelClient;

    @Override
    public boolean initIndexMappings(String indexName, String endIndexMappingJson) throws Exception {
        // 1.构造指定的索引是否存在的请求
        GetIndexRequest existIndexRequest = new GetIndexRequest(indexName);

        // 2.查询指定的索引是否存在
        boolean exit = restHighLevelClient.indices().exists(existIndexRequest, RequestOptions.DEFAULT);


        Request request;

        Response response;

        if (!exit) {
            // 构造索引元素数据请求

            request = new Request("PUT",indexName);
            request.setJsonEntity(endIndexMappingJson);
            // 使用es result api 来对索引进行创建
            response = restHighLevelClient.getLowLevelClient().performRequest(request);
            log.info("初始化索引mapping的结构的请求结果为【{}】", EntityUtils.toString(response.getEntity()));
            return true;
        }

        return false;
    }

    @Override
    public boolean saveOrUpdate(String indexName, Object documentIndexObject, String documentId) throws Exception {
        String userJson = JSON.toJSONString(documentIndexObject);
        IndexRequest indexRequest = new IndexRequest(indexName).source(userJson, XContentType.JSON);
        UpdateRequest updateRequest = new UpdateRequest(indexName,documentId).doc(userJson,XContentType.JSON).upsert(indexRequest);
        UpdateResponse updateResponse = restHighLevelClient.update(updateRequest,RequestOptions.DEFAULT);

        String index = updateResponse.getIndex();

        String docId = updateResponse.getId();
        long version = updateResponse.getVersion();
        log.info(String.format("Docmnet update:index = %s,docId=%s ,version = %s",index,docId,version));

        return updateResponse.getResult() == DocWriteResponse.Result.CREATED ||
                updateResponse.getResult() == DocWriteResponse.Result.UPDATED ||
                updateResponse.getResult() == DocWriteResponse.Result.NOOP;
    }

    @Override
    public String queryById(String indexName, String id) throws IOException {

        GetRequest getRequest = new GetRequest(indexName,id);

        GetResponse getResponse = restHighLevelClient.get(getRequest,RequestOptions.DEFAULT);



        return getResponse.getSourceAsString();
    }

    @Override
    public MultiGetItemResponse[] multiGetOrderInfo(String indexName, List<String> docsIds) throws IOException {

        MultiGetRequest multiGetRequest = new MultiGetRequest();

        // 构造批量查询条件

        docsIds.forEach(docId->{
        MultiGetRequest.Item item = new MultiGetRequest.Item(indexName,docId);
        multiGetRequest.add(item);
        });

        // 发起批量查询条件
        MultiGetResponse multiGetItemResponses = restHighLevelClient.mget(multiGetRequest,RequestOptions.DEFAULT);
        return multiGetItemResponses.getResponses();
    }

    @Override
    public EsResponse search(String indexName, SearchSourceBuilder searchSourceBuilder) throws IOException {

        // 获取返回hits
        SearchHits hits =searchHits(indexName,searchSourceBuilder);

        JSONArray jsonArray = new JSONArray();

        hits.forEach(item->{
        String sourceAsString  = item.getSourceAsString();
            JSONObject jsonObject = JSONObject.parseObject(sourceAsString);
            jsonArray.add(jsonArray);
        });

        EsResponse response = new EsResponse();
        response.setTotal(hits.getHits().length);
        response.setData(jsonArray);
        return response;
    }

    private SearchHits searchHits(String indexName, SearchSourceBuilder searchSourceBuilder) throws IOException {

        SearchRequest searchRequest = new SearchRequest(indexName);

        searchRequest.source(searchSourceBuilder);

        if (searchSourceBuilder.from() == -1){
            searchSourceBuilder.size(1000);
        }

        // 查询es
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);

        return searchResponse.getHits();

    }

    @Override
    public boolean updateDoc(String indexName, String docId, Object o) throws IOException {
        UpdateRequest updateRequest = new UpdateRequest(indexName,docId);
        updateRequest.doc(JSON.toJSON(o),XContentType.JSON);
        UpdateResponse updateResponse = restHighLevelClient.update(updateRequest,RequestOptions.DEFAULT);


        return updateResponse.getResult() == DocWriteResponse.Result.CREATED || updateResponse.getResult() == DocWriteResponse.Result.UPDATED;
    }

    @Override
    public boolean updateDoc(String indexName, String docId, Map<String, Object> map) throws IOException {
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.doc(map);
        UpdateResponse updateResponse = restHighLevelClient.update(updateRequest,RequestOptions.DEFAULT);

        String index = updateResponse.getIndex();
        String id = updateResponse.getId();
        long version = updateResponse.getVersion();
        log.info(String.format("Document update: index=%s,docId=%s,version=%s",index,id,version));

        return updateResponse.getResult() ==DocWriteResponse.Result.CREATED || updateResponse.getResult() ==DocWriteResponse.Result.UPDATED;


    }

    @Override
    public boolean deleteDoc(String indexName, String docId) throws IOException {

        DeleteRequest deleteRequest = new DeleteRequest();

        DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest,RequestOptions.DEFAULT);

        ReplicationResponse.ShardInfo shardInfo = deleteResponse.getShardInfo();

        if (shardInfo.getFailed() > 0){

            for (ReplicationResponse.ShardInfo.Failure failure : shardInfo.getFailures()) {
                String reason = failure.reason();
                log.error("delete doc error:{},docId:{}",reason,docId);
            }
        }

        return true;
    }

    @Override
    public boolean isIndexExists(String indexName) throws IOException {

        boolean exists = false;

        GetIndexRequest getIndexRequest  = new GetIndexRequest(indexName);
        getIndexRequest.humanReadable(true);
        exists = restHighLevelClient.indices().exists(getIndexRequest,RequestOptions.DEFAULT);

        return exists;
    }

    @Override
    public boolean isDocumentExists(String indexName, String docId) throws IOException {

        GetRequest getRequest = new GetRequest(indexName, docId);

        return restHighLevelClient.exists(getRequest,RequestOptions.DEFAULT);
    }
}
