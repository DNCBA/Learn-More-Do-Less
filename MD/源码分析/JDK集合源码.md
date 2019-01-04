# JDK集合源码

----------------------

> jdk版本为1.8

## 单列集合体系

* Collection：单列集合的顶层接口定义了常用的一个方法add/remove/size/stream

  * List：可以存储重复元素，并且也可以存储null值。新增了一些方法get/indexOf

    * ArrayList：底层为数组的list

      1. 成员变量

         ```java
         private static final long serialVersionUID = 8683452581122892189L;    //序列化的Uid
         private static final int DEFAULT_CAPACITY = 10;   //数组初始化大小
         private static final Object[] EMPTY_ELEMENTDATA = {};  //空数组时的数组
         private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};  //默认容量为空的时数组
         transient Object[] elementData;  //数组数据
         private int size;  //数组的大小
         ```

      2. 核心方法

         * add

           ```java
           public boolean add(E e) {
               ensureCapacityInternal(size + 1);   //检查是否需要扩容
               elementData[size++] = e;    //给数组中添加数据
               return true;
           }
           
           /** 检查是否需要扩容 */ 
           private void ensureCapacityInternal(int minCapacity) {
               ensureExplicitCapacity(calculateCapacity(elementData, minCapacity)); //计算所需要的最小容量
           }
           
           /** 计算所需要的最小容量 */ 
           private static int calculateCapacity(Object[] elementData, int minCapacity) {
               if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
                   //判断，如果当前list是一个空的，则拿要插入的位置和初始容量10进行比较，并返回最大值
                   return Math.max(DEFAULT_CAPACITY, minCapacity);
               }
               return minCapacity;  //否则返回传入的位置
           }
           
           /** 判断是否扩容 */
           private void ensureExplicitCapacity(int minCapacity) {
               modCount++;
           
               // 如果当前插入的位置比数组还大，则扩容
               if (minCapacity - elementData.length > 0)
                   grow(minCapacity);  //扩容
           }
           
           /** 扩容 */
           private void grow(int minCapacity) {
               int oldCapacity = elementData.length;  //保存原先数组的容量
               int newCapacity = oldCapacity + (oldCapacity >> 1);  //将原数组容量扩大1.5倍
               if (newCapacity - minCapacity < 0)   //如果扩容后的容量还是比插入位置小
                   newCapacity = minCapacity;   //则将容量扩大到插入位置
               if (newCapacity - MAX_ARRAY_SIZE > 0)   //检查扩容后容量是否查出最大容量
                   newCapacity = hugeCapacity(minCapacity); 
               // 根据计算出的容量大小，将数据重新拷贝到新的数组中，并更新数据
               elementData = Arrays.copyOf(elementData, newCapacity);
           }
           ```

         * remove

           ```java
           public boolean remove(Object o) {
               //判断要删除的元素是否为null
               if (o == null) {
                   //遍历整个数组
                   for (int index = 0; index < size; index++)
                       //如果有值为null则删除当前元素
                       if (elementData[index] == null) {
                           fastRemove(index);
                           return true;
                       }
               } else {
                   //遍历整个数组
                   for (int index = 0; index < size; index++)
                       //如果发现该元素则删除
                       if (o.equals(elementData[index])) {
                           fastRemove(index);
                           return true;
                       }
               }
               return false;
           }
           
           /** 删除元素 */ 
           private void fastRemove(int index) {
               modCount++;
               int numMoved = size - index - 1;  //判断是否数组下标越界
               if (numMoved > 0)
                   //将当前位置之后的数据左移一位
                   System.arraycopy(elementData, index+1, elementData, index,
                                    numMoved);
                   elementData[--size] = null; //释放最后的一位空间
               }
           ```

         * get

           ```java
           public E get(int index) {
               rangeCheck(index);    //检查是否索引越界
               return elementData(index);  //返回对应数据
           }
           
           private void rangeCheck(int index) {
               if (index >= size)   //如果索引大于数组容量
                   throw new IndexOutOfBoundsException(outOfBoundsMsg(index));  //抛异常
           }
           ```

         * indexOf

           ```java
               public int indexOf(Object o) {
                   //遍历集合找到对应的数据并返回
                   if (o == null) {
                       for (int i = 0; i < size; i++)
                           if (elementData[i]==null)
                               return i;
                   } else {
                       for (int i = 0; i < size; i++)
                           if (o.equals(elementData[i]))
                               return i;
                   }
                   //未找到则返回-1
                   return -1;
               }
           ```

    * LinkedList：底层为链表结构的list

      1. 成员变量

         ```java
         transient int size = 0;  //list当前容量
         transient Node<E> first;   //头节点
         transient Node<E> last;    //尾节点
         
         //  内部类，为数据节点的数据结构
         private static class Node<E> {
             E item;  //数据
             Node<E> next;  //后指针
             Node<E> prev;  //前指针
         
             Node(Node<E> prev, E element, Node<E> next) {
                 this.item = element;
                 this.next = next;
                 this.prev = prev;
             }
         }
         ```

      2. 核心方法

         * add

           ```java
           public boolean add(E e) {
               linkLast(e);   //给最后一位添加数据
               return true;
           }
           
           void linkLast(E e) {
               final Node<E> l = last;   //先获取最后一个节点
               final Node<E> newNode = new Node<>(l, e, null);   //创建一个节点，头指针指向最后一个节点，尾指针指向null
               last = newNode;  //更新list的尾节点
               if (l == null)  //如果最后一个节点是null则，将头节点也指向新插入的节点
                   first = newNode;
               else
                   l.next = newNode;  //更新最后一个节点的尾指针
               size++;  //跟新容量
               modCount++;  
           }
           ```

         * remove

           ```java
           public boolean remove(Object o) {
               //判断被删除元素是否为null
               if (o == null) {
                   //遍历链表
                   for (Node<E> x = first; x != null; x = x.next) {
                       if (x.item == null) {
                           //删除对应元素
                           unlink(x);
                           return true;
                       }
                   }
               } else {
                   //遍历链表
                   for (Node<E> x = first; x != null; x = x.next) {
                       if (o.equals(x.item)) {
                           //删除对应元素
                           unlink(x);
                           return true;
                       }
                   }
               }
               return false;
           }
           
           E unlink(Node<E> x) {
               // 删除当前节点
               final E element = x.item;
               final Node<E> next = x.next;
               final Node<E> prev = x.prev;
           
               if (prev == null) {
                   first = next;
               } else {
                   prev.next = next;  //将前节点的尾指针指向当前节点的下一个节点
                   x.prev = null;
               }
           
               if (next == null) {
                   last = prev;
               } else {
                   next.prev = prev;  //将尾节点的前指针指向当前节点的前一个指针
                   x.next = null;
               }
           
               x.item = null;
               size--;
               modCount++;
               return element;
           }
           ```

         * get

           ```java
           public E get(int index) {
               checkElementIndex(index);  //检查是否越界
               return node(index).item;
           }
           
           private void checkElementIndex(int index) {
               if (!isElementIndex(index))   //如果越界则抛异常
                   throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
           }
           
           Node<E> node(int index) {
               // 如果要查找的元素小于链表长度的一半
               if (index < (size >> 1)) {
                   //则从头节点开始正序遍历
                   Node<E> x = first;
                   for (int i = 0; i < index; i++)
                       x = x.next;
                   return x;
               } else {
                   //否则从尾节点开始倒叙遍历
                   Node<E> x = last;
                   for (int i = size - 1; i > index; i--)
                       x = x.prev;
                   return x;
               }
           }
           ```

         * indexOf

           ```java
           public int indexOf(Object o) {
               int index = 0;
               //判断是否为null
               if (o == null) {
                   //正序遍历链表
                   for (Node<E> x = first; x != null; x = x.next) {
                       //找到为null元素则返回索引
                       if (x.item == null)
                           return index;
                       index++;
                   }
               } else {
                   //正序遍历链表
                   for (Node<E> x = first; x != null; x = x.next) {
                       //找到对应元素则返回索引
                       if (o.equals(x.item))
                           return index;
                       index++;
                   }
               }
               //没找到则返回-1
               return -1;
           }
           ```

    * CopyOnWriteArrayList：提供高性能读的list

      1. 成员变量

         ```java
         private static final long serialVersionUID = 8673264195747942595L; //序列化id
         final transient ReentrantLock lock = new ReentrantLock();  //可重入锁对象
         private transient volatile Object[] array;  //数据，用volatile修饰保证线程之间的可见性
         ```

      2. 核心方法

         * add

           ```java
           public boolean add(E e) {
               final ReentrantLock lock = this.lock;
               lock.lock();    //加锁，保证线程安全
               try {
                   Object[] elements = getArray();   //获得集合的所有数据
                   int len = elements.length;    //获得集合的最大容量
                   Object[] newElements = Arrays.copyOf(elements, len + 1);//创建一个新数组
                   newElements[len] = e;  //在新数组中添加数据
                   setArray(newElements);  //更换数据
                   return true;
               } finally {
                   lock.unlock();   //释放锁
               }
           }
           /** 更换数据  */
           final void setArray(Object[] a) {
               array = a;
           }
           ```

         * remove

           ```java
           public boolean remove(Object o) {
               Object[] snapshot = getArray();  //获得集合中的所有数据
               int index = indexOf(o, snapshot, 0, snapshot.length);   //查找对应对象的索引
               return (index < 0) ? false : remove(o, snapshot, index);   //删除对象
           }
           
           private boolean remove(Object o, Object[] snapshot, int index) {
               final ReentrantLock lock = this.lock;
               lock.lock();   //加锁保证线程安全
               try {
                   Object[] current = getArray();   //获得当前集合的所有数据
                   int len = current.length;    //获得但概念集合的最大容量
                   if (snapshot != current) findIndex: {   //如果集合数据在删除的时候有改变
                       int prefix = Math.min(index, len);  //根据变化更新当前数据
                       for (int i = 0; i < prefix; i++) {
                           if (current[i] != snapshot[i] && eq(o, current[i])) {
                               index = i;
                               break findIndex;
                           }
                       }
                       if (index >= len)
                           return false;
                       if (current[index] == o)
                           break findIndex;
                       index = indexOf(o, current, index, len);
                       if (index < 0)
                           return false;
                   }
                   Object[] newElements = new Object[len - 1];  //创建新数组
                   System.arraycopy(current, 0, newElements, 0, index); //复制索引前数据
                   System.arraycopy(current, index + 1,    //复制索引后数据
                                    newElements, index,
                                    len - index - 1);
                   setArray(newElements);  //更换数据
                   return true;
               } finally {
                   lock.unlock();   //释放锁
               }
           }
           ```

         * get

           ```java
           public E get(int index) {
               return get(getArray(), index);  //获得数据，根据索引
           }
           
           private E get(Object[] a, int index) {
               return (E) a[index];   //根据索引，返回数据
           }
           ```

         * indexOf

           ```java
           public int indexOf(Object o) {
               Object[] elements = getArray();  //获得集合的所有数据
               return indexOf(o, elements, 0, elements.length);
           }
           
           private static int indexOf(Object o, Object[] elements,
                                      int index, int fence) {
               //判断备选元素是否为null
               if (o == null) {
                   //遍历数组，查找并返回索引
                   for (int i = index; i < fence; i++)
                       if (elements[i] == null)
                           return i;
               } else {
                   //遍历数组，查找并返回索引
                   for (int i = index; i < fence; i++)
                       if (o.equals(elements[i]))
                           return i;
               }
               return -1;  //未找到返回-1
           }
           ```

  * Set：不可以存储重复元素，且不一定有顺序。

    * HashSet

      1. 成员变量

         ```java
         static final long serialVersionUID = -5024744406713321676L;  //序列化的uid
         private transient HashMap<E,Object> map;   //hashmap
         private static final Object PRESENT = new Object();   //填充value的值
         ```

      2. 核心方法
         - add

           ```java
           public boolean add(E e) {
               //直接调用hashMap的put方法，key为数据，value为默认值
               return map.put(e, PRESENT)==null; 
           }
           ```

         - remove

           ```java
           public boolean remove(Object o) {
               //直接调用hashMap的remove方法
               return map.remove(o)==PRESENT;
           }
           ```

    * LinkedHashSet

      1. 成员变量

         ```java
         public class LinkedHashSet<E> extends HashSet<E> implements Set<E>, Cloneable, java.io.Serializable {
             //序列化的uid
             private static final long serialVersionUID = -2851667679971038690L;  
             ...
         }
         ```

      2. 核心方法

          因为该类继承自hashSet因此核心方法实现同HashSet

    * TreeSet

      1. 成员变量

         ```java
         private static final long serialVersionUID = -2479143000061671589L;  //序列化的uid
         private transient NavigableMap<E,Object> m;  //map集合
         private static final Object PRESENT = new Object();   //填充value的默认值
         ```

      2. 核心方法
         - add

           ```java
           public boolean add(E e) {
               return m.put(e, PRESENT)==null;  //直接调用map的put方法
           }
           ```

         - remove

           ```java
           public boolean remove(Object o) {
               return m.remove(o)==PRESENT;  //直接调用map的remove方法
           }
           ```

  * Queue：FIFO的队列，并提供了一些特殊方法offer/poll/peek

    * ConcurrentLinkedQueue：支持高并发的一个非阻塞链表队列

      1. 成员变量

         ```java
         private static final long serialVersionUID = 196745693267521676L;  //序列化id
         private transient volatile Node<E> head;   //头节点
         private transient volatile Node<E> tail;  //尾节点
         private static class Node<E> {      //节点数据结构
             volatile E item;   //数据
             volatile Node<E> next;   //尾指针
         }
         ```

      2. 核心方法

         * add

           ```java
           public boolean add(E e) {
               return offer(e);  //调用offer方法
           }
           ```

         * offer：如果添加失败则返回false，不会抛异常

           ```java
           public boolean offer(E e) {
               checkNotNull(e);   //检查元素是否为null，为null则抛异常
               final Node<E> newNode = new Node<E>(e);  //创建一个新节点
               for (Node<E> t = tail, p = t;;) {   //找到最后一个节点，把新节点挂载上去
                   Node<E> q = p.next;  //获取尾结点的指针
                   if (q == null) {    //如果指针为null，则证明是最后一个
                       if (p.casNext(null, newNode)) {  //cas无锁交换，尾节点的指针数据
                           if (p != t) 
                               casTail(t, newNode);  
                           return true;
                       }
                   }
                   else if (p == q)
                       p = (t != (t = tail)) ? t : head;
                   else
                       p = (p != t && t != (t = tail)) ? t : q;
               }
           }
           
           boolean casNext(Node<E> cmp, Node<E> val) {
               return UNSAFE.compareAndSwapObject(this, nextOffset, cmp, val);
           }
           
           private boolean casTail(Node<E> cmp, Node<E> val) {
               return UNSAFE.compareAndSwapObject(this, tailOffset, cmp, val);
           }
           
           public final native boolean compareAndSwapObject(Object var1, long var2, Object var4, Object var5);
           
           private static void checkNotNull(Object v) {
               if (v == null)
                   throw new NullPointerException();
           }
           ```

         * remove

           ```java
           public boolean remove(Object o) {
               if (o != null) {  //检验元素不为null
                   Node<E> next, pred = null;
                   for (Node<E> p = first(); p != null; pred = p, p = next) {  //遍历链表
                       boolean removed = false;
                       E item = p.item;
                       if (item != null) {     //查找对应数据的节点
                           if (!o.equals(item)) {
                               next = succ(p);
                               continue;
                           }
                           removed = p.casItem(item, null);  //cas无锁删除节点
                       }
           
                       next = succ(p);
                       if (pred != null && next != null) // unlink
                           pred.casNext(p, next);
                       if (removed)
                           return true;
                   }
               }
               return false;
           }
           ```

         * poll：如果移除失败会返回null

           ```java
           public E poll() {
               restartFromHead:
               for (;;) {
                   for (Node<E> h = head, p = h, q;;) {   //遍历链表
                       E item = p.item;
                       if (item != null && p.casItem(item, null)) {   //删除最后一个节点
                           if (p != h) // hop two nodes at a time
                               updateHead(h, ((q = p.next) != null) ? q : p);
                           return item;
                       }
                       else if ((q = p.next) == null) {
                           updateHead(h, p);
                           return null;
                       }
                       else if (p == q)
                           continue restartFromHead;
                       else
                           p = q;
                   }
               }
           }
           
           final void updateHead(Node<E> h, Node<E> p) {
               if (h != p && casHead(h, p))
                   h.lazySetNext(h);
           }
           ```

         * peek

           ```java
           public E peek() {
               restartFromHead:
               for (;;) {
                   for (Node<E> h = head, p = h, q;;) {
                       E item = p.item;
                       if (item != null || (q = p.next) == null) {
                           updateHead(h, p);
                           return item;
                       }
                       else if (p == q)
                           continue restartFromHead;
                       else
                           p = q;
                   }
               }
           }
           ```

    * BlockingQueue：阻塞队列，新增了一些方法，put/take

      * ArrayBlockingQueue
        * 底层为数组的阻塞队列
      * LinkedBlockingQueue
        * 底层为链表的阻塞队列
      * DelayQueue
        * 可以提供指定时间后才能获取的阻塞队列

## 双列结合体系

* Map

  * HashMap

    1. 成员变量

       ```java
       private static final long serialVersionUID = 362498820763181265L;  //序列化id
       static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; //默认初始化容量 2^4=16
       static final int MAXIMUM_CAPACITY = 1 << 30;   //最大容量 2^30
       static final float DEFAULT_LOAD_FACTOR = 0.75f;   //默认负载因子 0.75
       static final int TREEIFY_THRESHOLD = 8;
       static final int UNTREEIFY_THRESHOLD = 6;
       static final int MIN_TREEIFY_CAPACITY = 64;
       transient Node<K,V>[] table;    //表结构
       transient Set<Map.Entry<K,V>> entrySet;    //entry集合
       transient int size;   //当前容量
       transient int modCount;
       int threshold;  //最大可存量 = 负载因子 * 最大容量
       final float loadFactor;   //负载因子
       static class Node<K,V> implements Map.Entry<K,V> {   //节点的数据结构
           final int hash;    //hash值
           final K key;     //key的值
           V value;     //value的值
           Node<K,V> next;   //指针
       }
       ```

    2. 核心方法

       * put

         ```java
         public V put(K key, V value) {
             return putVal(hash(key), key, value, false, true);  //调用putVal方法
         }
         
         final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
                        boolean evict) {
             Node<K,V>[] tab;   //节点数组
             Node<K,V> p;     //节点
             int n, i;
             if ((tab = table) == null || (n = tab.length) == 0)  //如果hashMap没有初始化
                 n = (tab = resize()).length;  //进行初始化，并获取容器的最大容量
             if ((p = tab[i = (n - 1) & hash]) == null)  //为当前数据分配数组索引
                 //如果数组当前位置为null，则直接挂载当前数据
                 tab[i] = newNode(hash, key, value, null);  
             else {
                 //数组当前位置不为null
                 Node<K,V> e; 
                 K k;
                 //判断新数据的hash和key是不是和当前位置一样
                 if (p.hash == hash &&
                     ((k = p.key) == key || (key != null && key.equals(k))))  
                     //如果一样直接进行数据替换
                     e = p;
                 else if (p instanceof TreeNode)//如果不一样，则判断当前节点下面挂在的是不是树结构
                    //是树结构则将数据挂载在红黑树上
                     e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
                 else {
                     //如果是链表结构则将数据挂载在链表上
                     for (int binCount = 0; ; ++binCount) {
                         if ((e = p.next) == null) {
                             p.next = newNode(hash, key, value, null);
                             if (binCount >= TREEIFY_THRESHOLD - 1) //如果链表深度超过8
                                 treeifyBin(tab, hash);  //将链表转换成树结构
                             break;
                         }
                         if (e.hash == hash &&
                             ((k = e.key) == key || (key != null && key.equals(k))))
                             break;
                         p = e;
                     }
                 }
                 if (e != null) { // existing mapping for key
                     V oldValue = e.value;
                     if (!onlyIfAbsent || oldValue == null)
                         e.value = value;
                     afterNodeAccess(e);
                     return oldValue;
                 }
             }
             ++modCount;
             if (++size > threshold)
                 resize();
             afterNodeInsertion(evict);
             return null;
         }
         
         //扩容操作
         final Node<K,V>[] resize() {
             Node<K,V>[] oldTab = table;  //获得当前数据
             int oldCap = (oldTab == null) ? 0 : oldTab.length;  //获得当前容量
             int oldThr = threshold;
             int newCap, newThr = 0;
             if (oldCap > 0) {   //如果已经初始化
                 if (oldCap >= MAXIMUM_CAPACITY) {  //如果当前容量已经达到最大值
                     threshold = Integer.MAX_VALUE;  //将负载调整为100%
                     return oldTab;  //不进行扩容返回
                 }
                 else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
                          oldCap >= DEFAULT_INITIAL_CAPACITY)   //如果可以扩容
                     newThr = oldThr << 1;  //将最大可存容量扩大为原来的2倍
             }
             else if (oldThr > 0) 
                 newCap = oldThr;    //将最大可存量作为容量
             else {               //容器初始化
                 newCap = DEFAULT_INITIAL_CAPACITY; //默认大小为16
                 newThr = (int)(DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY); //最大可存量
             }
             if (newThr == 0) {  //如果初始化最大可存量0。则初始化容量
                 float ft = (float)newCap * loadFactor;
                 newThr = (newCap < MAXIMUM_CAPACITY && ft < (float)MAXIMUM_CAPACITY ?
                           (int)ft : Integer.MAX_VALUE);
             }
             threshold = newThr;   
             @SuppressWarnings({"rawtypes","unchecked"})
             Node<K,V>[] newTab = (Node<K,V>[])new Node[newCap];  //根据容量创建一个新的数组
             table = newTab;
             if (oldTab != null) {    //如果之前的数组有数据
                 for (int j = 0; j < oldCap; ++j) {  //遍历之前的数组
                     Node<K,V> e;
                     if ((e = oldTab[j]) != null) {  //获取节点数据
                         oldTab[j] = null;   //释放旧的数据
                         if (e.next == null)   //如果当前节点数据为单节点
                             newTab[e.hash & (newCap - 1)] = e;  //则将数据重新分配到新的数组中
                         else if (e instanceof TreeNode)   //如果是树结构则进行新的处理
                             ((TreeNode<K,V>)e).split(this, newTab, j, oldCap);
                         else { //如果是链表结构
                             Node<K,V> loHead = null, loTail = null;
                             Node<K,V> hiHead = null, hiTail = null;
                             Node<K,V> next;
                             do {   //将每个节点取出来重新处理
                                 next = e.next;
                                 if ((e.hash & oldCap) == 0) {
                                     if (loTail == null)
                                         loHead = e;
                                     else
                                         loTail.next = e;
                                     loTail = e;
                                 }
                                 else {
                                     if (hiTail == null)
                                         hiHead = e;
                                     else
                                         hiTail.next = e;
                                     hiTail = e;
                                 }
                             } while ((e = next) != null);
                             if (loTail != null) {
                                 loTail.next = null;
                                 newTab[j] = loHead;
                             }
                             if (hiTail != null) {
                                 hiTail.next = null;
                                 newTab[j + oldCap] = hiHead;
                             }
                         }
                     }
                 }
             }
             return newTab;
         }
         ```

  * HashTable

    * 线程同步的map容器，put和get方法都加锁了。

  * Properties

    * 提供持久化的map。load/store

  * ConcurrentHashMap

    * 支持高并发的map集合

  * ConcurrentSkipListMap

    * 支持高并发，同时有序

