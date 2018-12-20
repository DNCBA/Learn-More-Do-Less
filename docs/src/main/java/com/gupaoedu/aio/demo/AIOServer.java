package com.gupaoedu.aio.demo;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AIOServer {

	private int port = 8080;
	
	public AIOServer(int port){
		this.port = port;
	}
	
	public void listen(){
		try{
			AsynchronousServerSocketChannel  server = AsynchronousServerSocketChannel.open();
			server.bind(new InetSocketAddress(this.port));
			
			server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {

				@Override
				public void completed(AsynchronousSocketChannel client, Object attachment) {
					ByteBuffer buffer = ByteBuffer.allocate(1024);
					client.read(buffer);
					buffer.flip();
					System.out.println(new String(buffer.array()));
				}

				@Override
				public void failed(Throwable exc, Object attachment) {
					
				}
				
			});
		
		}catch(Exception e){
			
		}
		
		
		try {
			Thread.sleep(Integer.MAX_VALUE);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	public static void main(String[] args) {
		new AIOServer(8080).listen();;
	}
	
}
