# Redis

--------------

## 什么是Redis

* redis是一个开源的基于内存的KV型数据库。通常用来作为数据库，缓存，或者是消息中间件进行使用，它支持的数据类型有String，hash，list，set，zset

## 为什么使用Redis

* redis可以提供在少数（100以内）连接时12W的QPS,在多数链接（6W）时5W的QPS高性能。同时redis本身在集群模式下可以保证数据的一致性，以及本身对事物的支持和对发布订阅模式的支持可以应用于更多的业务场景。

## 怎么使用Redis

* 安装

  * 单机

    1.  [下载](https://redis.io/download)
    2. 解压 tar -xvf redis-*.tar.gz
    3. 编译make &make test& make install   PREFIX=dir (如果没有安装gcc会报错，使用 yum install gcc)
    4. 整理：将redis.conf和redis-server,redis-cli复制到指定的目录(/usr/local/redis)
    5. 启动服务：/usr/local/redis/bin/redis-server  /user/local/redis/etc/redis.conf
    6. 登陆：/user/lcoal/redis/bin/redis-cli  -h lcoalhost -p 6379

  * 集群

    1. 单机安装

    2. 修改配置文件redis.conf

       ```properties
       #开启集群模式
       cluster-enabled yes
       #设置配置文件
       cluster-config-file nodes9001.conf（9001和port要对应）
       #设置节点超时时间
       cluster-node-timeout 15000
       ```

    3. 安装ruby

       ```shell
       yum install ruby
       yum install rubygems
       gem install reids
       ```

    4. 创建集群

       ```shell
       /usr/local/redis-cluster/bin/redis-trib.rb create --replicas 1 192.168.119.131:9001 192.168.119.131:9002 192.168.119.131:9003 192.168.119.131:9004 192.168.119.131:9005 192.168.119.131:9006
       ```

* 配置

  ```properties
  #常用
  
  #是否后台运行
  daemonize no
  #监听端口
  port 6379
  #client空闲多少秒自动关闭（0就是不管）
  timeout 0
  #制定日志文件的位置
  logfile "/usr/local/log/redis-log.txt"
  #数据库的数量
  database 16
  #工作目录
  dir ./
  #指定 redis 只接收来自于该 IP 地址的请求，如果不进行设置，那么将处理所有请求
  bind 127.0.0.1
  
  #持久化配置
  #rdb模式
  #rdb持久化策略
  save [间隔时间] [写入的此时]
  #是否压缩快照文件
  rdbcompression yes
  #设置快照文件名
  dbfilename dump.rdb
  
  
  #aof模式
  #是否开启aof模式
  appendonly no
  #aof模式下的文件保存名
  appendfilename 'appendonly.aof'
  #aof持久化策略
  appendfsync always 每次写入都执行同步
  appendfsync everysec 每秒执行一次
  apendfsync no  根据系统来进行调度
  #aof持久化文件重写规则
  auto-aof-rewrite-percentage 100
  #设置最小aof文件大小，避免频繁重写
  auto-aof-rewrite-min-size 64mb
  ```

* 常用命令

  ```shell
  #选择数据库
  select [index]
  #查看key
  keys *
  #查看集群状态
  cluster info
  cluster nodes
  #设置过期时间
  expire key seconds
  #查看过期时间
  ttl key
  #publish/subscribe
  publish channelName msg
  subscribe channelName 
  #string类型
  set key value
  del key
  get key
  #hsah类型
  hset key field value
  hget key fueld
  hkeys key
  hdel key field
  #list类型
  lpush key value1 [value2]
  lpop key
  del key
  llen ley
  #set类型
  sadd key memnber
  spop key
  smembers key
  #zset类型
  zadd key member
  zrange key
  ```

## Redis进阶

* 使用java操作redis
  1. jedis

     ```java
     //直连
     Jedis jedis = new Jeids("localhost");
     jedis.set("mhc",mhc);
     String result = jedis.get("mhc");
     Pipelined pipelined = jedis.pipelined();
     pipelined.set("aaa","aaa");
     pipelined.sync();
     
     //连接池
     JedisPool jedisPool = new JedisPool("localhost");
     Jedis jedis = jedisPool.getResource();
     
     //分布式
     ShardedJedisPool shardedJedisPool = new ShardedJedisPool(new GenericObjectPoolConfig(),
                     Arrays.asList( new JedisShardInfo[]{new JedisShardInfo(host)} ));
             ShardedJedis shardedJedis = shardedJedisPool.getResource();
     ```

  2. spring-data-redis

     ```java
     ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
     RedisTemplate template = context.getBean(RedisTemplate.class);
     template.opsForValue().set("wyl","wyl");
     Object wyl = template.opsForValue().get("wyl");
     ```
* 缓存应用遇到的问题
  1. 缓存穿透

     * 缓存穿透是指查询一个一定不存在的数据，这样会导致这个不存在的数据每次都要去存储层查询，当流量大时，DB可能就会宕机。一般常见的解决方案为采用布隆过滤器，将所有可能存在的数据hash到一个足够大的bitmap中，一个不存在的数据会被bitmap过滤掉，这样就可以降低压力。也可以将返回的空数据进行缓存也可以解决。

  2. 缓存击穿

     * 缓存击穿是指当一个数据失效的时候，有大量并发的请求来请求，这样或导致大量并发的请求落到数据库上，可能会导致数据库宕机。这个时候一般采用的是互斥锁方案。在发现缓存失效后，如果要访问DB需要先获得锁，这样在没有获得锁的时候进行等待即可。redis的key不能重复就可以作为互斥的锁的实现基础。

  3. 缓存雪崩

     * 缓存雪崩是指当缓存的失效时间在同一个时间点失效，会导致下一次的请求无法命中而直接访问数据库从而导致DB宕机。可以在设置失效时间的时间加上随机值来处理这种情况。

  4. 缓存更新

     * 缓存更新是指缓存中的数据失效后需要更新的场景，这个时候一般可以采用在DB中存储完成后，删除缓存中的数据或者是设置失效时间，对于并发比较高的场景可以采用异步队列将更新数据放到消息队列中进行消费。


