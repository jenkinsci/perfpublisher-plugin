# How to Compile - PerfPublisher Plugin

**Author:** Georges Bossert  
**Email:** gbossert@caps-entreprise.com  
**Date:** 17/07/2009  
**Version:** 1.0

This document explains how to compile, run, debug, and distribute the PerfPublisher Plugin. More generally, this guide can be used as a reference for compiling and running Jenkins plugins.

### Table of Contents

- [1. Tools Needed](#1-tools-needed)
  - [JDK 6.0](#jdk-60)
  - [Maven](#maven)
  - [Installing Maven on Linux](#installing-maven-on-linux)
  - [Configuring Jenkins Repositories](#configuring-jenkins-repositories)
- [2. Files and Workspace Layout](#2-files-and-workspace-layout)
- [3. Compile a Project](#3-compile-a-project)
- [4. Debug a Project](#4-debug-a-project)
- [5. Distributing a Plugin](#5-distributing-a-plugin)

---

### 1. Tools Needed

To compile, run, and debug a Jenkins plugin, a few tools need to be installed.

#### JDK 6.0

Java and its JDK are required since Jenkins plugins are primarily developed in Java.

The minimum required version is JDK 6.0.

#### Maven

Apache Maven 2 must also be installed and fully functional, with access to the Internet to download dependencies.

#### Installing Maven on Linux

1. Open a command terminal.
2. Create a directory for your Maven installation.

   ```bash
   mkdir /udd/[USER]/apache-maven
   ```

3. Download the Maven distribution archive into your installation directory.

   ```bash
   wget http://apache.mirror99.com/maven/binaries/apache-maven-2.0.9-bin.tar.gz
   ```

4. Extract the distribution archive.

   ```bash
   tar -zxf apache-maven-2.0.9-bin.tar.gz
   ```

   The archive will be extracted into `/udd/[USER]/apache-maven/`.

5. Add the `M2_HOME` environment variable.

   ```bash
   export M2_HOME=/udd/[USER]/apache-maven/apache-maven-2.0.9
   ```

6. Add the `M2` environment variable.

   ```bash
   export M2=$M2_HOME/bin
   ```

7. Add Maven to your system `PATH`.

   ```bash
   export PATH=$M2:$PATH
   ```

8. Make sure that `JAVA_HOME` is set to the location of your JDK.

9. Verify your Maven installation.

   ```bash
   mvn --version
   ```

   If the setup is correct, you should see output similar to:

   ```text
   Maven version: 2.0.9
   Java version: 1.6.0_03
   OS name: "linux" version: "2.6.22-14-generic" arch: "i386" Family: "unix"
   ```

#### Configuring Jenkins Repositories

You also need to register the Jenkins repositories in Maven.

Edit the `~/.m2/settings.xml` file and make sure it contains the following configuration:

```xml
<settings>

  <pluginGroups>
    <pluginGroup>org.jenkins-ci.tools</pluginGroup>
  </pluginGroups>

  <profiles>

    <profile>
      <id>jenkins</id>

      <activation>
        <activeByDefault>true</activeByDefault>
      </activation>

      <repositories>
        <repository>
          <id>repo.jenkins-ci.org</id>
          <url>http://repo.jenkins-ci.org/public/</url>
        </repository>
        <repository>
          <id>m.g.o-public</id>
          <url>http://maven.glassfish.org/content/groups/public/</url>
        </repository>
      </repositories>

      <pluginRepositories>
        <pluginRepository>
          <id>repo.jenkins-ci.org</id>
          <url>http://repo.jenkins-ci.org/public/</url>
        </pluginRepository>
        <pluginRepository>
          <id>m.g.o-public</id>
          <url>http://maven.glassfish.org/content/groups/public/</url>
        </pluginRepository>
      </pluginRepositories>

    </profile>

  </profiles>

  <mirrors>

    <mirror>
      <id>repo.jenkins-ci.org</id>
      <url>http://repo.jenkins-ci.org/public/</url>
      <mirrorOf>m.g.o-public</mirrorOf>
    </mirror>

  </mirrors>

  <activeProfiles>
    <activeProfile>jenkins</activeProfile>
  </activeProfiles>

</settings>
```

---

### 2. Files and Workspace Layout

Like any Jenkins plugin, PerfPublisher is mainly based on four types of files and directories.

| Path | Description |
|---|---|
| `pom.xml` | Controls the Maven build configuration. |
| `src/main/java` | Contains all Java source files. |
| `src/main/resources` | Contains Jelly files and other resource files. |
| `src/main/webapp` | Contains static files, such as images and HTML pages, that are copied directly into the plugin runtime. |

---

### 3. Compile a Project

To compile the project, execute the following Maven commands in a shell:

**Clean the project:**

```bash
mvn clean
```

Removes generated files from previous builds.

**Compile the sources:**

```bash
mvn compile
```

Compiles the project's Java source files.

---

### 4. Debug a Project

Before starting the debugging process, configure the environment.

Use the following Linux command to enable remote debugging on port `8000`:

```bash
export MAVEN_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=8000,suspend=n"
```

As with compilation, debugging only requires executing a Maven command:

```bash
mvn hpi:run
```

Once the command finishes starting Jenkins, open the following URL in your browser:

```text
http://localhost:8080/
```

You should see the Jenkins interface running inside Jetty.

The `MAVEN_OPTS` configuration starts the application with a debugger listening on port `8000`. You can then configure your IDE to attach to this port and start a debugging session.

---

### 5. Distributing a Plugin

To generate the package containing all the required plugin files, execute the following Maven goal:

```bash
mvn package
```

This command generates an `.hpi` archive inside the `target` directory.

The `.hpi` archive is similar to a `.jar` file and is the package used to install the plugin in Jenkins.
----------------------------------------------------
6. Links
----------------------------------------------------
Maven2 :
http://maven.apache.org/
Jenkins :
http://jenkins-ci.org/
