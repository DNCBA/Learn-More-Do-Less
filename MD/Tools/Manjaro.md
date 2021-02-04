## Manjaro 系统安装和环境搭建

### 系统安装
 - 系统下载

   https://manjaro.org/get-manjaro/
  
 - 系统安装
 
   使用 balenaEtcher 刻录镜像文件到 U 盘,使用 U 盘进行安装
   
 - 安装备注
 
   根据提示正常设置参数,在安装到最后联网情况下会卡 93% 可以采取断网或者换源解决。
    
### 软件安装

 - 换 pacman 源,选择一个即可
 
 ```bash
sudo pacman-mirrors -i -c China -m rank
sudo pacman -Syyu

```
 - 添加 arch 源,非必须
 ```bash
 vi /etc/pacman.conf
 
 # add Text End
 [archlinuxcn]
 Server = http://mirrors.163.com/archlinux-cn/$arch
 
 
 sudo pacman -Sy archlinuxcn-keyring
```
 
 - 安装输入法
 ```bash
pacman -S fcitx5 fcitx5-qt fcitx5-gtk fcitx5-configtool

vi ~/.xprofile
#add Text
export GTK_IM_MODULE=fcitx5
export QT_IM_MODULE=fcitx5
export XMODIFIERS="@im=fcitx5"

export LANG="zh_CN.UTF-8"
export LC_CTYPE="zh_CN.UTF-8"

pacman -S fcitx5-rime
yay -S rime-cloverpinyin

vi ~/.local/share/fcitx5/rime/default.custom.yaml
#add Text
patch:
  "menu/page_size": 8
  schema_list:
    - schema: clover
   
sudo pacman -S yay 
yay -S fcitx5-pinyin-zhwiki-rime

```
 
 - 磁盘管理
 ```bash
 #查看文件系统占用情况
df -h
 #查看文件大小数据
du -h [path]
 #查看磁盘数据
fdisk -l
 #磁盘挂载将 path1 挂载到 path2
mount [path1] [path2]
 #磁盘卸载
umount [path]
 #磁盘格式化[例:mkfs -t ext3 /dev/hdc6]
mkfs [-t 文件系统格式] path
 #多磁盘开机自动挂载
vi etc/fstab

```

 - 文件权限管理
```bash
 #查看文件信息
lx -l
 #改变文件权限
chmod 777 path
 #改变文件所有者
chown userName:groupName path

```
 
 - 用户管理
 ```bash
 #用户组
groupadd groupName
groupdel groupName
cat /etc/group
newgrp gropName
#添加指定用户到对应的组
gpasswd -a userName groupName 
#删除指定用户在对应的组
gpasswd -d userName groupName
 
 #用户管理
useradd [-g groupName] userName
userdel [-r] userName
cat /etc/passwd
sudo passwd [userName]

```
 
 - ssh
 ```bash
 #开启ssh登录
systemctl enable sshd.service
systemctl start sshd.service
systemctl restart sshd.service

 #远程连接
ssh [-p port] userName@host

 #ssh密匙登录
 #client 端生成密匙对
ssh-keygen
 #服务器端配置公匙数据
vi ~/.ssh/authorized_keys << rsa.pub
 #服务器端ssh配置
vi ~/.ssh/sshd_config
 #add Text
RSAAuthentication yes  //开启rsa认证
PubkeyAuthentication yes  //开启密匙认证
PasswordAuthentication no  //关闭账号密码登录
AuthorizedKeysFile .ssh/authorized_keys  //公匙认证地
PermitRootLogin no //禁止root远程登录
Port 22 //暴露的端口
 #客户端配置config
vi ~/.ssh/config
 #add Text
Host alisName
    HostName host
    Port 22
    User dncba
    IdentityFile ~/.ssh/id_rsa
 #测试连接
ssh -T alisName
ssh alisName

```
 
 - 常用软件
 ```bash
sudo pacman -S google-chrome
sudo pacman -S SimpleScreenRecorder

```
 
 - docker
 ```bash
sudo pacman -S docker
#启动docker服务
sudo systemctl start docker 
#查看docker服务装填
sudo systemctl status docker
#设置docker开机启动
sudo systemctl enable docker
#重启docker服务
sudo systemctl restart docker 


#不需要sudo 需要创建对应group
groupadd docker
#添加当前用户到docker组
gpasswd -a userName docker

```
 
 - 桌面美化
 ```bash
 #下边栏
yay -S latte-dock

#部件中 > 应用程序面板

```

### 包管理工具
 - pacman & yay
 ```bash
 #软件查找
 pacman -Ss keyWord
 pacman -Qs keyWord

 #软件安装
 pacman -S packageName1 packageName2
 pacman -U localPackage
 #软件卸载
 pacman -R packageName
 pacman -Rs packageName
 #软件信息
 pacman -Qi packageName
 pacman -Ql pacakgeName
 
 #缓存管理
 pacman -Sc
 
 #软件更新
 pacman -Syu

```
 
 