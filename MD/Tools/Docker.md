### Docker

------------------

- Docker简介

  - Docker 是一个开源的应用容器引擎，让开发者可以打包他们的应用以及依赖包到一个可移植的镜像中，然后发布到任何流行的 Linux或Windows 机器上，也可以实现虚拟化。容器是完全使用沙箱机制，相互之间不会有任何接口。

- Docker基础

  1. 软件安装

     - [下载](https://www.docker.com/products/docker-desktop)
     - [安装](https://www.runoob.com/docker/macos-docker-install.html)

  2. 基本概念

     1. ![整体架构](https://docs.docker.com/engine/images/engine-components-flow.png)

     2. ![](https://docs.docker.com/engine/images/architecture.svg)

     3. 使用容器需要内核支持的技术

        1. namespaces名称空间

           | namespace | 系统调用参数  | 隔离内容                   | 内核版本 |
           | --------- | ------------- | -------------------------- | -------- |
           | UTS       | CLONE_NEWUTS  | 主机名和域名               | 2.6.19   |
           | IPC       | CLONE_NEWIPC  | 信号量、消息队列和共享内存 | 2.6.19   |
           | PID       | CLONE_NEWPID  | 进程编号                   | 2.6.24   |
           | Network   | CLONE_NEWNET  | 网络设备、网络栈、端口等   | 2.6.29   |
           | Mount     | CLONE_NEWNS   | 挂载点（文件系统）         | 2.4.19   |
           | User      | CLONE_NEWUSER | 用户和用户组               | 3.8      |

        2. controlGroup

           - Blkio:块设备IO
           - cpu：CPU
           - cpuacct：CPU资源使用报告
           - cpuset：多处理平台上的CPU集合
           - devices：设备访问
           - memory：内存用量及报告
           - perf_event:对cgroup中的任务进行统一性测试
           - net_cls:cgroup中的任务创建的数据报文的类别标识符

  3. 基本命令

     - container
     - image
     - volume
     - network