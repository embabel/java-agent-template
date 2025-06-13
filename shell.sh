#!/usr/bin/env bash

./scripts/support/check_env.sh

export SPRING_PROFILES_ACTIVE=shell,severance
mvn -Dmaven.test.skip=true spring-boot:run
