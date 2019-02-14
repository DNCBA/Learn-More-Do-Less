# 从Synchronized到cas

---------------------

> 在多核并行的时代里，因为线程并发而带来的安全问题，也逐渐暴露在视野中。在jdk1.5版本后添加java.util.concurrent包中提供了大量支持高并发的容器和线程的工具类来完成在多线程下的工作

* 线程的状态
  * 初始化状态：可以通过start()方法变成可运行状态
  * 可运行状态：等待系统调度变成可运行状态
  * 运行状态：运行完成后变成运行结束状态，或者调用yieId()变成可运行状态
    * 阻塞状态
      - 通过运行时调用 sleep 或者 join 进入阻塞状态，在 sleep 或 join 结束后，又变成可运行状态
    * 等待状态
      * 通过运行时调用 wait() 方法来进入等待状态。在被 notify() / notifyAll() 方法唤醒后进入锁定状态。
    * 锁定状态
      * 通过运行时 Synchronize 进入阻塞状态，当获得锁的时候变成可运行状态
  * 运行结束
* Synchronized 和 lock 对比
  * Synchronized
    * 当加载普通方法上时锁定的是对象本身 this，当加载静态方法的时候锁定的是 class 文件
    * 锁的释放不能手动执行，只能等待方法执行结束或者是抛异常的时候才能释放
  * lock
    * 可以手动释放锁，但是在抛异常的时候不会释放锁
    * 可以通过newCondition()来对锁进行更细粒度的控制
* Synchronized和volatile对比
  * Synchroized
    * 即保证线程之间的可见性，也保证执行的原子性。
  * volatile
    * 只保证线程之间的可见性，出现线程之间的可见性问题是因为线程有自己的私有缓存，Threadlocal.当线程从主内存中将数据复制到自己的本地缓存后，对于主内存数据的改变就不在感知。这样会出现可见性问题。
    * volatile：是在修改被volatile修饰的数据后，会发送一个lock开头的指令，将更新后的数据重新写回到主内从中，并且根据缓存一致性协议通知其他线程更新自己的本地缓存。
* 无锁同步 cas
  * cas 是无锁保证线程安全的乐观锁实现，核心原理为在修改 k 值得时候会给一个预期值 n，和修改值 m 。只有当 k 的值和预期值 n 相同的时候才去修改 k 的值为 m。这样保证了只有在其他线程没有修改的时候去修改数据。
  * 问题：ABA 问题。如果预期值为 A ，有线程把这个值从 A 修改为 B 后，又修改成了 A 就会出现问题。解决方案是，添加版本号即可解决。
* 并发容器的选择
  * 单列集合
    * Collections.synchronizedXXX()：通过这个方法来返回一个线程安全的容器，效率一般
    * ConcurrentLinkedQueue:支持高并发的队列
    * BlockingQueue：阻塞队列
      * LinkedBlockingQueue
      * ArrayBlockingQueue
      * DelayQueue
    * CopyOnWriteArrayList：只对写加锁同步，适合读多写少场景
  * 双列集合
    * Collections.synchronizedXXX()：通过这个方法来返回一个线程安全的容器，效率一般
    * hashtabl：效率一般
    * ConcurrentHashMap：分段锁机制保证了高性能
    * ConcurrentSkipListMap：跳表结构，可以保证顺序同时也高性能
* 创建线程的方法
  * 集成 Thread
  * 实现 Runnable
  * 实现 Callable
* 控制线程运行流程
  * join 方法
  * CountDownLatch
  * CyclicBarrier
  * Executors.newSingleThreadExecutor()