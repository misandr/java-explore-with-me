FROM amazoncorretto:11
COPY service/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
ENV TZ="Europe/Moscow"
