#!/usr/bin/env bash

awk -F '<[^>]*>' '/<dependencies>/,/<\/dependencies>/{next} /<plugins>/,/<\/plugins>/{next} /version/ {$1=$1;print "Version is:" $0}' pom.xml

#VERSION=$(get_xpath_value './pom.xml' 'project/version')

#echo "pom.xml version: $VERSION"

# export VERSION to the GitHub env
echo "POM_VERSION=$VERSION" >> "$GITHUB_ENV"