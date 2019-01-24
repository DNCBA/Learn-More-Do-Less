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
          server 192.168.0.1:8080;
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

* 负载均衡配置

  * 负载策略
    * 默认为轮询机制，还可以选择权重，iphash，urlhash等方式

  ```javascript
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
          ip_hash   //iphash模式
          server 192.168.0.1:8080 weight=10;  //权重策略
          server 192.168.0.2:8080;
          server 192.168.0.3:8090;
      }
      
      server {
          listen       8090;
          server_name  localhost;
          location / {
              proxy_pass  http://mysvr;
              root   html/dist;
              index  index.html index.htm;
          }
          error_page   500 502 503 504  /50x.html;
          location = /50x.html {
              root   html;
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
                rewrite ^/api/(.*)$/$1 break;
                proxy_pass http://mysvr
            }
            
            error_page   500 502 503 504  /50x.html;
            location = /50x.html {
                root   html;
            }
    	}
    }
    
    ```