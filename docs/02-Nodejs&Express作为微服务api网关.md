# Nodejs&Express作为微服务api网关


## 1. 安装Node、NPM和Express

安装Nodejs后，npm会一起安装，然后npm install express安装Express.

## 2. 使用Express框架开发web应用

express使用例子：
```
var express = require('express');
var port = 1234;
var app = express();
app.use(express.static('.'));
app.listen(port, function(){
    console.log('server is running at %d', port);
});
```

express进行简易路由：
```
app.get('/hello', function(req, res){
    res.send('Hello');
});
```

## 3. 搭建Nodejs集群环境

利用服务器的多核CPU，让每个CPU都运行一个Node.js进程，例子：
```
var cluster = require('cluster');  
var express = require('express');  
var numCPUs = require('os').cpus().length;

if (cluster.isMaster) {  
    for (var i = 0; i < numCPUs; i++) {
        // Create a worker
        cluster.fork();
    }
} else {
    // Workers share the TCP connection in this server
    var app = express();

    app.get('/', function (req, res) {
        res.send('Hello World!');
    });

    // All workers use this port
    app.listen(8080);
}
```

## 4. 使用Node.js实现反向代理，作为统一服务网关

使用Node作为api网关，原理是利用Node的http-proxy模块来启动代理服务器，实现反向代理。
例子：
```
var http = require('http');
var httpProxy = require('http-proxy');
var PORT = 3000;

var proxy = httpProxy.createProxyServer();
proxy.on('error', function(err, req, res){
    res.end();//当代理的请求发生错误时，输出空白的相应数据
});

var app = http.createServer(function(req, res) {
    proxy.web(req, res, {
        target: 'http://localhost:8080' //代理的目标地址
    });
});
app.listen(PORT, function(){
    console.log('server is running at %d', port);
});

```

- 下一步，就是服务的注册和发现，Nodejs网关将需要增加服务发现的功能


