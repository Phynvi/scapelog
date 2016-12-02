#!/bin/sh

pwd=`pwd`

# git checkout master

# cd to core module
cd ../core

# create fat jar
mvn -Daction=collect clean package -e

cd ${pwd}

# run step 1
java -jar lib/proguard.jar @conf/client_step1.conf

# run string obfuscator
java -classpath ../core/target/core-1.0-SNAPSHOT-jar-with-dependencies.jar:lib/string-obfuscator.jar com.scapelog.obfuscator.Obfuscator tmp/client_step1.jar tmp/client_step2.jar

# run step 2
java -jar lib/proguard.jar @conf/client_step2.conf

echo ""
echo ""
echo "done"