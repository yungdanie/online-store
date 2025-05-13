FROM bellsoft/liberica-openjdk-alpine:21

WORKDIR /app
COPY build/libs/online-store.jar online-store.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -jar online-store.jar"]