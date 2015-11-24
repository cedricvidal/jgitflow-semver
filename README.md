QuickSign JGitFlow SemVer
=========================

Provides [Semantic Versioning](http://semver.org/) using Gitflow. Highly based on [amkay's gradle-gitflow](https://github.com/amkay/gradle-gitflow) work but rewritten in pure Java without any Gradle dependencies and focused on being used as a JVM library and as a standalone command line program that you can call from your build system.

It has special Maven semantics options (`-m` and `-s`) but really is meant to be used in any build system for any language.

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

Basic shell installation

```
eval "$(curl -fsSL https://raw.githubusercontent.com/cedricvidal/jgitflow-semver/master/install)"
```

Command Line Usage
---

```
usage: jgitflow-semver [options]... <path>
 -b,--branch <arg>   force branch name in case of a detached repo
 -m,--maven          Maven compatible semver versions
 -s,--snapshot       Use Maven SNAPSHOT instead of semver build metadata 
```

Maven Integration using command line program
---

Use a variable (`GIT_FLOW_VERSION` here) in pom.xml's `<version/>` tag

```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.foo</groupId>
    <artifactId>bar</artifactId>
    <name>Foo Bar</name>
    <packaging>jar</packaging>
    <version>${GIT_FLOW_VERSION}</version>

    <properties>
        <GIT_FLOW_VERSION>dev</GIT_FLOW_VERSION>
    </properties>
</project>
```

In local development, you can either use default `dev` version

```
mvn clean package
```

or compute `SNAPSHOT` maven version using 

```
mvn clean package -DGIT_FLOW_VERSION=`jgitflow-semver -m -s`
```

NB: You might get a warning telling that the `<version/>` tag cannot use a variable but just ignore it, the Maven team at some point wanted to remove support for it but they have changed their mind recently and support will stay.

Maven Integration in CI server
---

On your CI server, you need to install `jgitflow-semver` and tell it the branch name to compute version for because often, CI servers checkout in detached mode.

You can download `jgitflow-semver` using the following code snippet:

```
eval "$(curl -fsSL https://raw.githubusercontent.com/cedricvidal/jgitflow-semver/master/install)"
```

You can then call `jgitflow-semver` by storing it in a variable and calling maven directly

```
export GIT_FLOW_VERSION=`jgitflow-semver -s -m -b $bamboo_repository_branch_name .`
mvn clean package -DGIT_FLOW_VERSION=$GIT_FLOW_VERSION
```

or storing it in a file to load it in you CI process:

```
jgitflow-semver -s -m -b $bamboo_repository_branch_name . > .git/.git_flow_version
```

You can then load the inferred version from `.git/.git_flow_version` file and use it to call maven. Task `Variable File reader` for Bamboo.

Beware that `$bamboo_repository_branch_name` must be replaced by the variable that your CI server exports the current branch name to. Indeed, most CI servers checkout in detached mode which means you need to tell `jgitflow-semver` what branch to compute version from.
