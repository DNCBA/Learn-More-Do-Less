1. object类:
  * 常用方法:
  	String toString()
  	Class getClass()
  	​		获取字节码对象的三种方式
  	​					在运行的时候,字节码对象在类加载的时候,由jvm创建的对象,;类加载一次,字节码对象也就只有一个.
  	​					通过object的getClass()方法
  	​					通过调用类的属性.class
  	​					通过Class类的静态方法forName()获取
  	​					Class clazz=Class.forName("全类名")
  	​		什么是字节码对象?
  	​					根据.class文件创建一个字节码对象
  	​		什么时候类进内存?
  	​					1.创建对象的时候
  	​					2.通过类名调用静态成员的时候
  	​					3.创建子类对象的时候,会加载父类的字节码文件
  	​					4.通过类名调用子类的静态成员的时候会加载父类再加载子类
  	​					5.反射操作
  	Boolean equals(Object obj)
  	​		Stringle可中的equals方法和Object中的equals方法不同
2. System类
  1. public static void arraycopy(Object src,int srcPos,Object dest,int destPos,int length)
  	* 作用:从A数组中复制指定位置的指定长度元素到B数组
  2. public static long currentTimeMillis()
  	* 作用:获取当前时间的毫秒值
  3. public static void exit(int status)
  	* 作用:终止当前虚拟机
  	

  输出语句特别耗费时间
  ​		
  ​	

3. Date类
    1.构造函数:
    ​			Date():创建的是一个表示当前系统时间的Date对象
    ​			Date(long date):根据"指定时间"创建Date对象
    ​	2.方法:
    ​		long getTime()
    ​		Void setTime(long time)
4. SimpleDateFormat类
    DateFormat:抽象类
    ​	如何使用DateFormate的方法
    ​		1.找子类,创建子类对象
    ​		2.在DateFormate中看有没有静态方法,返回值类型是DateFormat
    ​	1.方法:
    ​		String formate(Date d) 格式化
    ​		Date prase(String data) 解析
    ​	注意:解析的字符串,模式必须和构建对象的模式一样
5. Calendar类
    构造: 因为这个类是一个抽象类无法实例化对象,但是提供了一个实例化方法
    ​		静态方法getInstance()返回值是一个Calendar的对象
    ​	方法:
    ​		Get
    ​		Set
    ​		Add
    ​	注意:Calendar类的月份是从0开始的.

6. 基本数据类型的包装类
    除了char和int的包装类叫做Character和Integer其他的都是这个基本数据类型首字母大写

    ```java
    Integer:
    		public int intValue()
    		
    		public static int parseInt(String s)
    		
    		public static String toString(int i)
    		
    		public static Integer valueOf(String s)
    ```

