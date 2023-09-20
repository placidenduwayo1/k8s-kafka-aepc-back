FROM maven:3.8.3-openjdk-17-slim AS builder
EXPOSE 8085
ARG JAR_FILE=target/k8s-kafka-aepc-ms-config-service.jar
ADD ${JAR_FILE} k8s-kafka-aepc-ms-config-service.jar
RUN java -Djarmode=layertools -jar k8s-kafka-aepc-ms-config-service.jar extract

FROM maven:3.8.3-openjdk-17-slim
ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "org.springframework.boot.loader.JarLauncher"]
COPY --from=builder dependencies/ ./
COPY --from=builder spring-boot-loader/ ./
COPY --from=builder snapshot-dependencies/ ./
COPY --from=builder application/ ./
