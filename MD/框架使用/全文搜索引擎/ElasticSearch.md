# ElasticSearch

--------------------

## 什么是ElasticSearch

* 简介

  > elasticsearch是一个开源的全文搜索分析引擎，它允许您快速，近实时地存储，搜索和分析大量数据。它通常用作底层引擎/技术，为具有复杂搜索功能和要求的应用程序提供支持。

* 倒排索引

  > 倒排索引主要包含两部分：
  >
  > ​	单词词典:记录所有文档的单词，记录单词到倒排列表的关联信息
  >
  > ​	倒排列表:记录了单词对应的文档集合，由倒排索引项(文档id/单词词频/位置/偏移)组成

  

## 为什么使用ElasticSearch

* 特点：

  	1. elasticsearch在数据源不断增加(PB级别)的情况下，性能依旧非常好。另外其本身也支持分布式集群部署。
   	2. 对于文档的分词搜索相比于关系型数据库更加灵活，可以对数据进行分词后再进行搜索

* 性能：

  ​	[性能测试报告](https://github.com/mkocikowski/esbench)

* 使用场景：

  1. Github使用ES对1300亿行代码进行查询；
  2. 维基百科使用ES提供带有高亮片段的全文搜索；

## 如何使用ElasticSearch

* 使用rest API

  1. restfull风格

     | 请求方式 | 含义 |
     | :------: | :--: |
     |   GET    | 查询 |
     |   POST   | 修改 |
     |  DELETE  | 删除 |
     |   PUT    | 增加 |

  2. 请求规则

     Ip:9200/索引名/文档类型/id

     创建索

     ```json
     localhost:9200/索引名    PUT
     {
     	"settings":{
           "number_of_shards":5,   //设置5个分片
           "number_of_replicas":0    //设置0个副本
     	},
         "mappings":{
             "文档类型":{
                 "properties":{
                     "name":{      //设置字段   
                         "type":"text"   //设置字段类型
                     },
                     "age":{    //还有很多字段的公共属性（index/store/boost/analyzer）
                         "type":"integer"
                     },
                     "currentDate":{
                         "type":"date",
                         "format":"yyyy-MM-dd"
                     }   
                 }
             }
         } 
     }
     ```

     | type可选参数                                 | 含义         |
  | -------------------------------------------- | ------------ |
     | 数字类型(byte/shor/integer/log/double/float) | 数字类型数据 |
     | 文本类型(text)                               | 文本类型数据 |
     
     
     
     添加数据
     
     ```json
  ip:9200/索引名/文档名    POST
     {
      "name":"tom",
         "age":14,
         "currentDate":"2018-09-01"
     }
  ```
     
  查询数据
     
     ```json
     ip:9200/索引名/文档名/_search   GET
     ```
     
     删除索引
     
     ```json
     ip:9200/索引名     DELETE
     ```

* 使用java操作

  * 方法1：因为elasticsearch提供了restful风格的调用接口因此可以直接使用httpclient进行操作

  * 方法2：使用elasticsearch提供的jar包进行开发(es官方已经在8版本之后不再支持transport方式操作)

    ```java
    TransportClient client = new PreBuiltTransportClient(Settings.EMPTY).
        addTransportAddress(
        new TransportAddress(
            InetAddress.getByName("localhost"), 9300));
    
    //创建索引和添加数据（setSource()可以传递多种数据）
    client.prepareIndex("myindex3", "test").setSource("{\"name\":\"zs\",\"age\":19}", XContentType.JSON).get();
    //查询指定数据
    client.prepareGet("myindex","test","1").get();
    //删除指定数据
    client.prepareDelete().setIndex("myindex3").setType("test").setId("1").get();
    //查询
    client.prepareSearch("myindex").get();
    ```
    
  * 方法3：使用restClient进行开发(本次使用es版本为7.6.2)
  
    ```java
        //创建索引
        CreateIndexRequest request = new CreateIndexRequest("simple");
        request.settings(Settings.builder().put("index.number_of_shards", 1)
            .put("index.number_of_replicas", 0));
    
        request.mapping(
            "{\"doc\":{\"properties\":{\"org_id\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\"}}},\"class_name\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\"}}},\"name\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\"}}},\"age\":{\"type\":\"integer\"}}}}",
            XContentType.JSON);
        CreateIndexResponse createIndexResponse = client.indices()
            .create(request, RequestOptions.DEFAULT);
    
    
        UserInfoDTO user = new UserInfoDTO();
        user.setName(UUID.randomUUID().toString());
        user.setAge(13);
        user.setId(99);
    
        System.out.println(JSON.toJSONString(user));
    
        //添加数据
        IndexRequest saveRequest = new IndexRequest("simple");
        saveRequest.source(JSON.toJSONString(user), XContentType.JSON);
    
        IndexResponse saveResponse = client.index(saveRequest, RequestOptions.DEFAULT);
        Result result = saveResponse.getResult();
        System.out.println(result);
    
    
    
        //根据id查询数据
        GetRequest getRequest = new GetRequest("simple", "nb0y9HEBkY-_CmMLWUQh");
        //展示字段
        String[] includes = new String[]{"age", "name"};
        String[] excludes = Strings.EMPTY_ARRAY;
        FetchSourceContext fetchSourceContext = new FetchSourceContext(true, includes, excludes);
        getRequest.fetchSourceContext(fetchSourceContext);
    
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        String sourceAsString = getResponse.getSourceAsString();
        System.out.println(sourceAsString);
    
    
    
        //search查询
        SearchRequest searchRequest = new SearchRequest("simple");
    
    
        SearchSourceBuilder builder = new SearchSourceBuilder();
    
        //匹配查询1
        MatchPhraseQueryBuilder matchPhraseQueryBuilder = QueryBuilders
            .matchPhraseQuery("age", "13");
    
        //匹配查询2
        MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
    
    
        //组装两个查询条件
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.filter(matchPhraseQueryBuilder).must(matchAllQueryBuilder);
    
        builder.query(boolQueryBuilder);
    //    builder.query(matchAllQueryBuilder);
        builder.fetchSource(false); //是否返回source字段
        builder.sort(new FieldSortBuilder("_id").order(SortOrder.ASC));//按照指定顺序排序
    //    builder.from(2); //从哪里开始
    //    builder.size(2); //返回数量
    
    
        builder.fetchSource(new FetchSourceContext(true,new String[]{"name","age","id"},Strings.EMPTY_ARRAY));
    
        searchRequest.source(builder);
    
    
    
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        for (SearchHit hit : searchResponse.getHits()) {
          String id = hit.getId();
          String sdata = hit.getSourceAsString();
          System.out.println(id + sdata);
        }
    ```

