#!/usr/bin/python

import os

def touch(fname):
    if os.path.exists(fname):
        os.utime(fname, None)

properties_path = os.environ["WORKSPACE"] + "/properties"

if not os.path.exists(properties_path):
    os.makedirs(properties_path)

postbuild_path = properties_path + "/postbuild.properties"
touch(properties_path + "/prebuild.properties")
touch(properties_path)

git_branch = os.environ["GIT_BRANCH"].split("/")
repo = git_branch[0]
branch = "/".join(git_branch[1:])

f = open(postbuild_path, "a")
f.write("branch=" + branch + "\n")
f.write("repo=" + repo + "\n")
f.close()