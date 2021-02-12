#!/usr/bin/env bash

function get_xpath_value {
    if [ -f "$1" ]; then
        value=$( xpath "$1" "$2" | perl -pe 's/^.+?\>//; s/\<.+?$//;' )
#        2>/dev/null
        echo "$value"
    else
        echo "Invalid xml file \"$1\"!"
        exit 1;
    fi
}

awk '/depend/{flag=1}flag&&/artifactId|version/{next}1' pom.xml

VERSION=$(get_xpath_value './pom.xml' 'project/version')

echo "pom.xml version: $VERSION"

# export VERSION to the GitHub env
echo "POM_VERSION=$VERSION" >> "$GITHUB_ENV"