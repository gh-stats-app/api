#!/usr/bin/env bash

GITHUB_ACTION=$(echo $GITHUB_ACTION_PATH | sed "s%.*/_actions/%%" | sed "s%\(^[^\/]*\/[^\/]*\/[^\/]*\).*%\1%" | sed 's/\(.*\)\//\1@/') && curl -s -X POST https://gh-stats.app/actions -H "x-reporter: bash@1.0" -H "content-type: application/json" --data "{ \"repository\": \"$GITHUB_REPOSITORY\", \"action\": \"$GITHUB_ACTION\" }"
