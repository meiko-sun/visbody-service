### gradle构建

```gradle

./gradlew build
```

### maven构建

```mvn

./mvnw package
```

### 启动

```java
java -jar build/libs/visbody-service.jar
```


### Docker镜像创建和启动
```gradle
./gradlew build buildDocker
```

```docker
docker run -p 8080:8080 -t lazy/visbody-service:latest
```
