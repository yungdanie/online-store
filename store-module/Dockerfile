FROM bellsoft/liberica-openjdk-alpine:21

WORKDIR /app
COPY build/libs/store.jar store.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -jar store.jar"]