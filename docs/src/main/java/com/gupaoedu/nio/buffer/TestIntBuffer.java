package com.gupaoedu.nio.buffer;

import java.nio.IntBuffer;

public class TestIntBuffer {
	public static void main(String[] args) {  
        // 分配新的int缓冲区，参数为缓冲区容量  
        // 新缓冲区的当前位置将为零，其界限(限制位置)将为其容量。它将具有一个底层实现数组，其数组偏移量将为零。  
        
		//分配了8个长度的int数组
		IntBuffer buffer = IntBuffer.allocate(8);  

//		capacity //数组的长度，容量
		
        for (int i = 0; i < buffer.capacity(); ++i) {  
            int j = (i + 1);  
            // 将给定整数写入此缓冲区的当前位置，当前位置递增  
            buffer.put(j);  
        }  
  
        // 重设此缓冲区，将限制设置为当前位置，然后将当前位置设置为0
       //固定缓冲区中的某些值，告诉缓冲区，
        //我要开始操作了，如果你再往缓冲区写数据的话
        //不要再覆盖我固定状态以前的数据了
        buffer.flip();
  
        // 查看在当前位置和限制位置之间是否有元素  
        while (buffer.hasRemaining()) {  
            // 读取此缓冲区当前位置的整数，然后当前位置递增  
            int j = buffer.get();  
            System.out.print(j + "  ");  
        }
	}  
}
