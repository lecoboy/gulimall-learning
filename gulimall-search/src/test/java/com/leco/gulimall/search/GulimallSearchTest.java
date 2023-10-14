package com.leco.gulimall.search;

import com.alibaba.fastjson.JSON;
import com.leco.gulimall.search.config.GulimallElasticSearchConfig;
import lombok.Data;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

/**
 * @author greg
 * @version 2023/10/14
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallSearchTest {
    @Autowired
    private RestHighLevelClient client;

    @Test
    public void test() {
        System.out.println(client);
    }

    @Test
    public void searchMatch() throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("bank");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("address", "mill"));
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = client.search(searchRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);
//        ArrayList<Account> accounts = new ArrayList<>();
        for (SearchHit hit : response.getHits().getHits()) {
            Account account = JSON.parseObject(hit.getSourceAsString(), Account.class);
//            accounts.add(account);
            System.out.println(account);
        }
//        System.out.println(accounts);
    }

    @Test
    public void searchAggs() throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("bank");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        searchSourceBuilder.query(QueryBuilders.matchQuery("address", "mill"));
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchSourceBuilder.size(0);
        TermsAggregationBuilder ageAgg = AggregationBuilders.terms("ageAgg").field("age").size(100);
        TermsAggregationBuilder genderAgg = AggregationBuilders.terms("genderAgg").field("gender.keyword");
        genderAgg.subAggregation(AggregationBuilders.avg("balanceAvg").field("balance"));
        ageAgg.subAggregation(genderAgg);
        ageAgg.subAggregation(AggregationBuilders.avg("balanceAvg").field("balance"));
        searchSourceBuilder.aggregation(ageAgg);
        System.out.println(searchSourceBuilder.toString());
        SearchRequest request = searchRequest.source(searchSourceBuilder);
        SearchResponse response = client.search(request, GulimallElasticSearchConfig.COMMON_OPTIONS);
        System.out.println(response.toString());
        Aggregations aggregations = response.getAggregations();
        Terms ageAggResult = aggregations.get("ageAgg");
        for (Terms.Bucket bucket : ageAggResult.getBuckets()) {
            String key = bucket.getKeyAsString();
            long docCount = bucket.getDocCount();
            System.out.println("key: " + key + ", docCount: " + docCount);
        }
    }

    @Test
    public void indexData() throws IOException {
        IndexRequest indexRequest = new IndexRequest("users");
        indexRequest.id("1");
//        indexRequest.source("userName", "zhangsan", "age", 19, "gender", "男");
        User user = new User();
        user.setUserName("张三");
        user.setAge(19);
        user.setGender("男");
        indexRequest.source(JSON.toJSONString(user), XContentType.JSON);

        IndexResponse response = client.index(indexRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);

        System.out.println(response);
    }

    @Data
    static class User {
        private String userName;
        private Integer age;
        private String gender;
    }

    @Data
    static class Account {
        private int account_number;
        private int balance;
        private String firstname;
        private String lastname;
        private int age;
        private String gender;
        private String address;
        private String employer;
        private String email;
        private String city;
        private String state;

    }

}
