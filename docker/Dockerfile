FROM openjdk:8-jre-alpine

ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS \
    JAVA_OPTS="-Xmx128m"

RUN adduser -D -s /bin/sh coopel
WORKDIR /home/coopel

ADD entrypoint.sh entrypoint.sh
RUN chmod 755 entrypoint.sh && chown coopel:coopel entrypoint.sh
USER coopel

ADD ../auth/target/auth-*.jar app.jar

ENTRYPOINT ["./entrypoint.sh"]

EXPOSE 22022

