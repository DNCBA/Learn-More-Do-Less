package com.gupaoedu.nio.buffer;

import java.io.*;  
import java.nio.*;  
import java.nio.channels.*;  
  
public class DirectBuffer {  
    static public void main( String args[] ) throws Exception {  
    	
    	//首先我们从磁盘上读取刚才我们写出的文件内容
        String infile = "E:/GP_WORKSPACE/test.txt";  
        FileInputStream fin = new FileInputStream( infile );  
        FileChannel fcin = fin.getChannel();  
          
        //把刚刚读取的内容写入到一个新的文件中
        String outfile = String.format("E:/GP_WORKSPACE/testcopy.txt");  
        FileOutputStream fout = new FileOutputStream( outfile );      
        FileChannel fcout = fout.getChannel();  
          
        // 使用allocateDirect，而不是allocate
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);  
          
        while (true) {  
            buffer.clear();  
              
            int r = fcin.read(buffer);  
              
            if (r==-1) {  
                break;  
            }  
              
            buffer.flip();  
              
            fcout.write(buffer);  
        }
	}  
}  
