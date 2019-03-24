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
     | 更改密码 | set password for 'username'@'host' = password('newPassword') | 有时候会出现远程连接工具不能登录,可以尝试用 old_password ('password') |

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

     