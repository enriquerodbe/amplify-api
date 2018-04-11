FROM enriquerodbe/sbt-play:1.1.2_2.6.13 as dist
COPY . /dist
WORKDIR /dist
RUN sbt dist && unzip target/universal/amplify-api-1.0-SNAPSHOT.zip -d /app

FROM openjdk:8-jre-alpine
RUN apk add --no-cache bash
COPY --from=dist /app /app
WORKDIR /app
ENV HTTP_PORT 9000
CMD ["bin/amplify-api", "-Dpidfile.path=/dev/null", "-Dhttp.port=${HTTP_PORT}"]
EXPOSE $HTTP_PORT
