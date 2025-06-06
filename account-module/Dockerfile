FROM bellsoft/liberica-openjdk-alpine:21

WORKDIR /app
COPY build/libs/account.jar account.jar

EXPOSE 8081

ENTRYPOINT ["sh", "-c", "java -jar account.jar"]