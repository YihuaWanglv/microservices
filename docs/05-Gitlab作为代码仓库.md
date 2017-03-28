# Gitlab作为代码仓库


## 1. Gitlab的Docker安装

gitlab安装的过程稍微麻烦，但如果使用docker安装的话，就会非常简单。

下载镜像：
```
docker pull gitlab/gitlab-ce
```

下载镜像，并启动容器：
```
docker run -d -m 512m -h gitlab.iyihua.com -p 22:22 -p 80:80 -v ~/gitlab/etc:/etc/gitlab -v ~/gitlab/log:/var/log/gitlab -v ~/gitlab/opt:/var/opt/gitlab --name gitlab gitlab/gitlab-ce
```

说明：
(1) -h， 设置gitlab访问域名
(2) -p， 指定映射端口，22表示ssh端口,80表示http端口，
(3) -m， 指定目录映射
(4) -v， 指定分配多少内存来运行容器

启动后，访问首页gitlab.iyihua.com，会定向到修改管理员密码页面，修改完管理员密码后，会重定向到登陆页面。



## 2. git使用

gitlab安装好后，和github的使用并无二致。

git本地使用需要设置好git全局设置
```
$ git config --global user.name "John Doe"
$ git config --global user.email johndoe@example.com
```

如果要用ssh拉取和提交代码，需要设置好ssh公钥
```
ssh-keygen -t rsa -C "admin@example.com"

#可通过以下命令查看ssh key：
cat ~/.ssh/id_rsa.pub

把得到的key进入gitlab ssh密钥管理界面，输入这个ssh key.
```


## 3. more

gitlab运行需要的资源比较多，一个800m的虚拟机用docker跑gitlab比较吃力，经常会出现页面502.建议最低内存是1G。

如果仅仅是个人使用，可以直接使用github代替，或者使用GOGS代替.



