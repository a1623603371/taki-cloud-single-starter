package com.taki.cloud.es.template;

import com.taki.cloud.es.result.EsResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @ClassName EsTemplate
 * @Description TODO
 * @Author Long
 * @Date 2023/6/12 16:51
 * @Version 1.0
 */
public interface EsTemplate {

    /*** 
     * @description:  初始化es 索引 的mapping元数据
     * @param indexName
     * @param  endIndexMappingJson
     * @return  boolean
     * @author Long
     * @date: 2023/6/12 16:52
     */
    boolean initIndexMappings(String indexName,String endIndexMappingJson) throws Exception;


    /***
     * @description:  保存或者更新
     * @param indexName 索引名称
     * @param  documentIndexObject 文档对象
     * @param documentId 文档id
     * @return  boolean
     * @author Long
     * @date: 2023/6/12 16:53
     */
    boolean saveOrUpdate(String indexName,Object  documentIndexObject ,String  documentId) throws Exception;

    /*** 
     * @description:  根据索引名称 索引id  查询文档
     * @param indexName
     * @param  id
     * @return  java.lang.String
     * @author Long
     * @date: 2023/6/12 16:55
     */ 
    String queryById(String indexName,String id) throws IOException;

    /*** 
     * @description: 根据索引名称索引id查询文档
     * @param indexName 索引名称
     * @param i docsIds 文档id
     * @return  org.elasticsearch.action.get.MultiGetItemResponse[]
     * @author Long
     * @date: 2023/6/12 16:56
     */ 
    MultiGetItemResponse[] multiGetOrderInfo(String indexName, List<String>  docsIds) throws IOException;

    /*** 
     * @description: 搜索结果
     * @param indexName
     * @param searchSourceBuilder
     * @return  com.taki.cloud.es.result.EsResponse
     * @author Long
     * @date: 2023/6/12 16:58
     */ 
    EsResponse search(String indexName, SearchSourceBuilder searchSourceBuilder) throws IOException;

    /*** 
     * @description: 更新文档
     * @param indexName
     * @param   docId
     * @param   o
     * @return  boolean
     * @author Long
     * @date: 2023/6/12 16:59
     */ 
    boolean updateDoc(String indexName,String docId,Object o) throws IOException;

    /*** 
     * @description:  更新文档
     * @param indexName
     * @param  docId
     * @param   map
     * @return  boolean
     * @author Long
     * @date: 2023/6/12 17:00
     */ 
    boolean updateDoc(String indexName, String docId, Map<String,Object> map) throws IOException;

    /***
     * @description:  删除文档
     * @param indexName 索引名称
     * @param   docId 文档id
     * @return  boolean
     * @author Long
     * @date: 2023/6/12 17:01
     */
    boolean deleteDoc(String indexName,String docId) throws IOException;


    /*** 
     * @description:  索引是否存在
     * @param indexName
     * @return  boolean
     * @author Long
     * @date: 2023/6/12 17:16
     */ 
    boolean isIndexExists(String indexName) throws IOException;

    /*** 
     * @description:  文档是否存在
     * @param indexName 索引名称
     * @param  docId 文档 id
     * @return  boolean
     * @author Long
     * @date: 2023/6/12 19:14
     */ 
    boolean isDocumentExists(String indexName,String docId) throws IOException;
}
