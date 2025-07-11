#!/usr/bin/env bash

script_dir=$(dirname "$0")

export AGENT_APPLICATION="${script_dir}/.."

#export MAVEN_PROFILE=enable-mcp

"$script_dir/support/check_env.sh" || exit 1

cd "$AGENT_APPLICATION"
mvn -Dmaven.test.skip=true spring-boot:run
