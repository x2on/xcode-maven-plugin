# Xcode-Maven-Plugin

## Overview
A maven plugin for building iOS Apps, run unit-tests and integration-tests.

[![Build Status](https://secure.travis-ci.org/x2on/xcode-maven-plugin.png)](http://travis-ci.org/x2on/xcode-maven-plugin)

## Requirements on your xcode project

- Unit-Tests: GH-Unit: https://github.com/gabriel/gh-unit
- Integration-Tests: KIF: https://github.com/square/KIF

## Requirements on your build agent

- XCode
- Homebrew: ```ruby -e "$(curl -fsSkL raw.github.com/mxcl/homebrew/go)"```
- lcov: ```brew install lcov```
- ios-sim: ```brew install ios-sim```

## Example POM
```xml
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>de.felixschulze.my-project</groupId>
    <artifactId>my-project</artifactId>
    <packaging>xcode</packaging>
    <version>1.0-SNAPSHOT</version>
    <name>My Project</name>
    <build>
        <plugins>
            <plugin>
                <groupId>de.felixschulze.maven.plugins.xcode</groupId>
                <artifactId>xcode-maven-plugin</artifactId>
                <version>1.1</version>
                <configuration>
                    <xcodeProject>my-project.xcodeproj</xcodeProject>
                    <xcodeTarget>my-project</xcodeTarget>
                    <xcodeConfiguration>Release</xcodeConfiguration>
                    <xcodeSdk>iphoneos</xcodeSdk>
                </configuration>
                <extensions>true</extensions>
            </plugin>
        </plugins>
    </build>
</project>
```

## Advanced POM
```xml
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>de.felixschulze.my-project</groupId>
    <artifactId>my-project</artifactId>
    <packaging>xcode</packaging>
    <version>1.0-SNAPSHOT</version>
    <name>My Project</name>
    <build>
        <plugins>
            <plugin>
                <groupId>de.felixschulze.maven.plugins.xcode</groupId>
                <artifactId>xcode-maven-plugin</artifactId>
                <version>1.1</version>
                <configuration>
                    <xcodeProject>my-project.xcodeproj</xcodeProject>
                    <xcodeTarget>my-project</xcodeTarget>
                    <xcodeConfiguration>Release</xcodeConfiguration>
                    <xcodeSdk>iphoneos</xcodeSdk>
                    <appName>my-project</appName>
                    <executeGHUnitTests>true</executeGHUnitTests>
                    <generateCoverageReport>true</generateCoverageReport>
                    <coverageTarget>CI-Demo</coverageTarget>
                    <bundleVersionFromGit>true</bundleVersionFromGit>
                    <teamCityLog>true</teamCityLog>
                    <infoPlist>my-project/my-project-Info.plist</infoPlist>
                    <provisioningProfile>ABCDEFGH-ABCD-1234-ABCD-12345678901</provisioningProfile>
                    <codeSignIdentity>iPhone Distribution: Developer Name</codeSignIdentity>
                </configuration>
                <extensions>true</extensions>
            </plugin>
        </plugins>
    </build>
</project>
```

## License
Apache License, Version 2.0 - See LICENSE