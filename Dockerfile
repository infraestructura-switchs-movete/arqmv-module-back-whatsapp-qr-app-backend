FROM amazoncorretto:17-al2-jdk

WORKDIR /apl/
COPY target/bot-0.0.1.jar .

RUN mkdir -p /apl/files/
RUN mkdir -p /apl/tmp/
RUN rm /etc/localtime
RUN ln -s /usr/share/zoneinfo/America/Bogota /etc/localtime
RUN echo 'alias ll="ls -lha"' >> ~/.bashrc

EXPOSE 8080
ENTRYPOINT java $JAVA_OPTIONS -jar /apl/bot-0.0.1.jar --spring.servlet.multipart.location=/apl/tmp -Dlog4j2.formatMsgNoLookups=true $JAR_OPTIONS