/*
 * Copyright (C) 2012 Felix Schulze
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.felixschulze.maven.plugins.xcode;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;

import java.io.File;

/**
 * Contains common fields and methods for xcode mojos.
 *
 * @author <a href="mail@felixschulze.de">Felix Schulze</a>
 */
public abstract class AbstractXcodeMojo extends AbstractMojo {

    /**
     * Location of the xcodebuild executable.
     *
     * @parameter expression="${xcodebuild}" default-value="/usr/bin/xcodebuild"
     */
    protected File xcodeCommandLine;

    /**
     * Location of the xcrun executable.
     *
     * @parameter expression="${xcrun}" default-value="/usr/bin/xcrun"
     */
    protected File xcrunCommandLine;

    /**
     * Location of the plutil executable.
     *
     * @parameter expression="${plutil}" default-value="/usr/bin/plutil"
     */
    protected File plutilCommandLine;

    /**
     * Location of the xcodebuild executable.
     *
     * @parameter expression="${xcodebuild}" default-value="/usr/local/bin/ios-sim"
     */
    protected File iosSimCommandLine;

    /**
     * Location of the lcov executable.
     *
     * @parameter expression="${lcov}" default-value="/usr/local/bin/lcov"
     */
    protected File lcovCommandLine;

    /**
     * Location of the genhtml executable.
     *
     * @parameter expression="${genhtml}" default-value="/usr/local/bin/genhtml"
     */
    protected File genHtmlCommandLine;

    /**
     * Project Name
     *
     * @parameter
     */
    protected File xcodeProject;

    /**
     * Target to be built
     *
     * @parameter
     */
    protected String xcodeTarget;

    /**
     * Target to be built
     *
     * @parameter
     */
    protected String xcodeSdk;

    /**
     * Configuration to be built
     *
     * @parameter
     */
    protected String xcodeConfiguration;

    /**
     * Info Plist of the app
     *
     * @parameter
     */
    protected File infoPlist;

    /**
     * Info Plist of the app
     *
     * @parameter
     */
    protected String bundleIdentifierSuffix;

    /**
     * Info Plist of the app
     *
     * @parameter
     */
    protected String bundleDisplayNameSuffix;

    /**
     * Info Plist of the app
     *
     * @parameter
     */
    protected Boolean bundleVersionFromGit;

    /**
     * ProvisioningProfile to sign the app
     *
     * @parameter
     */
    protected String provisioningProfile;

    /**
     * CodeSignIdentity to sign the app
     *
     * @parameter
     */
    protected String codeSignIdentity;

    /**
     * App Name
     *
     * @parameter
     */
    protected String appName;

    /**
     * Optional Coverage App Name for static libraries
     *
     * @parameter
     */
    protected String coverageAppName;

    /**
     * App Name
     *
     * @parameter
     */
    protected String testDevice;

    /**
     * App Name
     *
     * @parameter default-value="False"
     */
    protected Boolean testNoAutoExit;

    /**
     * App Name
     *
     * @parameter default-value="False"
     */
    protected Boolean teamCityLog;

    /**
     * App Name
     *
     * @parameter default-value="False"
     */
    protected Boolean executeGHUnitTests;

    /**
     * App Name
     *
     * @parameter default-value="False"
     */
    protected Boolean generateCoverageReport;


    /**
     * Coverage Target
     *
     * @parameter
     */
    protected String coverageTarget;

    /**
     * The Maven Project Object
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    /**
     * @parameter expression="${basedir}"
     */
    protected String basedir;

    /**
     * Build directory.
     *
     * @parameter expression="${project.build.directory}"
     * @required
     */
    protected File buildDirectory;
}