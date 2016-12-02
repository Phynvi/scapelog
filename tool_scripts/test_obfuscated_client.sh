#!/bin/sh
java -javaagent:out/client.jar -cp ../core/target/dependencies/*:out/client.jar com.scapelog.client.ScapeLog
