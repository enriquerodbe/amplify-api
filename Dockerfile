FROM mozilla/sbt:8u181_1.2.7 as dist
ARG version=1.0-SNAPSHOT
COPY build.sbt /dist/
COPY project/ /dist/project/
WORKDIR /dist
RUN sbt update
COPY . /dist
RUN sbt -Damplify.api.version=$version dist &&\
 unzip target/universal/amplify-api-$version.zip -d /app

FROM openjdk:8-jre-alpine
RUN apk add --no-cache bash
COPY --from=dist /app /app
WORKDIR /app
ENV HTTP_PORT 9000
CMD bin/amplify-api -Dpidfile.path=/dev/null -Dhttp.port=$HTTP_PORT
EXPOSE $HTTP_PORT
