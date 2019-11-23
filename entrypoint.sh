#!/bin/sh
exec java ${JAVA_OPTS} \
-Djava.security.egd=file:/dev/./urandom \
-DTEMP_FOLDER=/temp \
-XX:+UnlockExperimentalVMOptions \
-XX:+UseCGroupMemoryLimitForHeap \
-jar "/opt/app/app.jar" "$@"