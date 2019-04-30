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
  	git commit -a -m [message]
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

# 进阶操作

* 多ssh账户配置

  1. 创建多个公私匙对。用于不用账户验证。

  2. 将对应公匙部署到对应的服务上

  3. 在~/.ssh/目录系下创建config文件

     ```yaml
     # user1
     Host codehub.devcloud.huaweicloud.com   //决定你git@xxx的xxx部分，包括克隆链接部分也要进行修改
     	HostName codehub.devcloud.huaweicloud.com   //服务的域名
     	User dncba    //你想要显示的用户名
     	IdentityFile ~/.ssh/id_rsa_application_1    //该服务对应的私匙
     # user2
     Host wlcb
     	HostName codehub.devcloud.huaweicloud.com
     	User dncba
     	IdentityFile ~/.ssh/id_rsa_application_2
     # user3
     Host github.com     
     	HostName github.com
     	User dncba
     	IdentityFile ~/.ssh/id_rsa_application_3
     ```

  4. 测试连通性

     ```shell
     ssh -T git@Host          //Host为你配置的host
     ```

* 高级操作

  | 命令                  | 含义                                     |
  | --------------------- | ---------------------------------------- |
  | Git merge branchName  | 将指定分支 branchName 合并到当前的分支上 |
  | Git rebase branchName | 将当前分支的提交在指定分支上进行重演     |
  | Git checkout hash     | 将 HEAD 移动到指定的位置                 |
  | Git checkout HEAD^    | 相对当前位置移动 HEAD 的位置             |
	| Git cherry-pick hash  | 将指定的提交拿到当前分支                 |
  
  







