# Docker作为服务运行容器

## 1. docker运行环境

### 1.1 操作系统选择
对于linux系统，Docker 需要安装在 64 位的平台，并且内核版本不低于 3.10。 CentOS 7 满足最低内核的要求.
这里选用centos7系统作为docker的运行环境.

在windows系统下，可以使用vm虚拟机虚拟centos7，运行docker.

### 1.2 linux操作系统配置

#### 1.2.1 防火墙
centos7使用firewall作为防火墙，docker向外提供服务，需要开通必要端口。
firewall开放端口命令：
```
#开放8080端口
firewall-cmd --zone=public --add-port=8080/tcp --permanent
#重启防火墙
firewall-cmd --reload
```

当然，如果习惯于iptables防火墙，也可以关闭firewall，启用iptables防火墙：
```
#1、关闭firewall：
systemctl stop firewalld.service #停止firewall
systemctl disable firewalld.service #禁止firewall开机启动
#查看默认防火墙状态（关闭后显示notrunning，开启后显示running）
firewall-cmd --state 

#2、iptables防火墙（这里iptables已经安装，下面进行配置）
$ vi /etc/sysconfig/iptables #编辑防火墙配置文件

*filter
:INPUT ACCEPT [0:0]
:FORWARD ACCEPT[0:0]
:OUTPUT ACCEPT[0:0]
-A INPUT -m state--state RELATED,ESTABLISHED -j ACCEPT
-A INPUT -p icmp -jACCEPT
-A INPUT -i lo -jACCEPT
-A INPUT -p tcp -mstate --state NEW -m tcp --dport 22 -j ACCEPT
-A INPUT -p tcp -m state --state NEW -m tcp --dport 80 -jACCEPT
-A INPUT -p tcp -m state --state NEW -m tcp --dport 8080-j ACCEPT
-A INPUT -j REJECT--reject-with icmp-host-prohibited
-A FORWARD -jREJECT --reject-with icmp-host-prohibited
COMMIT
:wq! #保存退出
```

## 2. 安装并启动docker

从2017-03-01起，新版的docker分为了CE和EE两个版本，CE是社区版，EE是企业版. 我们这里使用CE版即可.

### 2.1 安装docker-ce

#### 2.1.1. Set up the repository

Set up the Docker CE repository on CentOS:
```
sudo yum install -y yum-utils

sudo yum-config-manager \
    --add-repo \
    https://download.docker.com/linux/centos/docker-ce.repo

sudo yum makecache fast
```
#### 2.1.2. Get Docker CE

Install the latest version of Docker CE on CentOS:
```
sudo yum -y install docker-ce
```
Start Docker:
```
sudo systemctl start docker
```

docker开机启动：
```
systemctl  enable docker.service
```


#### 2.1.3. Test your Docker CE installation

Test your installation:
```
sudo docker run hello-world
```

### 2.2 添加镜像加速
假如你有阿里云账号，可配置镜像加速
```
vim /etc/docker/daemon.json
```
添加：
```
{
    "registry-mirrors": ["https://xxxxxxxx.mirror.aliyuncs.com"]
}
```
"https://xxxxxxxx.mirror.aliyuncs.com"
是你的专属镜像加速地址，可以在阿里云管理页面找到.

重启docker：
```
systemctl restart docker
```

## 3. docker镜像操作和docker容器运行使用

### 3.1 拉取镜像，并启动容器

```
# 查看当前有什么镜像
docker images
# 拉取centos系统镜像
docker pull centos
# 启动刚刚拉取的镜像
docker run -it centos /bin/bash
```

### 3.2 运行docker容器时的一些常用命令和选项

- 列出当前运行中的容器
```
docker ps
```
- 如果要列出所有状态（包括已停止）的容器，添加-a参数
```
docker ps -a
```
- 进入运行中的容器
```
docker attach 容器id
```
- 停止容器
```
docker stop 容器id
```
- 删除容器
```
docker rm 容器id
```
- 删除镜像
```
docker rmi 镜像名称
```

- 将宿主机上的磁盘挂载到容器中，也即“目录映射”
```
docker run -i -t -v /home/software:/mnt/software centos /bin/bash
```
“-v /home/software:/mnt/software”表示将容器的/mnt/software目录挂载到宿主机的/home/software目录.

## 4. 手工制作java镜像

### 4.1 上传java rpm安装包到/home/software目录

这里使用已下载好的java8 64位安装包：jdk-8u65-linux-x64.rpm

### 4.2 启动容器
```
docker run -i -t -v /home/software:/mnt/software centos /bin/bash
```

### 4.3 运行安装包
/mnt/software映射到宿主机的/home/software，说明容器内的/mnt/software已有jdk-8u65-linux-x64.rpm文件，直接rpm运行安装java8
```
cd /mnt/software
rpm -ivh jdk-8u65-linux-x64.rpm
```

### 4.4 查看是否安装成功
```
java -version
```

### 4.5 提交镜像
再打开一个终端，查看当前运行的容器
```
$docker ps
CONTAINER ID        IMAGE                                                COMMAND                  CREATED             STATUS              PORTS                     NAMES
3443c1097867        127.0.0.1:5000/com.iyihua/spring-boot-docker:1.0.0   "/bin/sh -c 'java ..."   6 days ago          Up 6 days           0.0.0.0:18101->8101/tcp   objective_shannon

```
获取容器id(3443c1097867)，提交镜像
docker commit 3443c1097867 iyihua/java

### 4.6 验证镜像
```
docker run -rm iyihua/java java -version
```
"-rm"参数表示不想保留容器，运行结束后即删除退出

## 5. 使用Dockerfile构建镜像

Dockerfile就是把手工构建镜像的过程写成一段自动执行的脚本，最终生成镜像。

### 5.1 Dockerfile构建java镜像

也就是把之前手工构建的java镜像的步骤放到脚本里，脚本如下：
```
FROM centos:latest
MAINTAINER "iyihua"<wanglvyihua@gmail.com>
ADD jdk-8u65-linux-x64.rpm /usr/local
RUN rpm -ivh /usr/local/jdk-8u65-linux-x64.rpm
CMD java -version
```
这个Dockerfile顺利运行要求Dockerfile所在宿主机目录含有一个准备好的java安装包jdk-8u65-linux-x64.rpm。

- 如果构建的镜像与之前构建过的镜像的仓库名、标签名相同，之前的镜像的仓库名和标签名就会更新为<none>. 我们可以使用docker tag命令来修改镜像仓库名和标签名。
```
docker tag 3443c1097867 iyihua/java:1.0.0
```

## 6. 使用Docker Registry管理镜像

我们默认就是从Docker Hub下载公共镜像。官方的Docker Hub也为我们提供了一个私有仓库，可以让内部人员通过这个仓库上传下载内部镜像，不过免费用户只能创建一个私有仓库。

不过，我们可以通过Docker Registry开源项目，在内部搭建一个私有镜像注册中心。

### 6.1 注册登录Docker Hub

通过浏览器注册登录Docker Hub，手动创建一个私有仓库。

然后我们就可以通过客户端login并push镜像到仓库。

登录：
```
docker login
```

推送镜像：
```
docker push iyihua/java
```

### 6.2 搭建Docker Registry

#### 6.2.1 启动
通过docker本身的镜像，就可以简单的在本地搭建起Docker Registry：
```
docker run -d -p 5000:5000 --restart=always --name registry \
  -v `pwd`/data:/var/lib/registry \
  registry:2
```
这样就会在127.0.0.1:5000的地址启动起Docker Registry服务.

- 参数说明：
    （1）-d表示后台运行
    （2）-p是宿主机与容器的端口映射
    （3）-v是宿主机与容器的目录映射，也即目录挂载

#### 6.2.2 重命名镜像标签
docker push默认的镜像中心是Docker Hub，没有指明目标地址的镜像，其完整的镜像名称是“docker.io/iyihua/java”.
如果我们打算将iyihua/java推送到本地的Docker Registry，则需要将镜像名称修改为127.0.0.1:5000/iyihua/java.

使用docker tag命令更名：
```
docker tag 3443c1097867 127.0.0.1:5000/iyihua/java
```

使用docker push命令推送：
```
docker push 127.0.0.1:5000/iyihua/java
```




## 7. Spring Boot与Docker整合


Spring Boot与Docker整合的目标是构建spring boot应用程序时可同时生成Docker镜像，并将此镜像推送至Docker Registry，整个构建过程依然使用maven来完成

现在假定已有一个普通spring boot应用spring-boot-docker.

### 7.1 为spring boot程序添加Dockerfile
在resources目录下添加Dockerfile：
```
FROM java
MAINTAINER "iyihua"<wanglvyihua@gmail.com>
ADD spring-boot-docker-1.0.0.jar app.jar
EXPOSE 8101
CMD java -jar app.jar
```

### 7.2 使用maven构建Dockerfile
在pom文件中添加docker相关插件：
```
<plugin>
    <groupId>com.spotify</groupId>
    <artifactId>docker-maven-plugin</artifactId>
    <version>0.4.10</version>
    <configuration>
        <imageName>${docker.registry}/${project.groupId}/${project.artifactId}:${project.version}</imageName>
        <dockerDirectory>${project.build.outputDirectory}</dockerDirectory>
        <resources>
            <resource>
                <!-- <targetPath>/</targetPath> -->
                <directory>${project.build.directory}</directory>
                <include>${project.build.finalName}.jar</include>
            </resource>
        </resources>
    </configuration>
</plugin>
```
需要添加的属性配置
```
<properties>
    <docker.registry>127.0.0.1:5000</docker.registry>
</properties>
```

### 7.3 构建并推送
```
mvn docker:build docker:push
```

### 7.4 docker容器启动应用
```
docker run -d -p 18101:8101 127.0.0.1:5000/com.iyihua/spring-boot-docker:1.0.0
```

- p参数指明宿主机和容器的端口映射
- d参数指明要后台运行

### 7.5 调整docker容器内存
查看docker容器运行情况
```
docker stats
```

运行应用时调整内存限制
```
docker run -d -p 18101:8101 -m 512m 127.0.0.1:5000/com.iyihua/spring-boot-docker:1.0.0
```

- m参数指明内存调整为多少

- demo代码可以在这里获取：
[spring-boot-docker sample项目](https://github.com/YihuaWanglv/spring-boot-docker)

或者：
[microservices/spring-boot-docker](https://github.com/YihuaWanglv/microservices/tree/master/services/spring-boot-docker)


## 附：常见问题：

### (1)docker iptables failed no chain/target/match by that name

重启docker即可:
```
systemctl restart docker
```

### (2)当docker run centos，出现：centos exec user process caused "permission denied"
需要加一个参数：--privileged

结果命令变为：
```
docker run --privileged -i -t centos /bin/bash
```

说明：
```
大约在0.6版，privileged被引入docker。
使用该参数，container内的root拥有真正的root权限。
否则，container内的root只是外部的一个普通用户权限。
privileged启动的容器，可以看到很多host上的设备，并且可以执行mount。
甚至允许你在docker容器中启动docker容器。
```

建议：
```
如果总是需要privileged才能正常运行docker，那么可能你安装的docker可能有问题，建议重新安装最新的docker-ce，将不再需要privileged参数.
```

### (3)docker build cannot allocate memory

这个问题的终极解决办法，还是重启docker，或者重启服务器；


