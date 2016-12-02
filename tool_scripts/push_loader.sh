#!/bin/sh
git checkout master

rsync -aqzhe ssh --progress --checksum --timeout=10 out/ScapeLog.jar deployer@scapelog:/opt/scapelog/static/ScapeLog.jar
