# Mac 系统开发环境搭建

------------------

### 安装 Jdk 以及开发工具

* 安装 Jdk

  1. [下载 Jdk 安装包](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
  2. 双击打开后进行正常安装
* 安装开发工具 idea
  1. [下载 Idea](https://www.jetbrains.com/idea/download)
  2. 双击打开之后拖动到 Application 文件夹中进行安装
  3. [激活方法](https://www.jianshu.com/p/3c87487e7121)
  4. [破解包相关](http://idea.lanyus.com/)

### 安装常用软件

* Maven : 直接[下载 tar ](https://maven.apache.org/download.cgi)包进行解压，配置对应的 setttings 配置文件即可

* Git : 官网[下载](https://git-scm.com/download)对应的安装包进行安装即可

* Office : 通过 [office365](www.office.com) 提供的下载链接进行下载安装包安装。

* 解压软件 ： 下载解压[软件](https://theunarchiver.com/)安装即可

* 浏览器 ： 通过对应官网下载安装 [火狐](http://www.firefox.com.cn/) [谷歌](https://www.google.cn/intl/zh-CN/chrome/)。

* 远程连接工具  ：下载 [iterm2](https://www.iterm2.com/downloads.html) 进行安装, 连接命令

  ```she
  ssh -p <port> <username>@<ip>
  ```

* 文件传输工具 ： 下载 [FileZilla](https://filezilla-project.org/) 进行操作，使用方式和 XFTP 一致。

* 文本编辑器 ： 下载 [Sublime](http://www.sublimetext.com/3) 来进行文本编辑

* 数据库连接工具 ： 下载 [Navicat Premiun](https://www.navicat.com.cn/download/navicat-premium) ,进行[激活](https://www.navicat.com.cn/download/navicat-premium)

### 常用开发软件

* 安装 brew

  1. 执行命令

     ```shell
     /usr/bin/ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"
     ```

  2. 通过 brew 管理软件

     * 常用命令

       | 功能 | 命令                    |
       | ---- | ----------------------- |
       | 安装 | brew install XXX        |
       | 启动 | brew services start XXX |
       | 停止 | brew services stop XXX  |
       | 卸载 | brew uninstall XXX      |
       | 查找 | brew search XXX         |
       | 列表 | brew list XXX           |

     * 软件安装路径

       | 路径             | 地址              |
       | ---------------- | ----------------- |
       | 软件安装路径     | /usr/local/Cellar |
       | 软件配置文件路径 | /user/local/etc   |
       | 可执行程序的连接 | /usr/local/bin    |

       