1.对象操作流
			序列化ID:未明确的时候系统会根据成员变量计算版本号,则当你改变成员变量的时候对应的ID会改变
			我们正常的时候会生成固定的ID.
2.编码集
		把计算机底层的二进制数据转换成我们能看见的字符
		ASCII
		GB2312---GBK
		Unicode 所有的字符都占2个字节
		UTF-8:长度可变的码表
		ISO-8859-1:不支持中文的编码表
		ANSI:本地编码表跟系统的环境语言有关系  中文简体用:GBK
		Java中的字符默认使用ANSI编码
		乱码:编码保持前后一致即可解决
		什么时候使用编码表?
					字符和字节之间转换的时候就要使用编码表
						将字符串转换成字节数组
							.getBytes[]:将字符串转换为字符数组,使用的是ANSI本地编码表,GBK,也可以使用用指定的编码表进行转换
						将字节数组转换成字符串
							String(byte[] ,String charsetName)
		字符流=字节流+编码
3.Properties
			实现了Map<>接口,继承自Hashtable(),每一个键和值都是String类型的
			Map<K,V>(接口)
					Hashtable<K,V>:线程安全的,键只能是非null对象
							Properties:键和值都是String
					HashMap<K,V>:线程不安全的,键可以是null			
			构造方法:
					Properties()
			特有功能:
					setProperty(String key,String vablue):给properties集合对象中添加映射关系
					String getProperty(String key):根据键获取对象的值
					
					Load(inputStream inStream):使用给定的流读取文件中的数据到指定的properties集合中
					Load(Reader reader):使用给定的字符流将文件中的
					Void store(Writer witer,String coment):使用给定的流将集合中的数据写入到指定文件中
					Void store(OutputStream os,String coment):使用字节流将集合中的映射元素写入到指定文件中					
			使用场景:
					作为配置文件
					
					配置文件:将程序中更可能出现变化的数据放到文件中,程序运行的时候直接从文件中读取就可以了
					         好处:提高程序的扩展性和可维护性					
4.多线程
		1.线程:进程中的一个执行控制单元,执行路径
				 进程:当前正在运行的程序,一个应用程序在内存中的执行区域
					一个进程可以有一个线程,也可以有多个线程
				单线程:安全性高,效率低
				多线程:安全性低,效率高			
		2.线程的实现
				1.继承Thread(线程类)类并且重写run()方法,接下来创建线程并启动线程
						thread类:
								方法:String getName():获取当前线程的名字 
									Void setName(String name):设置当前线程的名字
						CPU执行程序的随机性.						
						方式:
							A:定义一个类,继承Thread类(线程类)
								Class A extends Thread{
									Public void run(){};
								}	
							B:重写Thread类中的run方法
							C:创建线程类对象
								A a=new A();
							D:开启了线程
								a.start();
						注意:当调用的是start方法.系统会调用run方法
				2.实现Runable接口并且重写run()方法,然后实例化对象,在创建thread是作为一个参数进行传递
						Thread(Runable target)
						Static Thread currentThread():返回当前线程对象						
						方式:
							A:定义一个类,实现Runable接口(任务类)
							B:重写Runable中的run方法
								Class A implements Runnable{
									Public void run(){
									}
								}								
							C:创建任务类对象
								A a=new A():
							D:创建线程对象,并将任务对象作为参数传递
								Thread t=new Thread(a);
							E:启动线程
								t.start():								
					方式1和方式2的区别:
							区别1:方式1继承一个类,不能再继承其他的类
								方式2实现接口,豪科技继承其他的类								
							区别2:方式2线程和任务分离
								方式1线程类即使任务类							
							区别3:方式2中的任务类不能直接使用线程类的方法
								方式1可以直接使用线程类的方法								
						注意:以后创建线程推荐使用方式2						
		3.多线程会出现安全问题
			A:发生的原因
				多个线程共享数据
			B:解决的方法			
				Synchronized:同步,可以修饰代码块和方法,被修饰的代码块和方法,一旦被某个线程锁访问,则直接被锁住,其他线程无法访问
				同步代码块
						Synchronized(锁对象){
							//可能出现问题的代
						}
						注意:锁对象需要被所有的线程所共享,多个线程使用的锁对象必须是同一个对象
						同步:安全性高,效率低
						非同步:效率高,安全性低
				同步方法
					使用关键字synchronized修饰的方法,一旦被一个线程访问则整个方法全部锁住,其他对象则无法访问这个方法
					同步方法:
							修饰符 synchronized 返回值类型 方法名(参数列表){
								//可能出现安全问题的代码
							}
					注意:
						同步方法的锁对象是this
								局限性:锁对象是this,当多个线程没有共享同一份this的话,这里就不是同步的了
						静态方法的锁对象是当前类的字节码对象
				同步代码块和同步方法的区别:
									同步代码块的锁对象由开发人员指定
									同步方法的锁对象已经定死了
											如果方法是非静态方法则锁对象是this
											如果方法时静态方法则锁对象是当前类的的字节码对象
				死锁:
					线程A需要先后锁住1和2,线程B需要先后锁住2和1,当A锁住了1,B锁住了2的时候就会发生死锁
