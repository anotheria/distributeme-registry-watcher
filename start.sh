#!/bin/bash
export VERSION=1.0.5-SNAPSHOT

CLASSPATH=test/appdata:target/distributeme-registrywatcher-$VERSION-jar-with-dependencies.jar
echo CLASSPATH: $CLASSPATH
java -Xmx256M -Xms64M -classpath $CLASSPATH -Dconfigureme.defaultEnvironment=dev $*
