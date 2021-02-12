#!/usr/bin/env bash

function get_xpath_value {
    if [ -f "$1" ]; then
        value=$( xpath "$1" "$2" 2>/dev/null | perl -pe 's/^.+?\>//; s/\<.+?$//;' )
        echo -n "$value"
    else
        echo "Invalid xml file \"$1\"!"
        exit 1;
    fi
}

VERSION=$(get_xpath_value '../../pom.xml' 'project/version')

# export VERSION to the GitHub env
echo "VERSION=$VERSION" >> "$GITHUB_ENV"