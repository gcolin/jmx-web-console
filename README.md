# JMX Web console

## Why using this library?

* Access JMX from a web interface
* Very fast
* Stateless (nothing is stored in RAM or in HDD)
* Need only one Servlet (easy to secure)
* Easy to use or extend
* No dependency and compatible with Java SE Embedded 8 Compact 3
* A running example with different CSS styles (Bootstrap and PureCSS)
* JRE 1.5+
* The jmx-war need only servlet api 2.4 (can run on old and new containers)
* Very small (minimal war size about 24 KB)

![screen](https://github.com/gcolin/jmx-web-console/raw/master/screen.png)

## How to install

You need **maven** installed.

```bash
    mvn install
```

## How to run the example

```bash
    cd jmx-exmaple
    mvn jetty:run
```

Go to your browser at: *http://localhost:8080*
The login is *admin* and the password is *admin*.

The example project is a good start for integrating to a production level application.

## The minimal war

*jmx-war* is a minimal war for exposing the JMX with a web interface. The security provided is too light for production environment but it is adequate for an internal use. It is just a basic authentication with a role *manager*.

## How to insert in your project

### Maven

```xml
<dependency>
    <groupId>net.gcolin.jmxconsole</groupId>
    <artifactId>jmx-web-console</artifactId>
    <version>1.0</version>
</dependency>
```

### Gradle

```groovy
    compile group: 'net.gcolin.jmxconsole', name: 'jmx-web-console', version:'1.1'
```