#!/usr/bin/env bash

if [ ! -d "target" ]; then
    mvn clean package dependency:copy-dependencies
fi

JGROUPS_BIND_ADDR="127.0.0.1"

JAVA_OPTS="-Xmx2g -Xms2g -Djava.net.preferIPv4Stack=true -Djgroups.bind_addr=${JGROUPS_BIND_ADDR}"
java $JAVA_OPTS -cp "target/*:target/dependency/*" "org.jboss.benchmark.BenchMark" ${@}