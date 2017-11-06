### maven构建

```mvn

./mvn clean package
```

### 启动

```java
java -jar target/visbody-service.jar
```


### Docker镜像创建和启动
```mvn
./mvnw install dockerfile:build
```

```docker
docker run -d --name visbody-service -p 8080:8080 lazy/visbody-service:latest
```
