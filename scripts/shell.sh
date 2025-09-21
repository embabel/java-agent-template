#!/usr/bin/env bash

script_dir=$(dirname "$0")

export AGENT_APPLICATION="${script_dir}/.."

"$script_dir/support/agent.sh"