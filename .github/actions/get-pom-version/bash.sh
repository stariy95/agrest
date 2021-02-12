#!/usr/bin/env bash

cat pom.xml | grep "version" | sed -e 's/version//g' | tr -d '<>/' | grep "^[0-9]"

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

VERSION=$(get_xpath_value './pom.xml' 'project/version')

echo "$VERSION"

# export VERSION to the GitHub env
echo "POM_VERSION=$VERSION" >> "$GITHUB_ENV"