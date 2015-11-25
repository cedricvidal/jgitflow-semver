QuickSign JGitFlow SemVer
=========================

Provides [Semantic Versioning](http://semver.org/) using [Gitflow AVH Edition](https://github.com/petervanderdoes/gitflow-avh). Highly based on [amkay's gradle-gitflow](https://github.com/amkay/gradle-gitflow) work but rewritten in pure Java without any Gradle dependencies and focused on being used as a JVM library and as a standalone command line program that you can call from your build system.

It has special Maven semantics options (`-m` and `-s`) but really is meant to be used in any build system for any language.

![image](https://travis-ci.org/cedricvidal/jgitflow-semver.svg?branch=develop)
[![Code Coverage](https://img.shields.io/codecov/c/github/cedricvidal/jgitflow-semver/develop.svg)](https://codecov.io/github/cedricvidal/jgitflow-semver?branch=develop)
![image](https://img.shields.io/badge/license-Apache%202-blue.svg)

Quickstart
---

[![asciicast](https://asciinema.org/a/0bth8psrcgp2hc11bj7uw02ji.png)](https://asciinema.org/a/0bth8psrcgp2hc11bj7uw02ji)

Version structure
---

The inferred versions follow [SemVer](http://semver.org/) rules, they consist of the following components:

```
1.2.3-dev.65+sha.9066228.dirty
| | |  |  |   |     |      |
| | |  |  |   |     |      indicates if the repository is dirty
| | |  |  |   |     |
| | |  |  |   |     abbreviated SHA of the current commit
| | |  |  |   |
| | |  |  |   prefix of the SHA
| | |  |  |
| | |  |  # of commits since the last tag
| | |  |
| | |  denotes the current branch
| | |
| | patch version
| |
| minor version
|
major version
```

Mapping between Gitflow branches, pre-release identifiers and versions
---

| Gitflow branch       | Default name in Gitflow plugins | Pre-release identifier | Normal version |
| -------------------- | --------------- | ------------- | ------------- | ------------- |
| production release   | `master`        | (empty string) | Take the closest tag |
| development          | `develop`       | `dev` | Take the closest tag (or extract next version from release branch if it currently exists) and increment minor version |
| feature              | `feature/foo`   | `feature.foo` | Take the closest tag and increment minor version |
| next release         | `release/1.2.0` | `pre` | Extracted from the branch name -> `1.2.0` |
| versioned hotfix     | `hotfix/1.2.1`  | `pre` | Behaves as a release, version is extracted from branch name -> `1.2.1` |
| non versioned hotfix | `hotfix/foo`    | `fix.foo` | Take the closest tag |
| support              | `support/foo`   | `support.foo` | Take the closest tag and increment minor version |

NB: tags are considered version tags if they follow SemVer rules. They can optionnally be prefixed with `v`. Any other tag is ignored, you are therefore safe to create other tags if you will

Requirements
---

- JRE 7+
- [Gitflow AVH Edition](https://github.com/petervanderdoes/gitflow-avh)

Installation
---

On OSX

```Shell
brew tap cedricvidal/tap
brew install jgitflow-semver
```

Basic shell installation

```Shell
eval "$(curl -fsSL https://raw.githubusercontent.com/cedricvidal/jgitflow-semver/master/install)"
```

Command Line Usage
---

```Shell
usage: jgitflow-semver [options]... <path>
 -b,--branch <arg>   force branch name in case of a detached repo
 -m,--maven          Maven compatible semver versions
 -s,--snapshot       Use Maven SNAPSHOT instead of semver build metadata 
```

Maven Integration using command line program
---

Use a variable (`GIT_FLOW_VERSION` here) in pom.xml's `<version/>` tag

```Maven POM
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

```Shell
mvn clean package
```

or compute `SNAPSHOT` maven version using 

```Shell
mvn clean package -DGIT_FLOW_VERSION=`jgitflow-semver -m -s`
```

NB: You might get a warning telling that the `<version/>` tag cannot use a variable but just ignore it, the Maven team at some point wanted to remove support for it but they have changed their mind recently and support will stay.

Maven Integration in CI server
---

On your CI server, you need to install `jgitflow-semver` and tell it the branch name to compute version for because often, CI servers checkout in detached mode.

You can download `jgitflow-semver` using the following code snippet:

```Shell
eval "$(curl -fsSL https://raw.githubusercontent.com/cedricvidal/jgitflow-semver/master/install)"
```

You can then call `jgitflow-semver` by storing it in a variable and calling maven directly

```Shell
export GIT_FLOW_VERSION=`jgitflow-semver -s -m -b $bamboo_repository_branch_name .`
mvn clean package -DGIT_FLOW_VERSION=$GIT_FLOW_VERSION
```

or storing it in a file to load it in you CI process:

```Shell
jgitflow-semver -s -m -b $bamboo_repository_branch_name . > .git/.git_flow_version
```

Note: if your CI server doesn't honor the PATH variable update by the install script then you can call the program directly from there

```Shell
~/.jgitflow-semver/jgitflow-semver
```

You can then load the inferred version from `.git/.git_flow_version` file and use it to call maven. Task `Variable File reader` for Bamboo.

Beware that `$bamboo_repository_branch_name` must be replaced by the variable that your CI server exports the current branch name to. Indeed, most CI servers checkout in detached mode which means you need to tell `jgitflow-semver` what branch to compute version from.
