# Kafka

## 什么是kafka

* kafka是一个分布式流媒体平台，它具有发布订阅记录流，类似于消息队列或企业消息传递系统。以容错的持久方式存储记录流。记录发生时处理流。

## 为什么使用kafka

* 高性能，相比ActiveMQ来说性能可以提高5-10倍，并且特殊的存储机制可以保证高容错性。以及消息持久存储的便利性。

## 怎么使用kafka

* 安装

  1. [下载](https://www.apache.org/dyn/closer.cgi?path=/kafka/2.1.0/kafka_2.11-2.1.0.tgz)

  2. 解压 : tar -xzvf kafka_2.11.tgz

  3. 配置 : 

     * 修改/config/server.properties

       ```properties
       broker.id=0  //节点id
       listeners=PLAINTEXT://:9092    //监听端口
       advertised.listeners=PLAINTEXT://114.116.67.84:9092     //绑定地址
       zookeeper.connect=localhost:2181   //zk地址
       ```

  4. 启动

     ```shell
     #启动服务
     sh kafka-server-start.sh /config/dir
     #创建topic
     sh kafka--topic.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test
     #查看topic
     sh kafka-topics.sh --list --zookeeper localhost:2181
     #控制台生产者
     sh kafka-console-producer.sh --broker-list localhost:9092 --topic test
     #控制台消费者
     sh kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test --from-beginning
     ```

* kafka核心概念

  * topic

    * topic对于kafka是一个逻辑分区，其实在硬盘上存储的是将topic分别存储在partation中，数据真实存储的地方是partation中,数据的存储流程大致为：

      1. 生产者发送数据到kafka
      2. kafka接收到消息后，根据路由策略将选择消息存储在哪个partation中
      3. 确定后将数据追加到指定partation后完成存储

      ![topic](http://kafka.apache.org/20/images/log_anatomy.png)

  * partation

    * partation是kafka存储数据的真正位置，也是kafka高性能的秘密之一。为了克服磁盘io的瓶颈，kafka通过顺序写的操作来提高数据的写入速率，因此partation的写入方式就是追加顺序写。通过记录offset来确定消费的消息

    ![partation](http://kafka.apache.org/20/images/log_consumer.png)

* java API操作

  1. provider

     ```java
     Map<String, Object> configs = new HashMap<>();
     //设置brokerlist
     configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"114.116.67.84:9092");
     //设置keyvalue的序列化方式
     configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
     configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
     //设置ack。0代表不用等到leader返回ack，1代表会等待leader返回ack，-1代表等到所有的broker同步到消息后才返回ack
     configs.put(ProducerConfig.ACKS_CONFIG, "all");
     //设置clientid
     configs.put(ProducerConfig.CLIENT_ID_CONFIG,"productor1");
     //设置batchsize
     configs.put(ProducerConfig.BATCH_SIZE_CONFIG,"1");
     //设置延迟
     configs.put(ProducerConfig.LINGER_MS_CONFIG,"1");
     //设置buffer大小
     configs.put(ProducerConfig.BUFFER_MEMORY_CONFIG,"10240");
     //设置请求和发送的超时时间
     configs.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG,"20000");
     configs.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG,"30000");
     KafkaProducer producer = new KafkaProducer(configs);
     for (int i=0;i<10;i++) {
         String result = UUID.randomUUID().toString();
         String topic = "test";
         //ProducerRecord对象有多种构造方式，可以根据需要选择
         producer.send(new ProducerRecord(topic,result));
         System.out.println("send message : "+ result + ",to TOPIC ： " + topic );
         TimeUnit.SECONDS.sleep(2);
     }
     producer.close();
     ```

  2. consumer

     ```java
     Properties config = new Properties();
     //设置brokerlist
     config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"114.116.67.84:9092");
     //设置反序列化配置
     config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
     config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class.getName());
     //设置groupid
     config.put(ConsumerConfig.GROUP_ID_CONFIG,"consumer1");
     //设置是否自动提交
     config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,"true");
     //设置自动提交的时间配置
     config.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG,"100");
     KafkaConsumer consumer = new KafkaConsumer(config);
     consumer.subscribe(Arrays.asList("test"));
     ConsumerRecords<String,String> consumerRecords = consumer.poll(Duration.ofDays(1));
     for (ConsumerRecord<String,String> record : consumerRecords){
         System.out.println("recive data key: " + record.key()+",value:" + record.value());
     }
     ```

  3. adminCli

     ```java
     Properties config = new Properties();
     //设置brokerlist
     config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.0.185:9092");
     //设置clientid
     config.put(AdminClientConfig.CLIENT_ID_CONFIG,"adminClient1");
     AdminClient adminClient = AdminClient.create(config);
     //创建topic
     adminClient.createTopics(Arrays.asList(new NewTopic("topic1", 4, (short) 1)));
     //删除topic
     adminClient.deleteTopics(Arrays.asList("topic1"));
     //获得topic列表
     Set<String> topics = adminClient.listTopics().names().get();
     ```
