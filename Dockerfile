FROM hseeberger/scala-sbt:8u151-2.12.5-1.1.2

ENV APPLICATION_SECRET secret
ENV HTTP_PORT 9000

COPY . /dist
WORKDIR /dist

RUN sbt dist &&\
 unzip target/universal/amplify-api-1.0-SNAPSHOT.zip -d /app &&\
 rm -rf /dist

WORKDIR /app
CMD bin/amplify-api -Dhttp.port=${HTTP_PORT}

EXPOSE $HTTP_PORT
