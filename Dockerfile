FROM daocloud.io/java:latest
#更改时区为上海时间
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
#上传文件临时保存目录
RUN mkdir -p /data/work/device
#日志目录
RUN mkdir -p /data/logs
VOLUME /tmp
#拷贝项目jar包
ADD target/visbody-service-0.1.0.jar app.jar
ENV JAVA_OPTS=""
#暴露端口
EXPOSE 8080
#启动项目
ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar
