#!/bin/sh
set -xe

echo `pwd`

./gradlew checkstyleMain jacoco
