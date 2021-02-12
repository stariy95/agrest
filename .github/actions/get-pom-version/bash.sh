#!/usr/bin/env bash

function get_xpath_value {
    xml=$1
    path=$2
    if [ -f "$xml" ]; then
        # xpath returns <foo>value</foo>, so we need to unpack it
        value=$( xpath "$xml" "$path" 2>/dev/null | perl -pe 's/^.+?\>//; s/\<.+?$//;' )
        echo -n "$value"
    else
        echo "Invalid xml file \"$xml\"!"
        exit 1;
    fi
}

pom_xml='../../pom.xml'

VERSION=$(    get_xpath_value $pom_xml 'project/version'    )

echo "::set-output name=pom-version::$VERSION"
echo "TEST_VAR=4.4-SNAPSHOT" >> "$GITHUB_ENV"