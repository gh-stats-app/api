#!/usr/bin/env bash

curl -X POST https://gh-stats.app/actions/stats -H "content-type: application/json" --data "{ \"repository\": \"$GITHUB_REPOSITORY\", \"action\": \"$GITHUB_ACTION\" }" -i
