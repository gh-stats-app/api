#!/usr/bin/env bash

curl -X POST https://gh-stats.app/actions/stats -H "x-bash-version: 1.0" -H "content-type: application/json" --data "{ \"repository\": \"$GITHUB_REPOSITORY\", \"action\": \"$GITHUB_ACTION\" }" -i
