FROM java:8

COPY . /idear
WORKDIR /idear
RUN ./gradlew buildPlugin

