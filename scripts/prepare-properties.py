#!/usr/bin/python

import os

def touch(fname):
    if os.path.exists(fname):
        os.utime(fname, None)

properties_path = os.environ["WORKSPACE"] + "/properties"

if not os.path.exists(properties_path):
    os.makedirs(properties_path)

postbuild_path = properties_path + "/postbuild.properties"
prebuild_path  = properties_path + "/prebuild.properties"

touch(prebuild_path)
touch(postbuild_path)

if os.environ["REPO_NAME"] in os.environ["GIT_BRANCH"]:
	git_branch = os.environ["GIT_BRANCH"].split("/")
	repo = git_branch[0]
	branch = "/".join(git_branch[1:])
else:
	git_branch = os.environ["GIT_BRANCH"]
	repo = os.environ["REPO_NAME"]

f = open(postbuild_path, "a")
f.write("branch=" + branch + "\n")
f.write("repo=" + repo + "\n")
f.write("REPO_NAME=" + os.environ["REPO_NAME"] + "\n")
f.write("GIT_BRANCH=" + os.environ["GIT_BRANCH"] + "\n")
f.close()

f = open(prebuild_path, "a")
f.close()