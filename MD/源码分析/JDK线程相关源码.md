# JDK 线程相关源码

---------------------

## 线程基本知识

* 创建线程
  1. 集成Thread类，重写Run()方法
  2. 实现Runnable接口，实现Run()方法
  3. 实现Callable接口，实现Call()方法
* 线程通讯
  * 共享内存
  * wait() / notify()
* 线程的状态
  * 初始化状态：可以通过start()方法变成可运行状态
  * 可运行状态：等待系统调度变成可运行状态
  * 运行状态：运行完成后变成运行结束状态，或者调用yieId()变成可运行状态
    - 阻塞状态
      - 通过运行时调用 sleep 或者 join 进入阻塞状态，在 sleep 或 join 结束后，又变成可运行状态
    - 等待状态
      - 通过运行时调用 wait() 方法来进入等待状态。在被 notify() / notifyAll() 方法唤醒后进入锁定状态。
    - 锁定状态
      - 通过运行时 Synchronize 进入阻塞状态，当获得锁的时候变成可运行状态
  * 运行结束

## 线程池相关

* Executor

  * execute(Runnable command )

* ExecutorService

  * submit()
  * invokeAll()
  * shutdown()

* ThreadPoolExecutor

  ```java
      public Future<?> submit(Runnable task) {
          if (task == null) throw new NullPointerException();
          RunnableFuture<Void> ftask = newTaskFor(task, null);
          execute(ftask);   //执行作业
          return ftask;
      }
  
   public void execute(Runnable command) {
          if (command == null)
              throw new NullPointerException();
  
          int c = ctl.get();
          if (workerCountOf(c) < corePoolSize) {    //如果当前线程数比核心线程数少
              if (addWorker(command, true))			//增加线程，执行当前作业
                  return;
              c = ctl.get();        //获取当前线程数
          }
          if (isRunning(c) && workQueue.offer(command)) {      //如果达到核心线程，就往队列里放
              int recheck = ctl.get();
              //如果队列满了，就增加线程，直到线程达到最大线程数
              if (! isRunning(recheck) && remove(command))		
                  reject(command);
              else if (workerCountOf(recheck) == 0)
                  addWorker(null, false);
          }
          else if (!addWorker(command, false))   //如果线程已经达到最大，队列已经满了，直接拒绝
              reject(command);
      }
  
  ```

  

  

