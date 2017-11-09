## 项目介绍

体测仪器服务端接口项目

### maven构建

```mvn

./mvn clean package

```
在targe目录下生成项目jar文件，可以用于部署到服务器

### 启动

```java
java -jar target/visbody-service.jar
```


### Docker镜像创建和启动
```mvn
./mvnw install dockerfile:build
```

```docker
docker run -d --name visbody-service -p 8085:8080 lazy/visbody-service:latest
```

#### 项目日志文件夹路径

/data/logs

#### 注意事项

不同分支application.properties需要不同设置

dev分支

spring.profiles.active=dev

test分支

spring.profiles.active=test

master分支

spring.profiles.active=prod


