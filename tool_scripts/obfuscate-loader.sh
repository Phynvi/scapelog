#!/bin/sh

git checkout master

pwd=`pwd`
jdk16="/media/data/programs/linux/jdk1.6.0_38/bin/java"

# cd to loader module
cd ../loader

# create fat jar
mvn clean package -e

cd ${pwd}

# run step 1
java -jar lib/proguard.jar @conf/loader_step1.conf

# run string obfuscator
java -classpath ../loader/target/loader-1.0-SNAPSHOT-jar-with-dependencies.jar:lib/string-obfuscator.jar com.scapelog.obfuscator.Obfuscator tmp/loader_step1.jar tmp/loader_step2.jar

# run step 2
java -jar lib/proguard.jar @conf/loader_step2.conf

echo ""
echo ""
echo "done"