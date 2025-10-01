FROM alpine:3.20

RUN apk add --no-cache android-tools

ENV ANDROID_ADB_SERVER_PORT=5037
ENTRYPOINT ["adb"]
CMD ["devices","-l"]