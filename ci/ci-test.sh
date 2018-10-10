#!/bin/sh
set -xe

echo `pwd`

./gradlew checkstyle jacoco
