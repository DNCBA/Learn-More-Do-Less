# Hiberante框架

---------------------

## 什么是Hibernate

* Hibernate是一款优秀的持久层ORM框架，所谓的oRM(Object relational Mapping)意思是对象关系映射,这种框架将程序中的实体类和数据库中表对应其来。其中每个表中的一个Row代表一个实体。在程序中通过操作实体类，来完成对数据的操作。其框架底层还是根据配置文件将实体和数据库表对应起来。在用户操作对象之后根据这种对应在数据库中进行对应的修改。

## 为什么使用Hibernate

* 在持久层的开发过程中，因为使用原生JDBC太过繁琐（每次都要进行加载驱动，获得链接，编写statement，解析result，封装数据），导致开发速度缓慢。虽然在后来也有对应简化方法，比如使用数据库连接池，使用dbUtils等方法，但是都不尽人意。这个时候出现了自动化的ORM框架。在ORM框架中通过操作对象来完成对数据的操作。框架底层屏蔽了具体的实现过程，不需要开发人员编写sql，以及大量重复的工作。这样极大的提高了开发效率。并且使用ORM框架对于后台人员而言，降低了开发门槛，减少了数据库方面的知识要求。因此在开发过程中使用Hibernate可以为后台程序部分带来更快的开发速度，特别是在和spring-data-jpa整合之后更是方便开发。但是这款优秀的框架也有不足之处，因为框架底层进行了大量的封装，导致在项目开发后期出现问题的时候追踪问题，以及进行对应优化的时候也有很大的难度。因此在追求速度与更好控制的场景下，建议使用Mybatis来作为持久层框架。

## 怎么使用Hibernate

* 单独使用
    1. 建立普通maven工程，导入对应hibernate依赖

    2. 建立对应的数据表和实体类

    3. 编写实体类和对应表的映射配置文件XXX.hbm.xml

        ```xml
        <hibernate-mapping>
            <class name="" table="">
                <id name="" cloum="">
                    <generator class="uuid"></generator>
                </id>
                <property name="" type="" colum=""></property>
            </class>
        </hibernate-mapping>
        ```

    4. 编写hbernate.cfg.xml主配置文件

        ```xml
        <hibernate-configuration>
            <session-factory>
                <property name="connection.url">jdbc:mysql://localhost:3306/mysql</property>
                <property name="connection.username">root</property> 
                <property name="connection.password">ddd</property>
                <property name="connection.driver_class">com.mysql.jdbc.Driver</property>
                <property name="show_sql">true</property> 
                <property name="format_sql">true</property>  
                <mapping resource="entity/CourseSelectUser.hbm.xml" />   
            </session-factory>
        </hibernate-configuration>
        ```

    5. 创建configuration，并根据configuration创建sessionFactory再创建session.根据session进行对应的操作

        ```java
         public class TestHbernate{
        
        	public static void main(String[] args){
        
        		User user = new User();
        
        		Configuration configuration = new Configuration();
               		 configuration.configure();
                	SessionFactory factory = configuration.buildSessionFactory();
                	Session session = factory.openSession();
                	Transaction transaction = session.getTransaction();
                	transaction.begin();
                	session.save(user);
                	transaction.commit();
                	session.close();
        
        	}
        
        }
        ```

* 整合spring-data-jpa

    1. 编写对应的实体类在实体类上添加@Entity,@Table,@Id,@GenerateValue,@Colum,@Temppral注解进行映射配置

    ```java
        @Entity
        @Table(name="tb_user")
        public class User{
    
            @Id
            @GeneratedValue(strategy=Generation.AUTO)
            @Colum(name="userId")
            private Integer userId;
    
            @Temporal(TemporalType.TIME)   //时间类字段的标注方式
            private Date time;
    
            @Colum(name="userNaem")
            private String userName;
    
            @Colum(name="password")
            private String password;
    
        }
    ```

    2. 编写接口继承对应的Repository接口，并添加上@Repository注解。

    ```java
    @Repository
    public interface UserRepository extend JpaRepository{
        
    }
    ```

    3. 在使用的地方注入这个接口，可以直接根据对应的方法命名规则进行调用。

    ```java
    @Autowired
    private UserRepository userRepository;
    ```
## Hibernate进阶

 * 多表关联和级联操作

    * 一对多关联

       * 在创建对应的实体类的时候，在对应的实体中添加另外一个实体类的引用或者集合的引用
       * 在这些引用上面添加对应的@OneToMany(targetEntity=XX.class,mappedBy="user")和@ManyToOne(targetEntity=XX.class)注解，并且在一的一方放弃主键维护权
       * 在多的一方的引用上添加@JoinColum(name="userId")指定关联的外键
       * 在两方的对应引用上添加@Cascade(CascadeType.ALL)注解，进行级联操作

      ```java
      @Entity
      @Table(name="tb_user")
      public class User{
          @Id
          @GeneratedValue(strategy=GenerationType.AUTO)
          @Column(name="userId")
          private Integer userId;
          
          
      	@OneToMany(targetEntity=Order.class,mappedBy="user")
      	@Cascade(CascadeType.ALL)
      	private Collection<Order> orders = new ArrayList<Order>();
      }
      
      //----------------------------------------------------------------
      
      @Entity
      @Table(name="tb_order")
      public class Order{
          @Id
          @GeneratedValue(strategy="uuid")
          @GenericGenerator(name="uuid",strategy="uuid")
          @Column(name="orderId")
          private String orderId;
          
          @ManyToOne(targetEntity=User.class)
          @JoinColumn(name="userId")
          @Cascade(CascadeType.SAVE_UPDATE)
          @JsonIgnore
          private User user;
      }
      ```

 * 级联查询死循环处理办法

    * 在对应的多的一方，打上忽略序列化的注解@JasonIgnore，或者在一的一方的引用上添加@Transient注解进行忽略

 * 复杂条件查询

    * 在持久层接口中，继承JpaSpecificationExecutor<T>接口
    * 这个接口可以提供findAll(Specification<T>)复杂条件查询方法
    * 在serviceImpl里面可以调用这些方法，通过实现一个Specification接口，重写其中的toPredicate(root,query,cb)方法
    * 使用cb.like();cb.equal(“字段”，“值”)等来实现业务条件。
    * 字段通过root.get(“字段名”).as(类型.class)------------->(在多表关联查询的时候，可以使用get("对象的引用").get("新的字段名"))
    * 值通过对象.getXXX()获得,--------->()
    * 最后通过cb.and(Predicate... predicates)或者cb.or()来吧条件组装起来

 * 分页查询及实体封装

    * 在pagingAndStoreRepository接口中对简单的分页查询进行了封装，可以使用指定的方法find All(PageAble)进行查询
    * 在service层将请求的页数和每页显示数封装成PageRequest对象，使用PageRequest.of(pageIndex,size)创建
    * 在调用对应方法后会返回一个Page对象，通过这个对象可以获得当前页数据(getContent())，总分页数(getTotalPages())，总记录数(getTotalElements())

 * 自定义sql

    * 在repository的接口的对应方法上添加注解@Query(value=“HQL”)和@Modify
    * 编写HQL的时候，和sql一样但是省略select *。从from开始写。
       * 例子：from  实体名  where  userId = ： userId
   *  参数传递，通过在方法形参上添加@Parm（“parameter”）,然后在HQL中通过：parameter来进行引用