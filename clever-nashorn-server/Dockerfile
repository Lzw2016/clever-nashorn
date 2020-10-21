FROM harbor.yyjzt.com/basis/jdk:latest
ADD target/clever-nashorn-server-0.0.1-SNAPSHOT.jar app.jar
ADD ./run.sh /
RUN chmod +x /run.sh

ENTRYPOINT [ "sh", "-c", "/run.sh" ]
