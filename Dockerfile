FROM openjdk:8-jre-aplpine

ADD . /gloat/build/libs
WORKDIR /gloat/build/libs/

EXPOSE 8080 9000

CMD ["java -jar gloat-0.0.1-SNAPSHOT.jar"]
