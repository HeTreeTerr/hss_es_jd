package com.hss;

import com.alibaba.fastjson.JSON;
import com.hss.bean.User;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * 7.1.1高级客户端测试API
 */
@SpringBootTest
class HssEsJdApplicationTests {

    @Autowired
    @Qualifier(value = "restHighLevelClient")
    private RestHighLevelClient client;

    //索引的创建
    @Test
    void testCreateIndex() throws IOException {
        //1.创建索引请求
        CreateIndexRequest request = new CreateIndexRequest("hss_index");
        //2.创建客户端执行请求
        CreateIndexResponse createIndexResponse = client.indices().
                create(request, RequestOptions.DEFAULT);

        System.out.println(createIndexResponse);
    }

    //检查索引是否存在
    @Test
    void testExistIndex() throws IOException {
        GetIndexRequest request = new GetIndexRequest("hss_index");
        Boolean exists = client.indices().exists(request,RequestOptions.DEFAULT);
        System.out.println("是否存在："+exists);
    }

    //测试删除
    @Test
    void testDeleteIndex() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest("hss_index");
        client.indices().delete(request,RequestOptions.DEFAULT);
    }

    //测试添加文档
    @Test
    void testAddDocumen() throws IOException {
        //创建对象
        User user = new User("何森森",14);
        //创建请求
        IndexRequest request = new IndexRequest("hss_index");

        //规则 put /hss_index/_doc/1
        request.id("1");
        request.timeout(TimeValue.timeValueSeconds(1));
        request.timeout("1s");

        //将我们的数据放入请求 json
        request.source(JSON.toJSONString(user),XContentType.JSON);

        //客户端发送请求，获取响应的结果
        IndexResponse indexResponse = client.index(request,RequestOptions.DEFAULT);

        System.out.println(indexResponse.toString());
    }

    //获取文档，判断是否存在 get /index/_doc/1
    @Test
    void testGetDocument() throws IOException {
        GetRequest getRequest = new GetRequest("hss_index","1");
        //不湖区返回的_source的上下文
        //getRequest.fetchSourceContext(new FetchSourceContext(false));
        //getRequest.storedFields("_none_");

        Boolean exists = client.exists(getRequest,RequestOptions.DEFAULT);

        //如果存在文档信息
        if(exists){
            GetResponse getResponse = client.get(getRequest,RequestOptions.DEFAULT);
            System.out.println(getResponse.getSourceAsString());
        }
    }

    //更新完档信息
    @Test
    void testUpdateDocument() throws IOException {
        UpdateRequest updateRequest = new UpdateRequest("hss_index","1");
        updateRequest.timeout("1s");
        User user = new User("帅_衰",23);
        updateRequest.doc(JSON.toJSONString(user),XContentType.JSON);

        UpdateResponse updateResponse = client.update(updateRequest,RequestOptions.DEFAULT);

        System.out.println(updateResponse.status());
    }

    //删除文档信息
    @Test
    void testDeleteDocument() throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest("hss_index","1");
        deleteRequest.timeout("1s");

        DeleteResponse deleteResponse = client.delete(deleteRequest,RequestOptions.DEFAULT);

        System.out.println(deleteResponse.status());
    }

    //特殊的，批量插入
    @Test
    void testBulkRequest() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("10s");

        ArrayList<User> userArrayList = new ArrayList<User>();
        userArrayList.add(new User("hss1",12));
        userArrayList.add(new User("hss2",13));
        userArrayList.add(new User("hss3",14));
        userArrayList.add(new User("hss4",15));
        userArrayList.add(new User("hss5",16));

        for(int i= 0; i<userArrayList.size(); i++){
            bulkRequest.add(
                    new IndexRequest("hss_index")
                            .id(""+(i+1))
                            .source(JSON.toJSONString(userArrayList.get(i)),XContentType.JSON)
            );
        }

        BulkResponse bulkResponse = client.bulk(bulkRequest,RequestOptions.DEFAULT);
        System.out.println(bulkResponse.hasFailures());// 是否失败 ,返回faslse代表成功
    }

    //查询
    @Test
    void testSearch() throws IOException {
        SearchRequest searchRequest = new SearchRequest("hss_index");
        //构建搜索条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //HighlightBuilder 高亮构造器
        //查询条件，可以使用QueryBuilders工具来实现
        //QueryBuilders.termQuery 精确查询
        //QueryBuilders.matchAllQuery() 匹配所有
//        TermQueryBuilder termQueryBuilder =  QueryBuilders.termQuery("name","hss1");
        MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
        sourceBuilder.query(matchAllQueryBuilder);
        sourceBuilder.timeout(new TimeValue(60,TimeUnit.SECONDS));

        searchRequest.source(sourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest,RequestOptions.DEFAULT);
        //System.out.println(JSON.toJSONString(searchResponse.getHits()));
        for(SearchHit documentFields : searchResponse.getHits().getHits()){
            System.out.println(documentFields.getSourceAsMap());
        }
    }

}
