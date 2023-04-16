FROM amazoncorretto:11
COPY client/target/*.jar clients-app.jar
COPY service/target/*.jar service-app.jar
COPY app.sh app.sh

ENTRYPOINT ["/app.sh"]
#ENV TZ="Europe/Moscow"
