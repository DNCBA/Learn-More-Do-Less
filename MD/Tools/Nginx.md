# Nginx

--------------

## Nginx 简介

* nginx是一个静态服务器，同时也是一个反向代理服务器。其支持对服务器的负载均衡。在前后分离部署的时候，也可以通过代理来解决跨域问题。

## Nginx 使用

* 安装

  * [下载](http://nginx.org/en/download.html)

  * 解压 tar -xzvf nginx-1.x.x.tar.gz

  * 配置安装路径 ./configuration  --prefix = /install /dir

  * 编译安装 make & make install

  * 启动关闭服务  

    ```shell
    sh nginx -c /config/nginx.conf   //启动
    sh nginx -s stop   //停止作业
    sh nginx -s reload //重启作业
    ```

  * 常见缺少的库

    ```shell
    yum install pcre-devel
    yum install openssl-devel
    yum install zlib-devel
    ```

* 基本配置

  ```javascript
  worker_processes  1;  //配置工作的线程数
  
  events {
      use epoll  //事件驱动模型
      worker_connections  1024;   //允许链接的数量
  }
  
  http {
      include       mime.types;  //文件扩展名与文件类型映射表
      default_type  application/octet-stream;   //默认文件类型
      access_log off   //日志服务开关
      log_format myFormat '$remote_addr'  //自定义日志文件格式
      access_log log/access.log.myFormat   //设置日志格式
      sendfile        on;
      keepalive_timeout  65;   //连接超时时间
  
      upstream mysvr{
          ip_hash   //iphash模式
          server 192.168.0.1:8080 weight=10;  //权重策略
          server 192.168.0.2:8080;
          server 192.168.0.3:8090;
      }
      
      server {
          listen       8090;  //监听端口
          server_name  localhost;    //监听地址
          location / {    //请求的url
              root   html/dist;   //根目录
              index  index.html index.htm;   //默认页面配置
              proxy_pass  http://mysvr;      //请求转向定义的地址
              deny 192.168.0.2；   //拒绝的ip
              allow 192.168.0.3;   //允许的ip
          }
          error_page   500 502 503 504  /50x.html;  //错误页面
          location = /50x.html {
              root   html;
          }
  	}
  }
  ```

## Nginx进阶

- 静态服务器

  - Nginx 本身就可以作为高性能的静态服务器来进行使用

    ```javascript
    server {
    	listen 80; //监听的端口
    	server_name static.com;  //虚拟服务器名称
        
        location / {
            root /path;  //本地静态资源的路径
            index index.html; //主页面名称
        }
    }
    ```

- 反向代理负载均衡

  - Nginx 因为其采用的多线程和 I/O 多路复用技术使其具备高性能，并且其可以方便的将请求路由到不同的服务器上，所以也经常被使用做反向代理服务器

    ```javascript
    upstream myserver { //默认路由策略采用轮询
    	ip_hash; // 路由策略采用iphash
    	server ip:port wight=10;  //路由策略采用权重
    	server ip:port;
    }
    
    server {
    	listen 80;
    	server_name proxy.server;
    	
    	location / {
    		proxy_pass http://myserver;
    	}
    }
    
    ```

- 配置详解

  - location 匹配规则

    ```javascript
    //优先级 精准匹配 > 正则匹配 > 普通匹配（最长优先）
    //语法 location [=|~|~*|^~] uri {}
    // = 精准匹配
    // ~ 正则匹配区分大小写
    // ~* 正则匹配不区分大小写
    // ^~ 头匹配匹配最多的部分 比正则优先级高
    // 空 普通匹配，最长字符串匹配
    
    location = / {
    	return 1;
    }
    
    localtion / {
    	return 2;
    }
    
    location ~ \.[js|jpg|css|html]${
        return 3;
    }
    
    localtion ^~ /api/cc/aa {
        return 4;
    }
    
    
    host/  -> 1
    host   -> 2
    host/a.js -> 3
    host/api/cc/aa/dd.js -> 4
    
    ```

  - 压缩缓存

    ```javascript
    //压缩和缓存可以有效提升用户使用体验
    //压缩配置
    server{
        gzip on;  //开启压缩
        gzip_min_length 1k;  //最小的压缩大小
        gzip_comp_level 1;   //压缩等级
        gzip_types application/javascript test/css text/xml;  //压缩的类型
        gzip_vary on; //再相应头里面增加accept_tye gzip
        
        listen 80;
        server_name abc.com;
        
        location / {
            root /path;
        }
    }
    
    //缓存配置
    server{
        listen 80;
        server_name abc.com;
        
        lcation ~ \.[png|css|js]$ {
            root /path;
            expires 30s;   //设置缓存时间
        }
    }
    
    ```

  - rewrite 转发+防盗链

    ```javascript
    //语法 rewrite regex replacement [flag];
    //regex 为uri匹配的对应的正则
    //replacement为对应替换的内容
    //flag为对应的配置 
    //last:停止rewrite检测，如果没有匹配到，会继续匹配location
    //break:停止rewrite检测，如果没有匹配到直接404
    //redirect 302临时重定向
    //permanent 301永久重定向
    
    //案例
    
    
    //正则替换
    server{
        listen 80;
        server_name abc.com;
        
        location / {
            rewrite ^/api/(.*)$ /$1 break;  //()用于匹配括号之间的内容，用 $1 $2 进行调用
        	proxy_pass http://abc.com
        }
        
        location ~ \.[js|htm|css]$ {
            rewrite ^/static/ /new-static/ break;
        }
    }
    
    //if语句判断（可以使用全局变量 $hsot $uri $args $request_method)
    server{
        listen 80;
        server_name localhost;
        
        if($host ~ www.baidu.com)  //正则匹配
            rewrite ^/(.*) /api/$1 break;
        	proxy_pass 
        }
    
    	if($request_method = GET){  //数据匹配
            rewrite ^http\:\/\/(.*) https:// break; 
        }
    }
    
    //防盗链
    server{
        listen 80;
        server_name localhost;
        
        location ~* \.[js|css]${
            valid_referers none blocked deame1 demo2;
            if($valid_referers){
                return 404;
            }
        }
    }
    
    
    
    ```

* 前后台分离部署解决跨域配置

  * 核心思想：通过nginx拦截所有发送给后台的请求。将这些请求的url获得到之后再进行代理转发给对应的后台。

    ```javascript
    //前台代码部署在locahost:80端口,代码放在html/dist里
    //后台代码部署在remote:port，请求统一以api开头
    worker_processes  1;  //配置工作的线程数
    events {   
        use epoll  //事件驱动模型   
        worker_connections  1024;   //允许链接的数量
    }
    http {
        include       mime.types;
        default_type  application/octet-stream;
        sendfile        on;
        keepalive_timeout  65;
    
        upstream mysvr{
            server remote:port;  
        }
        
        server {
            listen       80;
            server_name  localhost;
            location / {
                root   html/dist;
                index  index.html index.htm;
            }
            
            location /api{
                rewrite ^/api/(.*)$ $1 break;
                proxy_pass http://mysvr
            }
            
            error_page   500 502 503 504  /50x.html;
            location = /50x.html {
                root   html;
            }
    	}
    }
    
    ```