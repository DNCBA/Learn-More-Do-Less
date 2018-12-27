# Git

-------------

## 安装

* 下载

  >[git](https://www.git-scm.com/download/)
  >
  >[tortoisegit](https://tortoisegit.org/download/)

* 安装

  * 注意下安装的路径即可

## 常用操作

* 配置ssh

  1. 首次使用先设置用户名和邮箱

     ```shell
     git config --global user.email "****@**.com"
     git config --global user.name "xxxx"
     ```

  2. 查看是否已经存在密钥(如果存在但是不想用可以删除rm -rf)

     ```shell
     cd ~/.ssh
     ls
     ```

  3. 生成密匙(连按三个回车)

     ```shell
     ssh-keygen -t rsa -C "*****@**.com"
     ```

  4. 在C:\Users\Administrator\\.ssh目录下就生成好了

  5. 将公匙配置在对应的账户中即可。

* 克隆仓库和初始化仓库

  ```shell
  git clone [url]
  git init [project-name]
  ```

* 其他常用操作

  ```shell
  #add
  	#添加当前目录所有文件
      git add .
      #添加制定目录包括子目录
      git add [dir]
  #commit
  	git commit -m [message]
  #branch
  	#显示所有分支
  	git branch -a
  	#新建分支
  	git branch [branch-name]
  	#切换分支
  	git checkout [branche-name]
  #tag
  	#查看所有tag
  	git tag
  	#新建一个tag
  	git tag [tag] 
  	#提交所有的tag
  	git push [remote] --tags
  #查看
  	git log
  #remote
  	#退送到远程分支
  	git push [remote] [branch]
  	git push [remote] [branch] --force
  	#拉取远程分支
  	git pull [remote] [branch]
  	#更新本地仓库
  	git fetch
  #回滚
  	#本地回滚
  	git reset --hard [commit]
  	#本地远程回滚
  	git revert [commit]
  ```

## 



