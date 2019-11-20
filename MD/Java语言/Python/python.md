# Python3

-------------------

## python基础

- Python简介：

  > python是一种解释型脚本语言,由荷兰人Guido van Rossum发明，可以跨平台，语法简单明了，写法简单轻松，上手难度简单，丰富的类库可以胜任一般的小功能开发

- Python基础数据类型：

  > python是一种弱类型语言，不需要在使用之前对数据类型进行定义

  | 数据类型                            | 常见方法                                                     |
  | ----------------------------------- | ------------------------------------------------------------ |
  | num(Var =1)                         |                                                              |
  | Str(Var = 'abc' / var = str(a))     | strip()/upper()/lower()/[startIndex:endIndex]                |
  | List(Var = [1,'2','c'])             | [startIndex:endIndex]/append()/count()/pop()/reverse()/del list[1] |
  | Dict(var={'a':1,'b':2})             | ['keyName']/del dict['a']                                    |
  | Tuple(var=(1,2)元组是不可更改的数组 | 同list                                                       |

- Python基础语法：

  - 方法定义

    ```python
    #方法定义
    def addTwo(a,b):
      result = int(a)+int(b)
      return result
    
    #方法调用
    print(functionName(1,2))
    ```

  - 导库

    ```python
    import datetime
    import json
    
    demoMap = {'a':1,'b':2}
    jsonStr = json.dumps(demoMap)
    print(jsonStr)
    ```

  - 逻辑控制

    ```python
    #判断
    a = 1
    if a == 1:
      print('1 == 1')
    elif a == 2:
      print('2 == 2')
    else :
      print ('a != [1,2]')
      
    #循环
    for i in range(0,10，step =1):
      print(i)
      
    dataFile = open('abc.txt','r')
    for line in dataFile:
      print(line)
     
    a = 1
    while(a <= 20 ):
      print(a)
      a = a + 1
     
    #异常控制
    try:
      0 / 0
    Except BaseException:
      print('error')
      
    ```

  - 面向对象

    ```python
    #定义类
    class Dog:
      #类变量，在实例化中的对象是通用的类似于静态变量
      kind = 'caine'
      #构造方法
      def __init__(self,name):
        #实例属性
        self.name = name
      #类方法
      def sayName(self):
        return self.name + 'is me!'
        
    tomDog = Dog('tom')
    print(tomDog.sayName())
    ```
    
  
- Python环境搭建：

  1. 直接安装官方发行版

     > 缺点单版本安装，在需要多环境多版本时比较麻烦。包管理用pip管理，仅包含自定义的一些包信息

     - [下载](https://www.python.org/downloads/)
     - 直接安装即可

  2. Anaconda开源发行版

     > 有点可以很方便的构建多个py环境各环境隔离，自带了大量科学包，包管理使用conda.同时方便的提供了notebook spyer开发环境

     - [下载清华源](https://mirrors.tuna.tsinghua.edu.cn/anaconda/archive/)
     - 解压安装即可

- Python常用库：

  - 文件操作

    ```python
    from pathlib import Path
    
    open('GC and Tuning.md','r')
    open('GC and Tuning.md','w')
    open('GC and Tuning.md','a')
    
    folder = Path('.')
    for item in folder.iterdir():
        print(item)
    
    gcPath = Path('GC and Tuning.md')
    print(gcPath.is_dir())
    print(gcPath.is_file())
    for line in gcPath.open(mode = 'w'):
        print(line)
    ```

  - json操作

    ```python
    import json
    import pathlib
    
    tom = {'name':'tome','age':11,'address':'sh','car':{'name':'benze','speed':'200km/h'}}
    tomJsonStr = json.dumps(tom)
    print(tomJsonStr)
    jsonObject = json.loads(tomJsonStr)
    print(jsonObject['name'])
    jsonObject['status'] = 'alive'
    print(jsonObject)
    
    resultFile = open('tom.json','w')
    json.dump(tom,resultFile)
    ```

  - request操作

    ```python
    import requests
    
    requestheader = {'Content-Type':'application/json'}
    baseUrl = 'https://www.baidu.com'
    response = requests.get(baseUrl,headers=requestheader)
    requestUrl = response.url
    responseCode = response.status_code
    responseBody =  response.text
    responseHeader = response.headers
    
    print(requestUrl)
    print(responseCode)
    print(responseBody)
    print(responseHeader)
    ```

  - numpy

  - Sklearn