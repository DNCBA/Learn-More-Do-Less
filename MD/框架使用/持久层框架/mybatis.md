# mybatis框架

-------

## 什么是mybatis

* 它支持定制化 SQL、存储过程以及高级映射。MyBatis 避免了几乎所有的 JDBC 代码和手动设置参数以及获取结果集。MyBatis 可以使用简单的 XML 或注解来配置和映射原生信息，将接口和 Java 的 POJOs(Plain Old Java Objects,普通的 Java对象)映射成数据库中的记录。

## 为什么使用mybatis

* 相对于原生JDBC而言使用mybatis可以简化大部分的重复编码，相对于hibernate（ORM）框架而言mybatis更加轻量，更加容易进行调整，也拥有更加快的速度。在出现问题的时候也更容易排查。总而言之在追求效率和快速变化的业务场景下，选择mybatis可以给你带来更好的开发体验。

## 怎么使用mybatis

* 单独使用mybatis

  1. 创建一个普通的java工程

  2. 添加mybatis的依赖，和对应的数据库连接驱动

     ```xml
     <dependency>
         <groupId>org.mybatis</groupId>
         <artifactId>mybatis</artifactId>
         <version>3.4.6</version>
     </dependency>
     <dependency>
         <groupId>mysql</groupId>
         <artifactId>mysql-connector-java</artifactId>
         <version>5.1.38</version>
     </dependency>
     ```

  3. 创建对应的实体和表，并且保持字段名一致

     ```java
     public class User{
         private Integer userId;
         private String userName;
         private String password;
     }
     ```

  4. 编写基本的mybatis-config.xml

     ```xml
     <configuration>
         <environments default="development">
             <environment id="development">
                 <transactionManager type="JDBC"></transactionManager>
                 <dataSource type="POOLED">
                     <property name="driver" value="com.mysql.jdbc.Driver" />
                     <property name="url" value="jdbc:mysql://localhost:3306/mybatis" />
                     <property name="username" value="root" />
                     <property name="password" value="123456" />
                 </dataSource>
             </environment>
         </environments>
         <mappers>
             <mapper class="com.mhc.mapper.UserMapper"></mapper>
         </mappers>
        
     </configuration>
     ```

  5. 根据对应的实体编写XXXMapping.xml和XXXMapping接口

     * XXXMapping接口

       ```java
       public interface UserMapper {
       
               void add(User user);
       
               Integer deleteById(Integer userId);
       
               Integer update(User user);
       
               List<User> findAll();
       
       }
       ```

     * XXXMapping.xml

       ```xml
       <mapper namespace="com.mhc.mapper.UserMapper">
       
           <insert id="add" parameterType="com.mhc.domain.User">
               insert into 'user' values (null ,#{userName},#{password})
           </insert>
       
           <delete id="deleteById" parameterType="Integer">
               delete from 'user' where userId = #{userId}
           </delete>
       
           <update id="update" parameterType="com.mhc.domain.User">
               update 'user' set userName = #{userName} and password = #{password} where userId = #{userId}
           </update>
       
           <select id="findAll" resultType="com.mhc.domain.User">
               select * from  'user';
           </select>
       
       </mapper>
       ```

  6. 在程序中使用SqlSessionFactoryBuilder来构造一个SqlSessionFactory对象

     ```java
     FileInputStream inputStream = new FileInputStream(new File("mybatis-conf.xml"))；
         
     SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().Build(inputStram)；
     ```

  7. 使用SqlSessionFactory构造对应的SqlSession来获得对应的Mapper

     ```java
     SqlSession sqlSession = sqlSessionFactory.openSession()；
     
     UserMapper mapper = sqlSession.getMapper(UserMapper.class);
     ```

  8. 使用Mapper进行对应的操作

     ```java
     mapper.add(user);
     
     Integer result = mapper.deleteById(1)；
     
     result = mapper.update(user);
     
     List<User> list = mapper.findAll()；
     
     sqlSession.commit()；
     ```

* 使用spring整合mybatis

  1. 创建一个Maven工程

  2. 添加spring和mybatis的相关依赖

     ```xml
     <dependency>
         <groupId>org.springframework</groupId>
         <artifactId>spring-context</artifactId>
         <version>${spring.version}</version>
     </dependency>
     <dependency>
         <groupId>org.springframework</groupId>
         <artifactId>spring-jdbc</artifactId>
         <version>${spring.version}</version>
     </dependency>
     
     <dependency>
         <groupId>org.mybatis</groupId>
         <artifactId>mybatis</artifactId>
         <version>3.4.6</version>
     </dependency>
     <dependency>
         <groupId>org.mybatis</groupId>
         <artifactId>mybatis-spring</artifactId>
         <version>1.3.1</version>
     </dependency>
     <dependency>
         <groupId>mysql</groupId>
         <artifactId>mysql-connector-java</artifactId>
         <version>5.1.38</version>
     </dependency>
     <dependency>
         <groupId>com.mchange</groupId>
         <artifactId>c3p0</artifactId>
         <version>0.9.5.2</version>
     </dependency>
     ```

  3. 编写spring的配置文件

     ```xml
     <!-- 配置数据源 -->
     <bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource" destroy-method="close">
         <property name="driverClass" value="com.mysql.jdbc.Driver" />
         <property name="jdbcUrl" value= "jdbc:mysql://localhost:3306/mybatis?characterEncoding=UTF-8" />
         <property name="user" value="root" />
         <property name="password" value="354710644" />
         <property name="maxPoolSize" value="10" />
         <property name="maxIdleTime" value="5" />
     </bean>
     
     <!-- 配置sqlSessionFactory，SqlSessionFactoryBean是用来产生sqlSessionFactory的 -->
     <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
         <!-- 加载mybatis的全局配置文件，放在classpath下的mybatis文件夹中了 -->
         <property name="configLocation" value="classpath:SqlMapConfig.xml" />
         <!-- 加载数据源，使用上面配置好的数据源 -->
         <property name="dataSource" ref="dataSource" />
     </bean>
     
     <tx:annotation-driven></tx:annotation-driven>
     
     <bean id="transactionManager"
           class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
         <property name="dataSource" ref="dataSource" />
     </bean>
     
     <!--mapper代理开发-->
     <bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
         <property name="basePackage" value="com.huawei.mapper" />
         <property name="sqlSessionFactory"  ref="sqlSessionFactory"/>
     </bean>
     ```

  4. 通过注入mapper对象进行操作

## mybatis进阶

* mybatis反编译插件

  1. 在pom中添加mybatis-generator插件

     ```xml
     <!--generator插件-->
     <plugin>
         <groupId>org.mybatis.generator</groupId>
         <artifactId>mybatis-generator-maven-plugin</artifactId>
         <version>1.3.2</version>
         <configuration>
             <verbose>true</verbose>
             <overwrite>true</overwrite>
         </configuration>
     </plugin>
     ```

  2. 编写generatorConfig.xml

     ```xml
     <!--数据库驱动-->
     <classPathEntry    location="D:\AppData\Respository\mysql\mysql-connector-java\5.1.2\mysql-connector-java-5.1.2.jar"/>
     <context id="MySqlDB"    targetRuntime="MyBatis3">
         <commentGenerator>
             <property name="suppressDate" value="true"/>
             <property name="suppressAllComments" value="true"/>
         </commentGenerator>
         <!--数据库链接地址账号密码-->
         <jdbcConnection driverClass="com.mysql.jdbc.Driver"
                         connectionURL="jdbc:mysql://localhost:3306/mybatis"
                         userId="root"
                         password="354710644">
         </jdbcConnection>
         <javaTypeResolver>
             <property name="forceBigDecimals" value="false"/>
         </javaTypeResolver>
         <!--生成Model类存放位置-->
         <javaModelGenerator targetPackage="com.mhc.domain" targetProject="src/main/java">
             <property name="enableSubPackages" value="true"/>
             <property name="trimStrings" value="true"/>
         </javaModelGenerator>
         <!--生成映射文件存放位置-->
         <sqlMapGenerator targetPackage="com.mhc.mapper" targetProject="src/main/resources">
             <property name="enableSubPackages" value="true"/>
         </sqlMapGenerator>
         <!--生成mapper类存放位置-->
         <javaClientGenerator type="XMLMAPPER" targetPackage="com.mhc.mapper" targetProject="src/main/java">
             <property name="enableSubPackages" value="true"/>
         </javaClientGenerator>
         <!--生成对应表及类名-->
         <table tableName="user"></table>
     </context>
     ```

  3. 运行插件即可反编译生成实体类和对应的mapper文件

* mybatis数据传递

  * 有两种方式进行数据的传递

    1. #{attribute}这个方式传递的数据是通过预编译的（传递的参数会被‘’进行标注，同时会被转换成字符串）

       - 在传递基本数据类型以及基本数据类型包装类的时候attribute可以任意填写
       - 在传递POJO类型数据的时候attribute应该为属性名
       - 在传递map集合的时候attribute应该为key的值

    2. ${attribute}这个方式传递的数据是直接传递的（传递的时候不会被转换，在order By和like的时候会用到）

       - 在传递基本数据类型以及基本数据类型包装类的时候attribute只能为value
       - 在传递POJO类型数据的时候attribute应该为属性名

       - 在传递map集合的时候attribute应该为key的值

* springboot整合mybatis

  * 在项目的以来中添加mybatis

    ```xml
    <!-- Spring Boot Mybatis 依赖 -->
    <dependency>
        <groupId>org.mybatis.spring.boot</groupId>
        <artifactId>mybatis-spring-boot-starter</artifactId>
        <version>${mybatis-spring-boot}</version>
    </dependency>
    ```

  * 在启动类上添加注解

    ```java
    @MapperScan("com.mhc.mapper")
    ```

  * 这样就可以使用spring注入对应mapper进行使用

* 手动编写动态sql

  * mybatis在映射的配置文件中提供了对应的几种标签供开发者使用

    ```xml
    <select id="selectPerson" parameterType="int" resultType="hashmap">
      SELECT * FROM PERSON WHERE ID = #{id}
    </select>
    <insert id="insertAuthor" parameterType="domain.blog.Author">
    	INSERT INTO 'user' VALUES (null,'zs','123')
    </insert>
    <update id ="update" parameterType="domain.user">
    	UPDATE 'user' SET userName = #{userName} where userId = #{userId} 
    </update>
    <delete id = "deleteById" parameterType = "Integer">
        Delete from 'user' where userId = #{userId}
    </delete>
    ```

  * 在这些基础的标签之上，mybatis还提供了其他的标签

    ```xml
    <！- if语句会根据表达式的内容，判断是否添加标签内sql -->
    <if test="表达式">
    	语句
    </if>  
    
    
    <！- where会自动去掉语句开头的AND和OR并且还能保证至少有一个条件的时候才拼接 -->
    <where>
    	<if />
        <if />
        <if />
    </where>
        
    <！- foreach用来循环集合数据，并且可以指定开头和结尾，以及分隔符，并提供索引和key来使用-->
    <foreach item="item" index="index" collection="list"
                  open="(" separator="," close=")">
             #{item}
    </foreach>
    ```

* 自定义sql的注解方式

  * 用户可以直接在mapper接口的方法上添加@Insert(sql),@Update(sql),@Delete(sql),@Select(sql)

* 主键返回

  * 可以在insert的标签中添加

    ```xml
    <selectKey keyProperty="userId" resultType="Integer" order"AFTER">
    	select last_insert_id()
    </selectKey>
    ```

  * 返回的主键会直接赋值给保存的数据

* 复杂条件查询

  * and关系
    * 使用Exemple.createCriteria()
  * or关系
    * 使用Exemple.or()
