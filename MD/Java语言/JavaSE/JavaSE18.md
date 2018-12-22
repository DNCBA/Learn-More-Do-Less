1.网络通讯的三要素
		IP地址:同一个网络中的计算机唯一标识
				InetAdderss类:
						构造方法:
							Static InetAdderss getLocalHost():返回本地主机
							Static InetAdderss getByName(String name):根据主机名或者ip地址返回ip地址						
						方法:
							Static InetAdderss getLocalHost():返回本地主机
							String getHostName():返回IP地址的主机名
							String getHostAddress():返回IP地址字符串					
		端口号:计算机中应用程序的唯一标识
		协议
2.UDP协议
		格式:
			1.创建socket对象,采用UDP协议的Socket类是DatagramSocket()
					构造方法:
							DatagramSocket()创建一个数据包的套接字对象,使用的端口是随机可用端口
							DatagramSocket(int import)创建一个数据包的套接字对象,指向指定端口
					方法:
						Send(DatagramPacket ):发送指定数据包
						Receive(DatagramPacket):接收指定数据包
			2.创建数据包对象用来发送或者接收DatagramPacket()
					构造方法:
							DatagramPacket(byte[] buf,int length):创建指定长度为length的接收数据包
							DatagramPacket(byte[] buf,int length,InetAdderss adderss,int import):创建指定长度和指定发送地址指定端口的发送数据包
					方法:
						InetAdderss getAdderss():获取发送端电脑IP
						Byte[] getData():获取发送的数据(可以使用String(byte[],sta,length)来构造一个String对象)
						Int getLength():获取发送数据的长度
						Int getPotr():获取端口号
			3.调用Socket对象的方法完成数据的发送或者接收
					Recevie(DatagramPacket):
					Send(DatagramPacket):
			4.解析数据
					采用DatagramPacket的方法来解析数据
						getAdderss():返回IP地址
						getData():返回数据
						getLength():返回数据长度
			5.释放资源
					调用Socket类的close方法,释放资源
		注意事项:
				1.如果端口错误是不会出现错误的,
				2.如果启动了一个接收端在启动第二个接收端就会出错
3.TCP/IP协议
			格式:
				1.创建Socket对象,TCP/IP协议客户端使用的是Socket类,服务器端使用的是ServerSocket()对象
						构造方法:
								Scoket(InetAdderss adderss,int import):创建一个指定ip地址和指定端口的流套接字对象
								ServerSocket(int import):创建一个绑定特定端口的流套接字对象
						方法:
							Socket类的方法:
								getInputStream():返回此套接字的输入流
								getOutputStream():返回此套接字的输出流
								getInetAdderss():返回套接字的连接地址
							ServerSocket类的方法:
								Socket Accept():侦听并接收返回套接字.
				2.利用Socket的方法,获取此套接字的输入输出流:
							getInputStream():返回此套接字的输入流
							getOutputStream():返回此套接字的输出流
				3.使用输入流和输出流的读和写方法来读取或者发送数据:
							InputStream()方法:read(byte[] b,int off,int len):从输入流中读取一定量的字节数,off起始位置,len长度
							OutputStream()方法:write(byte[] b,int off,int len):将制定byte数组中从偏移量off开始的len个字节写入流中
							OutputStream()方法:flush(0方法:刷新次输出流并强制写出所有缓冲的输出字节
				4.释放资源,关流和关套接字
				
		问题:
			Java.net.connectException:connection timed out connectip地址有问题
