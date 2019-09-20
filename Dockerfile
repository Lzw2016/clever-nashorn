FROM 172.18.1.1:15000/openjdk:8u222-jre as dev
ADD clever-nashorn-server/target/clever-nashorn-server-*-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=9066", "--server.address=0.0.0.0"]
EXPOSE 9066

FROM 172.18.1.1:15000/openjdk:8u222-jre as prod
ADD clever-nashorn-server/target/clever-nashorn-server-*-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=9066", "--server.address=0.0.0.0"]
EXPOSE 9066
