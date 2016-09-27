#!/bin/bash

cd ${WORKSPACE}

echo "remove srvm and slack repos"
rm -rf srvm
rm -rf slack

#git submodule add -b master http://stash.tutk.com:7990/scm/abs/srvm.git srvm
#cd srvm && git reset --hard && git pull && cd ..

git clone -b master http://stash.tutk.com:7990/scm/abs/srvm.git srvm
git clone -b master http://stash.tutk.com:7990/scm/abs/slack.git slack

python srvm/srvm.py
python slack/slack.py ${SLACK_PYTHON_TOKEN} ${SLACK_CHANNEL} "srvm/result.json"