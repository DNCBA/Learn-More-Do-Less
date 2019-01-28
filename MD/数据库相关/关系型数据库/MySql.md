# MySql

-------------------

## 基本

* 存储引擎
  * 概念：MySQL中的数据用各种不同的技术存储在文件(或者内存)中。这些技术中的每一种技术都使用不同的存储机制、索引技巧、锁定水平并且最终提供广泛的不同的功能和能力。通过选择不同的技术，你能够获得额外的速度或者功能，从而改善你的应用的整体功能。这些不同的技术以及配套的相关功能在 MySQL中被称作存储引擎(也称作表类型)。
  * 常见的存储引擎：
    1. InnoDB：默认的存储引擎。支持事物，容灾恢复，外键约束。
    2. MyISAM：不支持事物，和外键，但是访问速度快。
    3. MEMORY：数据存放在内存中，性能好
    4. ARCHIVE：比较好的压缩机制，可以作为仓库
  * 事物
    * ACID
      1. 隔离性：隔离状态执行事务，使它们好像是系统在给定时间内执行的唯一操作
      2. 持久性：在事务完成以后，该事务对数据库所作的更改便持久的保存在数据库之中，并不会被回滚。
      3. 原子性：整个事务中的所有操作，要么全部完成，要么全部不完成，不可能停滞在中间某个环节。
      4. 一致性：一个事务可以封装状态改变（除非它是一个只读的）。事务必须始终保持系统处于一致的状态，不管在任何给定的时间[**并发**](https://baike.baidu.com/item/%E5%B9%B6%E5%8F%91)事务有多少。
    * 隔离级别
      * 读未提交，不可重复读，可重复读，串行化。
    * 事物并发产生的问题
      1. 脏读：事物1读取到了事物2未提交的数据，在事物2回滚的时候，1读到脏数据
      2. 不可重复读：事物1读到事物2对数据的修改。这样导致多次读取的值不一样
      3. 幻读：在事物1读取到了事物2添加或删除的操作

## 高性能

* 读写分离

  * 概念：读写分离的场景是为了缓解，高并发读取，小部分写入的场景下的性能问题。采用的是mysql的主从复制来实现

  * 操作：

    * 配置master

      1. 创建同步用户

         ```sql
         create user rel1; //创建用户
         GRANT REPLICATION SLAVE ON *.* TO 'repl'@'192.168.0.%' IDENTIFIED BY 'mysql'; //配置权限
         ```

      2. 修改my.ini配置

         ```ini
         server-id=xxx  //服务id，全局唯一
         log-bin=mysql-bin   //启用二进制日志文件 
         ```

      3. 重启服务：systemct1 restart mysqld

      4. 查看状态：show master status

    * 配置slave

      1. 修改my.ini配置

         ```ini
         server-id=xxx
         relay-log-index=slave-relay-bin.index
         relay-log=slave-relay-bin
         ```

      2. 重启服务：systemct1 restart mysqld

      3. 建立同步链接

         ```sql
         change master to master_host='192.168.0.1', //Master 服务器Ip
         master_port=3306,
         master_user='root',
         master_password='root', 
         master_log_file='master-bin.000001',//Master服务器产生的日志
         master_log_pos=0;
         ```

      4. 启动slave：start slave

      5. 查看状态：show slave status

* 分库分表

  * 垂直拆分
    * 垂直分库：解决表过多的问题，可以将有关系的表放到一个数据库中
    * 垂直分表：解决的是表列过多的问题
  * 水平拆分
    * 解决的是单表数据量过大问题

## Mycat

* 安装

  1. [下载](http://dl.mycat.io/1.6-RELEASE/)

  2. 解压

     ```shell
     tar -xzvf Mycat-1.***.tar.gz
     ```

  3. 启动

     ```shell
     sh mycat start
     sh mycat stop
     sh mycat restart
     ```

* 配置

  * server.xml：配置用户相关信息

    ```xml
    <user name="root">
        <property name="password">123456</property>
        <property name="schemas">TESTDB</property>
    
        <!-- 表级 DML 权限设置 -->
        <!-- 		
      <privileges check="false">
       <schema name="TESTDB" dml="0110" >
        <table name="tb01" dml="0000"></table>
        <table name="tb02" dml="1111"></table>
       </schema>
      </privileges>		
       -->
    </user>
    ```

  * schema.xml：配置逻辑表相关

    ```xml
    <schema name="TESTDB" checkSQLschema="false" sqlMaxLimit="100">   //逻辑数据库
        //逻辑表水平拆分 （dataNode：表示数据库源，rule：表示水平拆分规则）          
        <table name="travelrecord" dataNode="dn1,dn2,dn3" rule="auto-sharding-long" />  
        //全局表
        <table name="company" primaryKey="ID" type="global" dataNode="dn1,dn2,dn3" />
    	//关联子表
        <table name="customer" primaryKey="ID" dataNode="dn1,dn2"
               rule="sharding-by-intfile">
            <childTable name="orders" primaryKey="ID" joinKey="customer_id"
                        parentKey="id">
                <childTable name="order_items" joinKey="order_id"
                            parentKey="id" />
            </childTable>
            <childTable name="customer_addr" primaryKey="ID" joinKey="customer_id"
                        parentKey="id" />
        </table>
    
    </schema>
    //数据源配置
    <dataNode name="dn1" dataHost="localhost1" database="db1" />
    <dataNode name="dn2" dataHost="localhost1" database="db2" />
    <dataNode name="dn3" dataHost="localhost1" database="db3" />
    
    <dataHost name="localhost1" maxCon="1000" minCon="10" balance="0"
              writeType="0" dbType="mysql" dbDriver="native" switchType="1"  slaveThreshold="100">
        <heartbeat>select user()</heartbeat>
        //读写分离
        <writeHost host="hostM1" url="localhost:3306" user="root"
                   password="123456">
            <readHost host="hostS2" url="192.168.1.200:3306" user="root" password="xxx" />
        </writeHost>
        //高可用
        <writeHost host="hostS1" url="localhost:3316" user="root"
                   password="123456" />
    </dataHost>
    
    ```



