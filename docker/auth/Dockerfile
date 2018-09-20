FROM openjdk:8-jre-alpine

ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS \
    JAVA_OPTS="-Xmx128m"

RUN adduser -D -s /bin/sh coopel

# Create and grant log directory
RUN mkdir -p /var/log/coopel && chown coopel:coopel -R /var/log/coopel

WORKDIR /home/coopel

ADD entrypoint.sh entrypoint.sh
RUN chmod 755 entrypoint.sh && chown coopel:coopel entrypoint.sh
USER coopel

ADD auth.jar app.jar

RUN mkdir -p config
ADD application-dev.yml config/application-dev.yml

ENTRYPOINT ["./entrypoint.sh"]

EXPOSE 22022