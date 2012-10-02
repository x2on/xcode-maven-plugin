# Xcode-Maven-Plugin

## Overview
A maven plugin for building iOS Apps, run unit-tests and integration-tests.

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
                <version>1.0-SNAPSHOT</version>
                <configuration>
                    <xcodeProject>my-project.xcodeproj</xcodeProject>
                    <xcodeTarget>my-project</xcodeTarget>
                    <xcodeConfiguration>Release</xcodeConfiguration>
                    <xcodeSdk>iphoneos</xcodeSdk>
                    <appName>my-project</appName>
                    <infoPlist>my-project/my-project-Info.plist</infoPlist>
                    <bundleVersionFromGit>true</bundleVersionFromGit>
                    <provisioningProfile>ABCDEFGH-ABCD-1234-ABCD-12345678901</provisioningProfile>
                    <codeSignIdentity>iPhone Distribution: Developer Name</codeSignIdentity>
                    <teamCityLog>true</teamCityLog>
                </configuration>
                <extensions>true</extensions>
            </plugin>
        </plugins>
    </build>
</project>
```

## License
Apache License, Version 2.0 - See LICENSE