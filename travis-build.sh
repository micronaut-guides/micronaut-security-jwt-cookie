#!/bin/bash
set -e

export EXIT_STATUS=0

./gradlew -Dgeb.env=chromeHeadless complete:test || EXIT_STATUS=$?

echo "exit status $EXIT_STATUS"

if [[ $EXIT_STATUS -ne 0 ]]; then

  echo "Test Failed, running whole test suite ignoring failures"

  ./gradlew -Dgeb.env=chromeHeadless -DIGNORE_FAILURES=true complete:test

  git clone https://${GH_TOKEN}@github.com/micronaut-guides/micronaut-security-jwt-cookie.git -b gh-pages gh-pages --single-branch > /dev/null

  cd gh-pages

  mkdir -p reports

  cp -r ../complete/build/reports/. ./reports/

  git add reports/*

  git commit -a -m "Updating reports for Travis build: https://travis-ci.org/$TRAVIS_REPO_SLUG/builds/$TRAVIS_BUILD_ID" && {
    git push origin HEAD || true
  }
  cd ..
  rm -rf gh-pages

  exit $EXIT_STATUS
fi

if [[ $EXIT_STATUS -eq 0 ]]; then

    curl -O https://raw.githubusercontent.com/micronaut-projects/micronaut-guides/master/travis/build-guide
    chmod 777 build-guide

    ./build-guide || EXIT_STATUS=$?

    curl -O https://raw.githubusercontent.com/micronaut-projects/micronaut-guides/master/travis/republish-guides-website.sh
    chmod 777 republish-guides-website.sh

    ./republish-guides-website.sh || EXIT_STATUS=$?
fi

exit $EXIT_STATUS
