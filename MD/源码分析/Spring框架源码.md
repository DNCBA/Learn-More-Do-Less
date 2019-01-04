# Spring框架源码

------------

> spring版本：5.0.9.RELEASE

## Springframework包结构分析

* core Container 
  * beans：包含对beanFactory的定义以及bean
  * core：对ioc和di的基本实现
  * context：ioc容器
  * el：el表达式相关
* aop
  * aop：面向切面编程
  * aspects：集成aspectJ
* web
  * webMVC：springMVC
* data access
  * jdbc：jdbc的支持
  * tx：事物
  * orm：对象关系映射

## Spring容器类关系分析

* BeanFactory：顶层bean工厂
  * ListableBeanFactory：提供了可以根据一些条件来获得Bean的工厂
  * HierarchicalBeanFactory：提供了可以访问父容器的工厂
  * AutowireCapableBeanFactory：提供了可以自动装配的工厂
    * ConfigurableListableBeanfactory：实现了上述三个接口
      * XmlBeanfactory：最初的基于xml的bean工厂
      * WebApplication：web项目的工厂，提供了加载的同时直接进行实例化的功能
      * ClassPathXmlApplicationContext：常用的基于calsspath目录下的web容器
* 容器初始化相关
  * Resource：用作封装配置文件
    * ContextResource
    * WritableResource
    * HttpResource
  * BeanDefinitionReader：用来将配置文件进行解析
    * doLoadBeanDefinitions()-->用来将Resource资源解析成Document
    * processBeanDefinition()-->具体的解析方法
  * BeanDefinition：用来封装解析后的数据
  * AliasRegistry：用来将解析后的数据注册到ioc容器中
  * AbstractAutowireCapableBeanFactory：beanfactory的一个抽象实现
    * createBeanInstance()--->用来通过反射创建对象
    * populateBean()--->用来给对象属性赋值DI

## 从ContextLoaderListener开始

ContextLoaderListener

```java
public class ContextLoaderListener extends ContextLoader implements ServletContextListener {
	//构造方法
    public ContextLoaderListener() {}
    public ContextLoaderListener(WebApplicationContext context) {
		super(context);
	}
    //web项目启动时触发
    public void contextInitialized(ServletContextEvent event) {
		initWebApplicationContext(event.getServletContext());  //ioc容器初始化
	}
    //web项目销毁时触发
    public void contextDestroyed(ServletContextEvent event) {
            closeWebApplicationContext(event.getServletContext());  //关闭容器
            ContextCleanupListener.cleanupAttributes(event.getServletContext());  //清除监听器
    }
}
```

ContextLoader

```java
public class ContextLoader {
		public WebApplicationContext initWebApplicationContext(ServletContext servletContext) {
		if (servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE) != null) {
			throw new IllegalStateException(
					"Cannot initialize context because there is already a root application context present - " +
					"check whether you have multiple ContextLoader* definitions in your web.xml!");
		}

		Log logger = LogFactory.getLog(ContextLoader.class);
		servletContext.log("Initializing Spring root WebApplicationContext");
		if (logger.isInfoEnabled()) {
			logger.info("Root WebApplicationContext: initialization started");
		}
		long startTime = System.currentTimeMillis();

		try {
			//如果容器没有初始化则进行初始化
			if (this.context == null) {
                //创建一个新的容器
				this.context = createWebApplicationContext(servletContext);
			}
            //判断当前容器类型，并进行对应的初始化操作
			if (this.context instanceof ConfigurableWebApplicationContext) {
				ConfigurableWebApplicationContext cwac = (ConfigurableWebApplicationContext) this.context;
				if (!cwac.isActive()) {
					if (cwac.getParent() == null) {
						ApplicationContext parent = loadParentContext(servletContext); //设置父容器
						cwac.setParent(parent);
					}
					configureAndRefreshWebApplicationContext(cwac, servletContext);  //刷新容器
				}
			}
			servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, this.context);
            //将容器保存在当前线程中
			ClassLoader ccl = Thread.currentThread().getContextClassLoader();
			if (ccl == ContextLoader.class.getClassLoader()) {
				currentContext = this.context;
			}
			else if (ccl != null) {
				currentContextPerThread.put(ccl, this.context);
			}

			if (logger.isDebugEnabled()) {
				logger.debug("Published root WebApplicationContext as ServletContext attribute with name [" +
						WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE + "]");
			}
			if (logger.isInfoEnabled()) {
				long elapsedTime = System.currentTimeMillis() - startTime;
				logger.info("Root WebApplicationContext: initialization completed in " + elapsedTime + " ms");
			}

			return this.context;
		}
		catch (RuntimeException ex) {
			logger.error("Context initialization failed", ex);
			servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, ex);
			throw ex;
		}
		catch (Error err) {
			logger.error("Context initialization failed", err);
			servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, err);
			throw err;
		}
	}
    //创建新的容器
    protected WebApplicationContext createWebApplicationContext(ServletContext sc) {
		Class<?> contextClass = determineContextClass(sc); //根据配置返回对应的容器class
		if (!ConfigurableWebApplicationContext.class.isAssignableFrom(contextClass)) {
			throw new ApplicationContextException("Custom context class [" + contextClass.getName() +
					"] is not of type [" + ConfigurableWebApplicationContext.class.getName() + "]");
		}
		return (ConfigurableWebApplicationContext) BeanUtils.instantiateClass(contextClass);//实例化返回容器
	}
}
```

BeanUtils

```java
public class BeanUtils{
    	//根据class类型实例化对象
        public static <T> T instantiateClass(Class<T> clazz) throws BeanInstantiationException {
        Assert.notNull(clazz, "Class must not be null");
        if (clazz.isInterface()) {
            throw new BeanInstantiationException(clazz, "Specified class is an interface");
        } else {
            try {
                Constructor<T> ctor = KotlinDetector.isKotlinType(clazz) ? BeanUtils.KotlinDelegate.getPrimaryConstructor(clazz) : clazz.getDeclaredConstructor();
                return instantiateClass(ctor);
            } catch (NoSuchMethodException var2) {
                throw new BeanInstantiationException(clazz, "No default constructor found", var2);
            } catch (LinkageError var3) {
                throw new BeanInstantiationException(clazz, "Unresolvable class definition", var3);
            }
        }
    }
}
```







