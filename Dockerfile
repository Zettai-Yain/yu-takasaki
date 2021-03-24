FROM openjdk:15-alpine
WORKDIR /home/app
COPY build/libs/yu-takasaki-*-all.jar /home/app/application.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/home/app/application.jar"]
