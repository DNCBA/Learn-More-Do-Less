1. 体系:
   ​	* Collection(接口)
   ​			|---List(接口)特点:有序,可存储重复值,有索引
   ​				|---ArrayLIst(实现类)
   ​					底层是数组:查询快,增删慢,
   ​				|--LinkedList(实现类)
   ​					底层是链表:查询慢,增删快
   ​			|----Set(接口)特点:无序,不可存储重复值,无索引
   ​				|---HashSet(实现类)
   2.Set(接口)
   ​	* 如何实现不可存储重复元素
   ​		* set集合的特点:
   ​			无序(存储和输出的顺序不一样)(底层有算法来确定排序,如果存入的是某个正确顺序则一定会返回同样的顺序)
   ​			不允许重复(要求元素唯一)
   ​			因为没有顺序所以没有索引
   ​		* Add()方法解析:
   ​			1. 根据新添加元素的hashcode()返回值计算出hash值
   ​			2. 遍历每一个集合中元素
   ​			3. 比较每一个元素的hash值和添加元素的hash值是否一样,如果hash值一样,则使用equa/., /ls方法进行比较,
   ​			4. 如果所有条件都不满足则添加成功.
   ​		* hashCode():
   ​			我们发现当hashcode方法永远返回整数1是,所有对象的hash值是一样的
   ​			有一些对象他们的成员变量玩安全不同,但是他们还需要hash和equals方法的比较
   ​			我们可以通过对hashcode方法进行改进;改进成成员变量的和,基本数据类型直接用,,引用数据类型用对应的hashcode方法
   3.Collections类:
   ​	* Collection是集合体系的最高层,包含了集合体系的共性
   ​	* Collections是一个工具类,(私有构造器,成员全部被static修饰)

```java
	常用方法:
	public static <T> int binarySearch(List list,T key):二分查找
	public static <T> void copy(List dest, List src):src源列表,把源列表的元素覆盖到目标列中
	public static <T> void fill(List list,T obj):使用指定元素覆盖目标列表的所有元素
	public static void reverse(List list):反转目标列表中的元素
	public static void shuffle(List list):随机置换
	public static  void sort(List list):按照元素的自然规则进行排序
	public static void swap(List list,int i,int j):在指定列表的指定位置处交换元素
```
4.双列集合体系Map(接口)
​	* 为了体现这种有对应关系的数据,我们使用跟以前所学的内容是可以实现的,但是略有不便,所以Java为我们提供了一种新的存储容器
​	* Collection:存储的是一个一个的对象
​	
	Map:存储的是一对一对的数据,存储的是映射关系
		格式:Map<K,V>: K--->key键
				          V---->value值
	成员方法:
			添加:put(K key,V value):就是讲Key映射到value,如果key存在则覆盖原先的value,并将原来的value返回
			修改:put(K key,V value)
			删除:remove(Object key):根据指定键值删除对应的映射关系,并返回key对应的值.如果没有删除成功则返回null
			获取:get(Object key):根据对应的键值获取对应的映射关系.
			判断是否有指定的键:containsKey(Object key):判断指定的键值是否存在
			判断是否有指定的值:containsValue(Object value):判断指定的valu额是否存在
			长度size():返回对应的关系对数
			keySet():获取所有的键值,放置到Set集合中.因为键值不允许重复所以使用的是set的集合
			Values():获取所有的value值,并将返回值放在Collection中,因为放在value的值可以重复,所以返回了一个Collection,通过源码可以知道这个返回的是一个实现了Collection的成员内部类.


​			
​	
​	Map:将键值映射到值的对象.一个映射不能包含多个键值,
