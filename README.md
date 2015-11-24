QuickSign JGitFlow SemVer
=========================

Provides [Semantic Versioning](http://semver.org/) using Gitflow. Highly based on [amkay's gradle-gitflow](https://github.com/amkay/gradle-gitflow) work but rewritten in pure Java without any Gradle dependencies and focused on being used as a JVM library and as a standalone command line program.

![image](https://travis-ci.org/cedricvidal/jgitflow-semver.svg?branch=develop)
[![Code Coverage](https://img.shields.io/codecov/c/github/cedricvidal/jgitflow-semver/develop.svg)](https://codecov.io/github/cedricvidal/jgitflow-semver?branch=develop)
![image](https://img.shields.io/badge/license-Apache%202-blue.svg)

ASCIInema First look
---

[![asciicast](https://asciinema.org/a/0bth8psrcgp2hc11bj7uw02ji.png)](https://asciinema.org/a/0bth8psrcgp2hc11bj7uw02ji)

Requirements
---

JRE 7+

Installation
---

On OSX

```
brew tap cedricvidal/tap
brew install jgitflow-semver
```

Usage
---

```
usage: jgitflow-semver [options]... <path>
 -b,--branch <arg>   force branch name in case of a detached repo
 -m,--maven          Maven compatible semver versions
 -s,--snapshot       Use Maven SNAPSHOT instead of semver build metadata 
```
