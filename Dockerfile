FROM hseeberger/scala-sbt:8u151-2.12.5-1.1.2

ENV PLAY_SECRET secret

COPY . /app

WORKDIR /app

RUN sbt universal:packageZipTarball

RUN tar xzf target/universal/amplify-api-1.0-SNAPSHOT.tgz

EXPOSE 9000

CMD amplify-api-1.0-SNAPSHOT/bin/amplify-api -Dplay.crypto.secret=${PLAY_SECRET}
