# IO流

-----------
1. 分类：
  - 输入流，输出流（以数据流向分类）
  -  字符流，字节流（以数据格式分类）
2. 作用：
  - 可以把数据存储到文件中，也可以从文件中读取数据
  - 使用场景：文件复制， 上传文件，下载文件
3. 输出流FileWriter
  * 构造方法：

    * FileWriter（String fileName）：传递一个文件名称
  * 输出流写数据的步骤：
    1. 创建输出流对象 
    ```java
    	FileWriter fw=new FileWriter(d:\\a.txt);
    	//1.调用系统资源创建了一个文件(要求：路径合法，权限正常)
    	//	FileWriter的构造方法有要求，要求这个文件的路径是合法，是存在的，权限是允许的，当这个路径合法，
    	//	那么，如果这个文件不存在FileWriter会帮你创建这个文件，如果这个文件已经存在，
    	//	那么FileWriter则直接指向这个文件
    	//2.创建输出流对象
    	//3.把输出流对象指向文件
    ```
    2. 调用输出流对象的写数据方法

      ```java
      //写入一个字符串数据，但是只是写入到缓冲区
      writer(String str);
      //刷新该流的缓冲区，可以将缓冲区的数据写入到文件
      flush();
      ```

    3. 释放资源

      ```java
      //通知系统释放和该文件相关的资源，该方法自带flush（）功能
      close();
      ```

  - 相对路径：相对当前项目而言的，在项目的根目录下
  - 绝对路径：以盘符开始的路径
  - close()和flush()方法的区别：
  	* flush():刷新缓冲区
  	* close()：先刷新缓冲区，然后再通知系统释放资源。流对象不可以再被使用了

  * FileWriter写数据的方法：
  	* void write(String str):写一个字符串数据
  	* void write(String str,int index)：写一个字符串数据的一部分
  	* void write(int ch)：写一个字符数据，这里写int类型的好处是既可以char类型的数据，也可以写char类型对象的int类型数据
  	* void write(char[] chs)：写一个字符数组数据
  	* void write(char[] chs,int index,int len)：写一个字符数组的一部分
4. 输入流FileReader：
  * 构造方法：

    - FileReader（String fileName）
  * 输入流读数据的步骤：
    1. 创建输入流，绑定文件源

      ```java
      FileReader fw=new FileReader("d:\\a.txt");
      /*	1.调用系统资源创建了一个文件(要求：路径合法，权限正常)
      		FileWriter的构造方法有要求，要求这个文件的路径是合法，是存在的，全新啊是允许的，当这个路径合法，
      		那么，如果这个文件不存在FileWriter会帮你创建这个文件，如果这个文件已经存在，
      		那么FileWriter则直接指向这个文件
      	2.创建输出流对象
      	3.把输出流对象指向文件	*/
      ```

    2. 调用输入流对象的读入方法

      ```java
      //创建一个int类型的变量存储read读出来的ansic码
      read():
      //创建一个char[] 类型的数组来存储read读出来的字符数组
      //同时创建一个int类型的长度
      read(char[] chr):
      ```

    3. 释放资源

      ```java
      close():
      ```

  * 注意:

    *  在调用read（chs）方法的时候，下一次读到的数据会覆盖前一次的数据，但是有时候，下一次的数据不能完全覆盖前一次的内容，那么会造成垃圾的出现。所以我们要明确每次读到字符的个数来过滤垃圾
5. 缓冲流：
  * 分类：
  	输出流和输入流
  	BufferedWriter
  	BufferedReader
  * 构造方法：
  	- BufferedWriter bw=new BufferedWriter(new FileWriter("a.txt"));
  	- BufferedRander bw=new BufferedRander(new FileRander("a.txt"));
  	因为缓冲流的速度极快，所以大部分使用的Buffer流。
  		® 基本流：可以读写
  		® 包装流：不可以读写，只是有了一些特点。
  	- 缓冲流的特殊功能：
  		* void newLine():写一个换行符，会根据系统自定义更换
  		* void readLine():一次读取一行的数据，但是不读取换行符
  	- 输入流和输出流指向同一个源文件的时候，之后当一个流关闭之后，另外一个流才能对这个源文件进行绑定
