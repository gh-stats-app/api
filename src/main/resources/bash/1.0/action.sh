#!/usr/bin/env bash

curl -s -X POST https://gh-stats.app/actions -H "x-reporter: bash@1.0" -H "content-type: application/json" --data "{ \"repository\": \"$GITHUB_REPOSITORY\", \"action\": \"$GITHUB_ACTION\" }"
