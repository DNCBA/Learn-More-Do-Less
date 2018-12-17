# String常用API
-----------------------

* String的构造方法：
	- String（String）
	- String（char[] arr）
	- String（char[] arr,int idex,int count）
* String的判断功能：
	- boolean equals（objec obj）：比较内容
	- boolean equalsIgnoreCase（String str）：比较内容忽略大小写
	- boolean startWith（String str）：判断是否以str为开头	- 使用场景：在删除某个姓的所有人
	- boolean endWith（String str）：判断是否以strWie结尾                 使用场景：获取某个盘符的某种类型的文件
* String的获取功能：
	- int length():获取字符串的长度，其实也就是字符的个数   可以遍历字符串
	- char charAt(int index):获取指定索引处的字符
	- int indexof(String str):获取str在字符串中第一次出现时的索引
	- String substring(int start):从start处开始截取字符串
	- String substring(int start,int end):从start处开始截取，到end处结束   （含头不含尾）
* String的转换功能：
	- char[] toCharArray():
	- String toLowerCase():
	- String toUpperCase():
* String的其他功能：
	- String trim(): 去掉字符串两端的空格
	- String[] split(String str):按照指定符号分割字符串  '.'  '+'不能用来当做切割标识
	
	* 使用环境：
		1. 搜索文件夹内的指定类型的文件，使用endsWith（String str）来进行判断
		2. 通过一个字符串来判断某姓的人数，使用String[] split（String str）来对字符串进行切割
		3. 在生成的字符数组中遍历使用boolean startWith(String str)
	* 小知识
		* ==：
			* 基本数据类型：比较的是基本数据类型的值
			* 引用数据类型：比较的是引用数据类型的地址值是否相等
		字符串的内容存储在方法区的常量池里面的，目的是为了方便字符串的重复使用

# StringBuilder的常用API

---------------

* StringBuilder的构造方法：
	- StringBuilder();
	- StringBuilder(String str);
* StringBuilderde 方法：
	- public int capacity():返回当前容量   理论值
	- public int length()：返回长度		实际值
	- public StringBuilder append(任意类型)：添加功能
	- public StringBuilder revers（）： 反转功能
	- public String toString();
