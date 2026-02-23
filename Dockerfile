FROM eclipse-temurin:21-jre-jammy
# OS 타임존 설정
ENV TZ=Asia/Seoul
# JVM 타임존 설정 (둘 다 설정하는 것이 가장 확실함)
ENV JAVA_TOOL_OPTIONS="-Duser.timezone=Asia/Seoul"

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
# JAVA_OPTS 환경변수를 인식하도록 수정
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -Dspring.profiles.active=prod -jar /app.jar"]