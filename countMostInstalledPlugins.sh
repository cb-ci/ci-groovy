#! /bin/bash

#NOTE: The result is not correct, because not all of the plugins have the ` .stats.installations` json data
curl -s https://plugins.jenkins.io/api/plugins | \
jq '[.plugins[] | select(.stats.installations[-1].total != null) | {name, installs: .stats.installations[-1].total}] | sort_by(-.installs) '