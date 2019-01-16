# ElasticSearch

--------------------

## 什么是ElasticSearch

* elasticsearch是一个开源的全文搜索分析引擎，它允许您快速，近实时地存储，搜索和分析大量数据。它通常用作底层引擎/技术，为具有复杂搜索功能和要求的应用程序提供支持。

## 为什么使用ElasticSearch

* elasticsearch在数据源不断增加的情况下，性能依旧非常好。另外其本身也支持分布式集群部署。

## 如何使用ElasticSearch

* 安装

  1. [下载](https://www.elastic.co/downloads/elasticsearch)

  2. 将下载好的压缩包进行解压

  3. 运行/bin/elasticsearch

     问题：

      1. 不能以root用户运行：

         ```shell
         useradd test   //添加用户
         passwd test   //设置密码
         su - test     //切换用户
         ```

     	2. io异常：

         ```shell
         chown test /xx/xx/elasticsearch -R
         ```

  4. 不能外网访问

     修改/config/elasticsearch.yml

     ```yaml
     network.host:0.0.0.0   //
     ```

  5. 访问ip：9200即可看到节点信息

* 使用rest API

  1. restfull风格

     | 请求方式 | 含义 |
     | :------: | :--: |
     |   GET    | 查询 |
     |   POST   | 修改 |
     |  DELETE  | 删除 |
     |   PUT    | 增加 |

  2. 请求规则

     localhost:9200/索引名/文档类型/id

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

     添加数据

     ```json
     localhost:9200/索引名/文档名    POST
     {
         "name":"tom",
         "age":14,
         "currentDate":"2018-09-01"
     }
     
     ```

     查询数据

     ```json
     localhost:9200/索引名/文档名/_search   GET
     ```

     删除索引

     ```json
     localhost:9200/索引名     DELETE
     ```

* 使用java操作

## ElasticSearch进阶

* 使用spring-data-es开发
* 高级查询规则



