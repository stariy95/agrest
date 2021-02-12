#!/usr/bin/env bash

VERSION="undefined"
grep ../../pom.xml "<version>"

echo "::set-output name=pom-version::$VERSION"
echo "TEST_VAR=4.4-SNAPSHOT" >> $GITHUB_ENV