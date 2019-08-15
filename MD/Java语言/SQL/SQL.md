# SQL-1999

-----------------

## 什么是SQL

* sql概念：*结构化查询语言*(Structured Query Language)简称‘SQL‘
* sql分类：
  * DML:数据操作语言(CRUD)
  * DDL:数据定义语言
* 登录命令：
  * mysql -uusername -ppassword -hhost -Ddatabase

# SQL常见语法

* 基本操作

  | 编号 |   功能   |                         语句                         |
  | :--: | :------: | :--------------------------------------------------: |
  |  1   |  insert  |   insert into tableName values (att1,att2,att3..)    |
  |  2   |  delete  |                delete from tableName                 |
  |  3   |  update  |           update tableName set att = value           |
  |  4   |  select  |          select att1,att2,.. from tableName          |
  |  5   | distinct |         select distinct att1 from tableName          |
  |  6   | order by | select * from tableName order by att1 asc ,att2 desc |

* 查询进阶

  * 子查询

    1. select子查询

       * 将查询出来的结果作为查询条件再次进行查询

         ```sql
         select (select att1 from table1) from table2;
         ```

    2. from子查询

       * 将查询出来的结果作为数据进行二次查询

         ```sql
         select * from (select * from table1 where att1 = value1 );
         ```

    3. where子查询

       * 查询出来的结果作为查询条件再次进行查询

         ```sql
         select * from table1 where att1 = (select distinct att1 from table2 where att2 = value2) and att2 in (select att1 from table3)
         ```

  * 关联查询

    1. 内连接

       ```sql
       select t1.att1 , t2.att1 from table1 t1 inner join table2 t2 where t1.att2 = t2.att2 
       ```

    2. 外连接查询

       ```sql
       
       select t1.att1 , t2.att1 from table1 t1 left join table2 t2 on t1.att2 = t2.att2 ;
       
       select t1.att1 , t2.att1 from table1 t1 right join table2 t2 on t1.att2 = t2.att2 ;
       ```

    3. 交叉连接

       ```sql
       select t1.att1 , t2.att1 from table1 t1 cross join table2 t2 where t1.att2 = t2.att2 	
       ```

# Sql 进阶

* 数据库管理

  1. 用户管理

     | 功能     | 语句                                                         | 参数                                                         |
     | -------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
     | 创建用户 | CREATE USER 'username'@'host' IDENTIFIED BY 'password'       | host：代表用户可以从哪个 IP 登录。% 为通配符代表所有都可以。 |
     | 用户授权 | GRANT privileges ON databasename.tablename TO 'username'@'host' | privileges:代表操作权限( select,insert,update,all )<br />databasename/tablename:代表数据库和表明,可以用 * 来统配 |
     | 查看用户 | SELECT host,user from mysql.user;                            | host:代表可以登录的 host<br />user:代表可以登录的用户名      |
     | 刷新权限 | flush privileges;                                            | 只有刷新了权限后才能使用                                     |
     | 更改密码 | set password for 'username'@'host' = password('newPassword') | 有时候会出现远程连接工具不能登录,可以尝试用 ALTER USER  'username'@'host' IDENTIFIED WITH mysql_native_password BY 'password'; |

  2. 数据备份导入

     | 功能       | 命令                                                         | 参数                                        |
     | ---------- | ------------------------------------------------------------ | ------------------------------------------- |
     | 导出数据库 | mysqldump -uusername -ppassword databaseName tableName> database.sql | databaseName: 数据库名<br />tableName: 表名 |
     | 导入数据库 | use database;<br />source d:/database.sql                    | database:选择数据库                         |

  3. 字符集相关

     | 功能                 | 命令                                                         | 参数                                          |
     | -------------------- | ------------------------------------------------------------ | --------------------------------------------- |
     | 查看数据库指令集相关 | SHOW VARIABLES WHERE Variable_name LIKE 'character\_set\_%' OR Variable_name LIKE 'collation%'; |                                               |
     | 设置数据库的指令集   | ALTER DATABASE databaseName CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci; |                                               |
     | 设置表的指令集       | ALTER TABLE tableName CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci; |                                               |
     | 设置字段的指令集     | ALTER TABLE tableNameCHANGE colum colum_type CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci; | colum:代表字段名<br />colum_type:代表字段类型 |

* Json 操作

  * 在 Mysql 5.7 版本之后提供了对 json 文件的支持，这样大大方便了数据操作的灵活性，为了方便使用和记忆所以总结一些常用的 json 函数方便使用
  
    ```sql
    # 数据库初始化 sql
    CREATE TABLE `t_user` (`id` int(11) NOT NULL AUTO_INCREMENT,`user_info` json DEFAULT NULL, PRIMARY KEY (`id`)); 
  
    # 导入数据
    insert into t_user (id, user_info) values (1,JSON_OBJECT('name','zhangsan','age',18,'address','sh'));
    ```
    
    | 功能描述                 | 函数语法                                                     | 使用样例                                                     | 返回值说明                                                   |
    | ------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
    | 查找 json 中某个路径的值 | json_extract(josn,path)<br />json-> 'path'<br />json->> 'path' | select json_extract(user_info,'$.name') as name from t_user;<br />select user_info -> 'path' from t_user | 如果路径不存在则返回 null 否则返回对应的数据                 |
    | 判断某个路径是否存在     | json_contains_path(json,path)                                | select * from t_user where json_contains_path(user_info,'one','$.age') = 1; | 如果路径存在则返回 1，不存在返回 0，如果有 null 参数返回 null |
    | 判断某个路径下是否是某值 | json_contains(json,value,path)                               | select * from t_user where json_contains(user_info,'"zhangsan"','$.age') = 1; | 如果值相同返回1，值不相同返回0，路径不存在返回null。         |
    | 插入对应的json 数据      | json_insert(json,path,value)                                 | update t_user set user_info = json_insert(user_info,'$.phone','120') where id = 2; | 如果指定路径不存在则插入数据，否则不进行操作                 |
    | 替换对应的json 数据      | json_replace(json,path,value)                                | update t_user set user_info = json_replace(user_info,'$.name','james') where id= 2; | 替换对应的数据，如果存在则替换否则不进行操作                 |
    | 指定对应路径的json 数据  | json_set(json.path,value)                                    | update t_user set user_info = json_set(user_info,'$.nickname','{"age":13}') where id = 2; | 直接设定对应的值不管路径是否存在                             |
    | 移除指定路径的 json 数据 | json_remove(json,path)                                       | update t_user set user_info = json_remove(user_info,'$.nickname') where id = 2; | 移除对应的路径，如果路径不存在则不进行操作                   |
    
  * 注意：在使用 json_set / insert / replace 操作 json 数据的时候，如果 value 是 json 字符串，需要使用 cast  / convert 函数进行转换。否则 mysql 会按照字符串进行处理。
  
* 分析函数

  * 语法

    ```sql
    funcation_name(<argument>,<argument>..) over(<partition by clause> <order by clause>)
    ```

    Function_name() : 函数名称

    argument：参数

    over():开窗函数

  * 常见分析函数

    | 函数                                     | 功能             |
    | ---------------------------------------- | ---------------- |
    | count() over()                           | 统计行数         |
    | sum() over()                             | 统计总和         |
    | avg() over()                             | 统计平均值       |
    | min() over()/max() over()                | 统计最大值最小值 |
    | rank() over()                            | 跳跃排序         |
    | dense_rank()                             | 连续排序         |
    | row_number() over()                      | 排序无重复值     |
    | first_value() over()/last_value() over() | 取出对应数据     |
    |                                          |                  |

    

