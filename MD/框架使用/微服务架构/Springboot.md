# Springboot框架

-------------

## 什么是springboot框架

* springboot框架是一款可以很方便的搭建web项目的框架，他通过使用springfarmework框架为基石，通过springfarmework框架发布的自动组装的新特性，来实现springboot的自动装配来提高项目的开发效率。并将很多配置以约定的方式进行装配，并同时对外提供修改的方法来保证框架的灵活性。总的来说springboot就是一款基于springfarmework整合了web层，service层，以及dao层的项目开发框架。

## 为什么要使用springboot框架

* 随着现在开发需求迭代的速度不断提高，传统开发模式中的项目架构搭建方式显得比较笨重，因为不仅仅要将对应的框架导入，并且还要根据实际开发需求配置大量的配置项。这样会降低开发速度。这个时候springboot的发布，带来的新的自动组装的理念，以及将大部分的配置作为约定的方式，放在框架内部供开发者使用。这样可以让开发者在尽可能少的配置下，快速的搭建一个自己想要的项目架构。并且springboot将常用的开发模块，以不同依赖组件的方式提供，让使用者可以灵活的控制自己项目的结构。因此使用springboot可以带来高效的开发速度，以及灵活的模块配置。

## 怎么使用spring boot搭建项目

* 创建一个简单的Maven工程

* 在创建项目的POM文件中继承自springboot的父依赖

  ```xml
      <!--继承的父工程-->
      <parent>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-starter-parent</artifactId>
          <version>2.0.5.RELEASE</version>
      </parent>
  ```

* 在pom插件中添加对应的springboot插件,这样在部署的时候可以找到入口

  ```xml
  <plugin>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-maven-plugin</artifactId>
      <configuration>
          <mainClass>com.huawei.benchmark.MyApplication</mainClass>
      </configuration>
  </plugin>
  ```

* 在pom中添加需要的组件，比如web依赖

  ```xml
  <dependency>
  	<groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
  </dependency>
  ```

* 然后编写一个启动类，在这个类上打上注解@SpringbootApplication.在这个类的main方法中编写，SpringApplication.run(XXX.class,args)

* 然后编写对应的controller和services以及repository，并且创建aplication.properties就可以通过启动类直接启动作业。

## springboot进阶

* web部分
  * 自定义servlet，filter，listener开发（基于servlet3.0的注解开发）
    1. 编写对应的类实现对应的接口
    2. 在编写的对应的类上添加对应的@WebServlet;@WebFilter;@WebListener
    3. 在springboot的启动类或者配置类上添加@ServletComponentScan("XX.XX.XX")扫描对应的包
  * 全局异常处理 
    1. 编写一个类，并添加@ControllerAdvice;@RestControllerAdvice注解
    2. 在这个方法中编写对应的方法，给这个方法上添加注解@ExeceptionHandler（XXXExeception.class）
    3. 在这个方法的形式参数上可以添加HttpServletRequest和Response以及Exeception，这些对象spring容器会自动进行组装注入
    4. 通过这些注入的对象可以获得到异常，以及请求和相应，在抛出对应一场后会在这里进行处理
    5. 在这些方法处理完成之后，会将对应的数据进行返回，这个时候可以通过@ResponseBody来控制返回的数据为json
  * 面向切面的处理
    1. 编写一个类，并且添加上@Aspect和@Component注解
    2. 在这个类中，可以定义个方法来确定切点表达式
       1. 编写一个无返回的方法，在这个方法上打上@Pointcut注解，并在这个注解中可以编写切点表达式
       2. 切点表示式：execution ( 修饰符 返回值类型 类的全路径名.方法名（参数类型） 异常类型 )
    3. 可以编写其他的方法来选择打上前置通知@Before，环绕通知@Around，后置通知@After，返回后通知@AfterReturning，以及异常后通知@AfterThrowing
    4. 在这些编写的通知方法中，可以直接注入JoinPoint对象，以及对应的异常对象和返回后对象。使用这些注入进去的对象获得对应的数据
    5. 注意：在环绕通知里注入的是ProceedingJoinPoint这里应该使用这个对象进行处理后并返回对应的返回值。
  * 自定义异常页面处理
    1. 在springboot的启动类上实现ErrorPageRegistrar接口
    2. 重写对应的方法registerErrorPages（ErrorPageRegistry registry）
    3. 通过registry.addErrorPages(new ErrorPage(HttpStatus,NOT_FOUND，“/baseError/404/controller”));来讲对应的错误页面进行自定义跳转

* springdataJPA部分

  * 配置部分

    1. 在项目的pom中添加springdatajpa依赖

    2. 在application.properties配置文件中添加以下对应配置

       ```properties
       # 数据库连接基本配置
       spring.datasource.url = jdbc:mysql://localhost:3306/springboot
       spring.datasource.username = root
       spring.datasource.password = root
       spring.datasource.driverClassName = com.mysql.jdbc.Driver
       #hikari数据库连接池配置
       spring.datasource.hikari.max-lifetime=5000
       spring.datasource.hikari.maximum-pool-size=150
       spring.datasource.hikari.idle-timeout=6000
       spring.datasource.hikari.connection-timeout=6000
       spring.datasource.hikari.validation-timeout=3000
       spring.datasource.hikari.login-timeout=5
       #JPA相关配置
       spring.jpa.show-sql = true
       spring.jpa.hibernate.ddl-auto=update
       ```

  * 代码部分

    1. 编写对应的实体类，并且添加相应注解，（参见：hibernate框架使用）
    2. 编写实体对应的repository接口，并且根据业务需求继承对应的Repository接口，和JpaSpecificationExecutor接口
    3. 通过spring注入来获得这个对象，并进行对应的操作即可，（参见：hibernate框架使用进阶）

