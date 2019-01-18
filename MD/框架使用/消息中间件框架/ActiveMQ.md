# ActiveMQ

----------------

## 什么是ActiveMQ

* ActiveMQ是消息中间件的一种，所谓的消息中间件就是提供一种类似于异步解耦的作用在系统中，主要核心通过三部分来组成：生产者，消费者，消息。生产者来生产消息并发送到对应的队列或者是topic中，然后消费者在对应的队列或者是topic中进行消费。

## 为什么使用ActiveMQ

* ActiveMQ作为apache下的一个开源项目，不仅提供了一个高性能的MQ中间件，而且该中间件还支持跨语言（Java,C,C++,C#,Python,PHP）。另外一个选择它的理由是ActiveMQ完全支持JMS,这样在使用的时候提供了极大的便利。

## 怎么使用ActiveMQ

* 安装ActiveMQ

  1. [下载](http://activemq.apache.org/download-archives.html)
  2. 安装，解压下载的tar包
  3. 运行 直接运行activemq/bin/activemq脚本
  4. 访问控制台localhost:8161

* 集群模式

  * 高性能解决方案(networkconnection)

    1. 修改activeMQ.xml

       ```xml
       <networkConnectors>
           <!-- by default just auto discover the other brokers -->
           <networkConnector name="default-nc" uri="multicast://default"/> //动态链接
           <!-- Example of a static configuration:      //静态链接
                   <networkConnector name="host1 and host2" uri="static://(tcp://host1:61616,tcp://host2:61616)"/>
                   -->
       </networkConnectors>
       ```

    2. 重启服务

       * 这个时候不同的broker之间可以共同处理消息，但是有个问题是会出现消息丢失，这个时候就要设置消息回流

    3. 消息回流

       ```xml
       <policyEntry queue=">" enableAudit="false">
           <networkBridgeFilterFactory>
               <conditionalNetworkBridgeFilterFactory replayWhenNoConsumers="true"/>
           </networkBridgeFilterFactory>
       </policyEntry>
       ```

  * 高可用解决方案(zookeeper+levelDB)

    1. 修改配置文件

       ```xml
       <persistenceAdapter>
           <replicatedLevelDB
       		directory="activemq-data"     //数据存储地址
               replicas="3"				//集群中至少有replicas/2 +1 台机器存活
               bind="tcp://0.0.0.0:0"		//相互通讯的
               zkAddress="zoo1.example.org:2181,zoo2.example.org:2181,zoo3.example.org:2181" //zk
               zkPassword="password"     //zk密码
               zkPath="/activemq/leveldb-stores"   //zk路径
               hostname="broker1.example.org"   //name
                              />
       </persistenceAdapter>
       ```

    2. 原理

       * 通过在zk上注册临时有序节点，来选择最小的节点作为master对外提供服务，当master宕机之后，重新选举最小的节点对外提供服务

* JMS操作ActiveMQ

  * queue模式

    * 生产者

      ```java
      String host = "tcp://localhost:61616";
      ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(host);
      Connection connection = connectionFactory.createConnection();
      connection.start();
      Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
      Queue queue = session.createQueue(queueName);
      MessageProducer producer = session.createProducer(queue);
      String message = "hello ActiveMQ" + UUID.randomUUID().toString();
      System.out.println("send message:" + message);
      producer.send(session.createTextMessage(message));
      ```

    * 消费者

      ```java
      String host = "tcp://localhost:61616";
      ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(host);
      Connection connection = connectionFactory.createConnection();
      connection.start();
      Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
      Queue queue = session.createQueue(queueName);
      MessageConsumer consumer = session.createConsumer(queue);
      consumer.setMessageListener((message -> {
          System.out.println(message);
      }));
      ```

  * topic模式

    * 生产者

      ```java
      String host = "tcp://localhost:61616";
      ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(host);
      Connection connection = connectionFactory.createConnection();
      connection.start();
      Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
      Topic topic = session.createTopic(topicName);
      MessageProducer producer = session.createProducer(topic);
      String message = "hello ActiveMQ" + UUID.randomUUID().toString();
      System.out.println("send message:" + message);
      producer.send(session.createTextMessage(message));
      ```

    * 消费者

      ```java
      String host = "tcp://localhost:61616";
      ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER,ActiveMQConnection.DEFAULT_PASSWORD,host);
      Connection connection = connectionFactory.createConnection();
      connection.start();
      Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
      Topic topic = session.createTopic(topicName);
      MessageConsumer consumer = session.createConsumer(topic);
      consumer.setMessageListener((message -> {
          System.out.println(message);
      }));
      ```

## ActiveMQ进阶

* ActiveMQ持久化策略

  * JDBC会将消息持久化到数据库中，将数据存储在三个表中activemq-msgs/acks/lock

    * 在conf/activemq.xml文件中修改

      ```xml
      <beans>
          <broker brokerName="test-broker" persistent="true" xmlns="http://activemq.apache.org/schema/core">
              <persistenceAdapter>
                  <jdbcPersistenceAdapter dataSource="#mysql-ds" createTablesOnStartup="false"/>
              </persistenceAdapter>
          </broker>
          <bean id="mysql-ds" class="org.apache.commons.dbcp.BasicDataSource" destroy-method="close">
              <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
              <property name="url" value="jdbc:mysql://localhost/activemq?relaxAutoCommit=true"/>
              <property name="username" value="activemq"/>
              <property name="password" value="activemq"/>
              <property name="maxActive" value="200"/>
              <property name="poolPreparedStatements" value="true"/>
          </bean>
      </beans>
      ```

  * AMQ将消息写入到日志文件中

    ```xml
    <persistenceAdapter>
        <amqPersistenceAdapter directory="${activemq.data}/activemq-data" maxFileLength="32mb"/>
    </persistenceAdapter>
    ```

  * LevelDB

    * 集群模式下推荐使用的持久化策略

  * kahaDB

    * 默认的持久化策略，基于文件系统的

* ActiveMQ消息签收和分发策略

  * 订阅策略

    * 持久订阅

      * 持久订阅的消费者在，离线状态下也不会丢失这段时间的消息，当消费者再次上线的时候MQ会主动将这段时间的消息进行推送。

        ```java
        connection.setClientID("clientId");  //首先需要设置id
        Session session =  connection.createSession();
        Topic topic = session.createTopic("topicName");
        //设置持久订阅
        MessageConsumer consumer = session.createDurableSubscriber(topic,"clientId"); 
        ```

    * 非持久订阅

      * 非持久订阅的消费者，如果在离线状态下就会丢失这段时间的消息。这是默认的p/s订阅策略

        ```java
        connection.setClientID("clientId");  //首先需要设置id
        Session session =  connection.createSession();
        Topic topic = session.createTopic("topicName");
        //普通订阅
        MessageConsumer consumer = session.createConsumer(topic); 
        ```

  * 签收策略

    ```java
    //自动签收，当recive方法结束或者onMessage方法结束的时候就自动签收
    Session.AUTO_ACKNOWLEDGE;   
    //手动签收，调用message的acknowledge()方法就可以签收当前session消费的数据
    Session.CLIENT_ACKNOWLEDGE;  
    //批量签收，当接受到一定量数据后可以进行签收
    Session.DUPS_OK_ACKNOWLEDGE;
    //当事物提交的时候会自动提交
    Session.SESSION_TRANSACTED;
    ```

  * Ack_Type

    ```java
    DELIVERED_ACK_TYPE = 0    //消息"已接收"，但尚未处理结束
    STANDARD_ACK_TYPE = 2    //"标准"类型,通常表示为消息"处理成功"，broker端可以删除消息了
    POSION_ACK_TYPE = 1    //消息"错误",通常表示"抛弃"此消息
    REDELIVERED_ACK_TYPE = 3   // 消息需"重发"
    INDIVIDUAL_ACK_TYPE = 4   // 表示只确认"单条消息",无论在任何ACK_MODE下    
    UNMATCHED_ACK_TYPE = 5   // BROKER间转发消息时,接收端"拒绝"消息
    ```

  * 分发策略

    * topic
      1. 轮询：按照订阅的顺序，依次发送给订阅者，但是会在第二次发送时反转顺序
      2. 有序：按照订阅的顺序进行发送
      3. 权重：根据权重进行发送
      4. 默认：按照订阅者列表的顺序进行发送

* ActiveMQ事物相关

  * 可以在创建session的时候指定开启事务，在开启之后需要手动的调用session.commit进行事物的提交。在发生异常的时候及时使用session.rolback进行回滚


