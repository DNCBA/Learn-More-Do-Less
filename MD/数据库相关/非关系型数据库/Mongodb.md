# Mongodb

-------

## 是么是Mongodb

* mongodb是一个nosql型数据库，也是一个开源文档数据库，提供高性能，高可用性和自动扩展

## 为什么使用Mongodb

* 相比较于关系型数据库有更高的性能，相较于其他的nosql数据库提供丰富的查询语言，同时他支持多个存储引擎，具有高可用，和水平可伸缩性

## 怎么使用Mongodb

* 安装

  1. 下载，解压

  2. 启动

     ```shell
     ./mongod --dbpath /data/dir     //启动
     ./mongo localhost:27017         //连接mongodb
     ```

* 核心概念

  |       mysql       |     mongodb      |
  | :---------------: | :--------------: |
  | 数据库(database)  | 数据库(database) |
  |     表(table)     | 集合(collection) |
  |      行(row)      |  文档(document)  |
  |    列(columm)     |   字段(field)    |
  | 主键(primary key) |  id(object id)   |
  |    索引(index)    |   索引(index)    |

* 常见shell命令

  |      含义       |                     命令                     |
  | :-------------: | :------------------------------------------: |
  | 创建/删除数据库 |       use "db-name"/db.dropDatabase()        |
  |  创建/删除集合  | db.createCollection("c-name")/db.c-name.drop |
  |    添加数据     |       db.c-name.insert({json:"json"})        |
  |    更新数据     |       db.c-name.update(query，update)        |
  |    删除数据     |          db.c-name.remove({match})           |
  |    查询数据     |               db.c-name.find()               |
  | 展示数据库/集合 |         show dbs/show dropDatebase()         |

## Mongodb进阶

* 使用java操作

  1. 原生api

     ```java
     MongoClient client = new MongoClient(host,port);
     //database
     MongoDatabase database = client.getDatabase("test");
     //client.getDatabase("test").drop();
     
     //collection
     MongoCollection<Document> collection = database.getCollection("test");
     database.createCollection("c-name");
     database.getCollection("c-name").drop();
     
     //document
     collection.insertOne(Document.parse("{\"age\":13,\"address\":\"hz\"}"));
     //collection.updateOne();
     FindIterable<Document> documents = collection.find();
     for (Document document : documents){
         System.out.println(document);
     }
     //collection.deleteOne(BsonDocument.parse(""));
     ```

  2. spring-data-mongodb

     1. 组装MongoTemplate

        ```xml
        <bean id="mongoClient" class="com.mongodb.MongoClient">
            <constructor-arg name="host" value="localhost"></constructor-arg>
            <constructor-arg name="port" value="27017"></constructor-arg>
        </bean>
        
        <bean id="mongoDbFactory" class="org.springframework.data.mongodb.core.SimpleMongoDbFactory">
            <constructor-arg name="mongoClient" ref="mongoClient"></constructor-arg>
            <constructor-arg name="databaseName" value="test"></constructor-arg>
        </bean>
        
        <bean id="mongodbTemplate" class="org.springframework.data.mongodb.core.MongoTemplate">
            <constructor-arg ref="mongoDbFactory"></constructor-arg>
        </bean>
        ```

     2. 使用template

        ```java
        FindIterable<Document> documents = mongoTemplate.getCollection("test").find();
        
        for (Document document : documents){
            System.out.println(document);
        }
        ```

        

