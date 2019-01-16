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

  * LevelDB，MQ自身提供的持久化引擎

* ActiveMQ消息签收和分发策略

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

  * 分发策略

    * topic
      1. 轮询：按照订阅的顺序，依次发送给订阅者，但是会在第二次发送时反转顺序
      2. 有序：按照订阅的顺序进行发送
      3. 权重：根据权重进行发送
      4. 默认：按照订阅者列表的顺序进行发送
    * queue

* ActiveMQ事物相关

  * 可以在创建session的时候指定开启事务，在开启之后需要手动的调用session.commit进行事物的提交。在发生异常的时候及时使用session.rolback进行回滚


