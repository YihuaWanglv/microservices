# Spring boot开发微服务

## 1. 为什么使用spring boot？

- 1) spring boot有pivotal和netfix背书，是一套完整的企业级应用的开发方案，天然集成分布式云架构spring-cloud。
- 2) spring-boot的完全抛弃以往java项目配置文件过多的“陋习”，开启一个项目只需几行代码。
- 3) Sprinboot允许项目使用内嵌的tomcat像启动普通java程序一样启动一个web项目.由于有了这个特性，项目不再需要打war包部署到tomcat，而是打成jar包，直接使用java -jar命令启动.


## 2. 开始第一个springboot项目
在一切开始之前，我们首先要知道如何开始一个springboot项目。

### 2.1. 创建一个空的maven项目，pom.xml中添加maven依赖
```
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
</dependencies>
```

### 2.2. 编写项目启动入口App.java
```
@SpringBootApplication
public class App 
{
    public static void main(String[] args) throws Exception {
        SpringApplication.run(App.class, args);
    }
}
```

ok! done!
这样就已经能直接使用spring boot了.
启动App.java，spring boot就会使用内置的tomcat直接在本机的8080端口开启一个服务。

### 2.3. 再进一步，为应用引入spring mvc
```
@Controller
public class SampleController {

    
    @RequestMapping("/")
    @ResponseBody
    String home() {
        String data = "";
        return "Hello World!";
    }
}
```


启动App.java，访问localhost:8080, 即可遇见“Hello World!”