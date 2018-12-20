package com.gupaoedu.bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class BIOServer {
	
	ServerSocket server;
	//服务器
	public BIOServer(int port){
		try {
			//把Socket服务端启动
			server = new ServerSocket(port);
			System.out.println("BIO服务已启动，监听端口是：" + port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 开始监听，并处理逻辑
	 * @throws IOException 
	 */
	public void listener() throws IOException{
		//死循环监听
		while(true){
			//虽然写了一个死循环，如果一直没有客户端连接的话，这里一直不会往下执行
			Socket client = server.accept();//等待客户端连接，阻塞方法
			
			//拿到输入流，也就是乡村公路
			InputStream is = client.getInputStream();
			
			//缓冲区，数组而已
			byte [] buff = new byte[1024];
			int len = is.read(buff);
			//只要一直有数据写入，len就会一直大于0
			if(len > 0){
				String msg = new String(buff,0,len);
				System.out.println("收到" + msg);
			}
		}
	}
	
	
	public static void main(String[] args) throws IOException {
		new BIOServer(8080).listener();
	}
	
}
