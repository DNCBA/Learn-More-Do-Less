# springMVC框架

---------------

## 什么是springMVC框架

​	springMVC是sun公司提出的三层架构中的web层框架。同时springMVC框架本身也是一个MVC的框架，所谓的MVC框架就是将model(模型)，View(视图)，Controller(控制器)区分开来。之所以这样进行区分是因为在早期的JavaEE开发中，使用jsp的时候将视图，控制器，模型混合在一起导致代码逻辑混乱，后期维护性差等缺点。为了避免这些问题，所以提出了MVC分层的思想，将每个部分的代码进行分离。其中springMVC就是其中的一个实践，同时springMVC通过一个前段控制器将所有的请求拦截，再根据URI进行再分发来方便后台开发。

## 为什么使用springMVC框架

​	springMVC作为SpringFrameWork框架web部分的一块，不仅依旧有着spring的轻量，低侵入的特点。并且还因为采用单例的controller在存在并发访问的时候不会出现数据安全问题。因此也带来了高性能的用户体验。在对于开发者而言，使用springMVC进行开发，可以避免频繁的修改web.xml，同时也可以更加简洁的对请求的参数进行封装和处理。在将请求处理完成之后，springMVC还提供了丰富的视图处理工具，以及灵活的数据相应方式供开发者使用。这样可以极大的提高开发者的开发效率。

## 怎么使用springMVC框架

* 创建一个普通的Maven-Web工程，在src目录下创建java和resources目录

* 在工程的依赖中添加spring的核心依赖，以及springMVC的依赖

  * springy依赖部分

    ```xml
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-context</artifactId>
        <version>5.1.1.RELEASE</version>
    </dependency>
    ```

  * springMVC依赖部分

    ```xml
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-web</artifactId>
            <version>5.1.1.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-webmvc</artifactId>
            <version>5.1.1.RELEASE</version>
        </dependency>
    ```

* 在resources目录下创建applcation.xml进行对应的配置

  ```xm
  <context:component-scan base-package="com.mhc"></context:component-scan>
  <mvc:annotation-driven />
  ```

* 在web.xml中配置spring的核心监听器和springMVC的前端控制器

  * spring的核心监听器

    ```xml
    <context-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>classpath:spring.xml</param-value>
    </context-param>
    
    <listener>
        <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
    </listener>
    ```

  * springMVC的前端控制器

    ```xml
      <servlet>
        <servlet-name>dispatcherServlet</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <init-param>
          <param-name>contextClass</param-name>
          <param-value>org.springframework.web.context.support.AnnotationConfigWebApplicationContext</param-value>
        </init-param>
        <load-on-startup>5</load-on-startup>
      </servlet>
      <servlet-mapping>
        <servlet-name>dispatcherServlet</servlet-name>
        <url-pattern>*.do</url-pattern>
      </servlet-mapping>
    ```

* 编写对应类并添加@Controller，@RequestMapping，@RequestParm，@ResponseBody等注解进行控制

  ```java
  @Controller
  public class UserController{
      
      @RequestMapping("/user/login")
      @ResponseBody
      public Object login(User user){
          return user;
      }
  }
  ```


## springMVC进阶

* 异常处理

  * 编写一个类，添加对应的@ControllerAdvice或者@RestControllerAdvice

  * 在类中编写对应方法，并在方法上添加@ExeceptioHandler注解

  * 在方法内部进行对应的逻辑处理

    ```java
    @ControllerAdvice
    public class ExeceptionHandler{
        
        @ExeceptionHandler(value = Throwable.class)
        @ResponseBody
        public Object handler(Throwable throwable){
            return throwable;
        }
    }
    ```

* 参数校验

  * 使用hibernate-validate框架来进行参数校验

  * 添加pom依赖

    ```xml
    <dependency>
        <groupId>org.hibernate</groupId>
        <artifactId>hibernate-validator</artifactId>
        <version>5.2.2.Final</version>
    </dependency>
    
    ```

  * 在applicationContext.xml中添加

    ```xml
    <bean id="validator" class="org.springframework.validation.beanvalidation.LocalValidatorFactoryBean" />  
    ```

  * 在实体类以及对应的形参上添加注解

* 全局异常处理

  * 在web.xml的前段控制器中，添加以下配置

    ```xml
    <init-param>
        <param-name>throwExceptionIfNoHandlerFound</param-name>
        <param-value>true</param-value>
    </init-param>
    ```

  * 这样在编写的异常处理中就可以把捕获这个异常进行处理

* 模板引擎

* 文件上传

  * 在pom依赖中添加fileupload

    ```xml
    <dependency>
        <groupId>commons-fileupload</groupId>
        <artifactId>commons-fileupload</artifactId>
        <version>1.3.1</version>
    </dependency>
    ```

  * 在applicationContext.xml中配置

    ```xml
    <!--文件上传的解析器-->
    <bean id="multipartResolver"
          class="org.springframework.web.multipart.commons.CommonsMultipartResolver">
        <!-- 上传文件大小上限，单位为字节（10MB） -->
        <property name="maxUploadSize">
            <value>10485760</value>
        </property>
        <!-- 请求的编码格式，必须和jSP的pageEncoding属性一致，以便正确读取表单的内容，默认为ISO-8859-1 -->
        <property name="defaultEncoding">
            <value>UTF-8</value>
        </property>
    </bean>
    ```

  * 在方法形参中添加MutilpartFile即可接收参数

* 中文乱码

  * 可以在web.xml中添加

    ```xml
      <!--解决POST乱码问题-->
      <filter>
        <filter-name>encodingFilter</filter-name>
        <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
        <init-param>
          <param-name>encoding</param-name>
          <param-value>UTF-8</param-value>
        </init-param>
      </filter>
      <filter-mapping>
        <filter-name>encodingFilter</filter-name>
        <url-pattern>/*</url-pattern>
      </filter-mapping>
    ```


