# Mybatis源码分析

-----------------

## 核心类及主要功能

* SqlSessionFactoryBuilder ： 用来构建sqlSessionFactory工厂
* Configuration ：保存了用户配置的一些设置及映射关系
* SqlSessionFactory ： 用来生产sqlSession的工厂
  * DefaultSqlSessionFactory
  * SqlSessionManager
* SqlSession：sql会话对象
  * SqlSessionManager
  * DefaultSqlSession
  * SqlSessionTemplate
* Executor：执行器
  * CachingExecutor
  * SimpleExecutor
  * BatchExecutor

## 从SqlSessionFactoryBuilder开始

SqlSessionFactoryBuilder	

```java
public class SqlSessionFactoryBuilder {
    
    //开始构建
	public SqlSessionFactory build(InputStream inputStream) {
        return this.build((InputStream)inputStream, (String)null, (Properties)null);//调用重载方法
    }
    //真正构建的方法
    public SqlSessionFactory build(InputStream inputStream, String environment, Properties properties) {
        try {
             //构建一个配置解析器
              XMLConfigBuilder parser = new XMLConfigBuilder(inputStream, environment, properties);
              //调用另外的重载方法
              return build(parser.parse() //这个方法会将配置解析成一个configuration对象);
        } catch (Exception e) {
                throw ExceptionFactory.wrapException("Error building SqlSession.", e);
        } finally {
                  ErrorContext.instance().reset();
                  try {
                    inputStream.close();
                  } catch (IOException e) {
                    // Intentionally ignore. Prefer previous error.
                  }
        }
  	}
    
  //生成一个sqlsessionFactory对象                     
  public SqlSessionFactory build(Configuration config) {
    	return new DefaultSqlSessionFactory(config);
  }
                           
}
```

SqlSessionFactory

```java
public class DefaultSqlSessionFactory implements SqlSessionFactory {
    
    //获得sqlSession
	public SqlSession openSession() {
    	return openSessionFromDataSource(configuration.getDefaultExecutorType(), null, false);
  	}
    
    //真正获得sqlSession的方法
    private SqlSession openSessionFromDataSource(ExecutorType execType, TransactionIsolationLevel level, boolean autoCommit) {
        Transaction tx = null;
        try {
            final Environment environment = configuration.getEnvironment();
            //获得事物管理工厂
            final TransactionFactory transactionFactory = getTransactionFactoryFromEnvironment(environment);
            //创建一个新的食物
            tx = transactionFactory.newTransaction(environment.getDataSource(), level, autoCommit);
            //根据当前的配置和事物创建一个执行器
            final Executor executor = configuration.newExecutor(tx, execType);
            //根据执行器，配置来创建一个sqlSession
            return new DefaultSqlSession(configuration, executor, autoCommit);
        } catch (Exception e) {
            closeTransaction(tx); // may have fetched a connection so lets call close()
            throw ExceptionFactory.wrapException("Error opening session.  Cause: " + e, e);
        } finally {
            ErrorContext.instance().reset();
        }
    }
}
```

SqlSession

```java
public class DefaultSqlSession implements SqlSession {
    public <T> T getMapper(Class<T> type) {
        return configuration.<T>getMapper(type, this);
    }
}
```

Configuration

```java
public class Configuration {
    
    //获得mapper
    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        return mapperRegistry.getMapper(type, sqlSession);
    }
    //创建一个新的执行器
    public Executor newExecutor(Transaction transaction, ExecutorType executorType) {
        executorType = executorType == null ? defaultExecutorType : executorType;
        executorType = executorType == null ? ExecutorType.SIMPLE : executorType;
        Executor executor;
        if (ExecutorType.BATCH == executorType) {
            executor = new BatchExecutor(this, transaction);
        } else if (ExecutorType.REUSE == executorType) {
            executor = new ReuseExecutor(this, transaction);
        } else {
            executor = new SimpleExecutor(this, transaction);
        }
        if (cacheEnabled) {
            executor = new CachingExecutor(executor);
        }
        executor = (Executor) interceptorChain.pluginAll(executor);
        return executor;
    }
}
```

MapperRegistry

```java
public class MapperRegistry {
    
    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        //创建一个mapperProxyFactory的实例
        final MapperProxyFactory<T> mapperProxyFactory = (MapperProxyFactory<T>) knownMappers.get(type);
        if (mapperProxyFactory == null) {
            throw new BindingException("Type " + type + " is not known to the MapperRegistry.");
        }
        try {
            //通过工厂来获得一个mapper对象
            return mapperProxyFactory.newInstance(sqlSession);
        } catch (Exception e) {
            throw new BindingException("Error getting mapper instance. Cause: " + e, e);
        }
    }
}
```

MapperProxyFactory

```java
public class MapperProxyFactory<T> {
    //通过jdk的动态代理来实例化对象
    protected T newInstance(MapperProxy<T> mapperProxy) {
        return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[] { mapperInterface }, mapperProxy);
    }

    public T newInstance(SqlSession sqlSession) {
        //先获得一个invocationHandler
        final MapperProxy<T> mapperProxy = new MapperProxy<T>(sqlSession, mapperInterface, methodCache);
        return newInstance(mapperProxy);
    }
}
```

MapperProxy

```java
public class MapperProxy<T> implements InvocationHandler, Serializable {
    
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            if (Object.class.equals(method.getDeclaringClass())) {
                return method.invoke(this, args);
            } else if (isDefaultMethod(method)) {
                return invokeDefaultMethod(proxy, method, args);  //核心方法
            }
        } catch (Throwable t) {
            throw ExceptionUtil.unwrapThrowable(t);
        }
        //将方法缓存
        final MapperMethod mapperMethod = cachedMapperMethod(method);
        //执行对应的方法
        return mapperMethod.execute(sqlSession, args);
    }

    private MapperMethod cachedMapperMethod(Method method) {
        MapperMethod mapperMethod = methodCache.get(method);
        if (mapperMethod == null) {
            mapperMethod = new MapperMethod(mapperInterface, method, sqlSession.getConfiguration());
            methodCache.put(method, mapperMethod);
        }
        return mapperMethod;
    }

    //拦截调用的方法，去mapper.xml文件中进行查找对应sql,并执行
    @UsesJava7
    private Object invokeDefaultMethod(Object proxy, Method method, Object[] args)
        throws Throwable {
        final Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class
            .getDeclaredConstructor(Class.class, int.class);
        if (!constructor.isAccessible()) {
            constructor.setAccessible(true);
        }
        final Class<?> declaringClass = method.getDeclaringClass();
        return constructor
            .newInstance(declaringClass,
                         MethodHandles.Lookup.PRIVATE | MethodHandles.Lookup.PROTECTED
                         | MethodHandles.Lookup.PACKAGE | MethodHandles.Lookup.PUBLIC)
            .unreflectSpecial(method, declaringClass).bindTo(proxy).invokeWithArguments(args);
    }


    private boolean isDefaultMethod(Method method) {
        return (method.getModifiers()
                & (Modifier.ABSTRACT | Modifier.PUBLIC | Modifier.STATIC)) == Modifier.PUBLIC
            && method.getDeclaringClass().isInterface();
    }
}
```



