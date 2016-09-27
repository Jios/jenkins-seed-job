#!/bin/bash

git checkout ${GIT_BRANCH}

export prebuild_path=properties/prebuild.properties
export postbuild_path=properties/postbuild.properties 

mkdir -p ${WORKSPACE}/properties 
touch $prebuild_path
touch $postbuild_path

python -c 'import os; git_branch = os.environ["GIT_BRANCH"].split("/"); repo = git_branch[0]; branch = "/".join(git_branch[1:]); f = open(os.environ["postbuild_path"], "a"); f.write("branch=" + branch + "\n"); f.write("repo=" + repo + "\n"); f.close()'
