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

import java.io.*;
import java.util.*;

import de.felixschulze.maven.plugins.xcode.helper.GitHelper;
import de.felixschulze.maven.plugins.xcode.helper.TeamCityHelper;
import de.felixschulze.maven.plugins.xcode.xcodeprojparser.PBXNativeTarget;
import de.felixschulze.maven.plugins.xcode.xcodeprojparser.PBXProject;
import de.felixschulze.maven.plugins.xcode.xcodeprojparser.XCBuildConfiguration;
import de.felixschulze.maven.plugins.xcode.xcodeprojparser.XcodeprojParser;
import org.apache.maven.plugin.MojoExecutionException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.json.JSONException;
import xmlwise.Plist;
import xmlwise.XmlParseException;

/**
 * Run the xcodebuild command line program
 *
 * @goal xcodebuild
 * @phase compile
 * @author <a href="mail@felixschulze.de">Felix Schulze</a>
 */
public class XcodeBuildMojo extends AbstractXcodeMojo {


    private final String BUNDLE_IDENTIFIER_KEY = "CFBundleIdentifier";
    private final String BUNDLE_DISPLAY_NAME_KEY = "CFBundleDisplayName";
    private final String BUNDLE_VERSION_KEY = "CFBundleVersion";
    private final String INTERFACE_BUILDER_ERROR = "Interface Builder encountered an error communicating with the iOS Simulator.";
    private final String COMPILE_XIB_ERROR = "CompileXIB";
    private final String IBTOOL_ERROR = "Exception while running ibtool: connection went invalid while waiting for a reply because a mach port died";

    /**
     * Execute the xcode command line utility.
     */
    public void execute() throws MojoExecutionException {
        if (!xcodeCommandLine.exists()) {
            throw new MojoExecutionException("Invalid path for xcodebuild: " + xcodeCommandLine.getAbsolutePath());
        }

        if (!buildDirectory.exists()) {
            buildDirectory.mkdir();
        }

        CommandExecutor executor = CommandExecutor.Factory.createDefaultCommmandExecutor();
        executor.setLogger(this.getLog());
        List<String> commands = new ArrayList<String>();

        commands = new ArrayList<String>();

        if (xcodeProject != null) {
            commands.add("-project");
            commands.add(xcodeProject.getAbsolutePath());
        }
        if (xcodeTarget != null) {
            commands.add("-target");
            commands.add(xcodeTarget);
        }
        if (xcodeConfiguration != null) {
            commands.add("-configuration");
            commands.add(xcodeConfiguration);
        }
        if (xcodeWorkspace != null) {
            commands.add("-workspace");
            commands.add(xcodeWorkspace.getAbsolutePath());
        }
        if (xcodeScheme != null) {
            commands.add("-scheme");
            commands.add(xcodeScheme);
        }
        if (xcodeSdk != null) {
            commands.add("-sdk");
            commands.add(xcodeSdk);
        }

        commands.add("build");
        commands.add("ONLY_ACTIVE_ARCH=NO");

        commands.add("OBJROOT=" + buildDirectory);
        commands.add("SYMROOT=" + buildDirectory);
        commands.add("DSTROOT=" + buildDirectory);

        if (provisioningProfile != null) {
            commands.add("PROVISIONING_PROFILE=\"" + provisioningProfile + "\"");
        }
        if (codeSignIdentity != null) {
            commands.add("CODE_SIGN_IDENTITY=" + codeSignIdentity);
        }

        if (infoPlist != null) {
            changesValuesInPlist();
        }

        getLog().info(xcodeCommandLine.getAbsolutePath() + " " + commands.toString());

        try {
            executor.executeCommand(xcodeCommandLine.getAbsolutePath(), commands, false);

        } catch (ExecutionException e) {
            String errorString = executor.getStandardError();
            String outPutString = executor.getStandardOut();

            String cleanedErrorString = errorString.substring(errorString.indexOf("** BUILD FAILED **"));
            if (errorString.contains(INTERFACE_BUILDER_ERROR) || errorString.contains(IBTOOL_ERROR)
                    || outPutString.contains(INTERFACE_BUILDER_ERROR) || outPutString.contains(IBTOOL_ERROR)) {
                if (teamCityLog) {
                    getLog().error("##teamcity[buildStatus status='FAILURE' text='Interface builder crashed']");
                }
                getLog().error("Interface builder crashed.");
            } else {
                if (teamCityLog) {
                    if (cleanedErrorString != null) {
                        getLog().error("##teamcity[message text='BUILD FAILED' errorDetails='" + TeamCityHelper.escapeString(cleanedErrorString) + "' status='ERROR']");
                        if (cleanedErrorString.contains(COMPILE_XIB_ERROR)) {
                            getLog().error("##teamcity[buildStatus status='FAILURE' text='Interface builder crashed']");
                        } else {
                            getLog().error("##teamcity[buildStatus status='FAILURE' text='Build failed']");
                        }
                    }
                }
            }
            throw new MojoExecutionException("Error while executing: ", e);
        }

    }

    private void changesValuesInPlist() {
        try {
            Map<String, Object> properties = Plist.load(infoPlist);

            Boolean changeInPlist = false;

            if (bundleIdentifierSuffix != null) {

                if (properties.containsKey(BUNDLE_IDENTIFIER_KEY)) {

                    String identifier = String.valueOf(properties.get(BUNDLE_IDENTIFIER_KEY));
                    if (!identifier.endsWith(bundleIdentifierSuffix)) {
                        getLog().info("Add suffix: \"" + bundleIdentifierSuffix + "\" for: \"" + BUNDLE_IDENTIFIER_KEY + "\"");
                        identifier = identifier.concat(bundleIdentifierSuffix);
                        changeInPlist = true;
                        properties.put(BUNDLE_IDENTIFIER_KEY, identifier);
                    }
                }
            }
            if (bundleDisplayNameSuffix != null) {

                if (properties.containsKey(BUNDLE_DISPLAY_NAME_KEY)) {

                    String displayName = String.valueOf(properties.get(BUNDLE_DISPLAY_NAME_KEY));
                    if (!displayName.endsWith(bundleDisplayNameSuffix)) {
                        getLog().info("Add suffix: \"" + bundleDisplayNameSuffix + "\" for: \"" + BUNDLE_DISPLAY_NAME_KEY + "\"");
                        displayName = displayName.concat(bundleDisplayNameSuffix);
                        changeInPlist = true;
                        properties.put(BUNDLE_DISPLAY_NAME_KEY, displayName);
                    }
                }
            }
            if (bundleVersionFromGit) {
                if (properties.containsKey(BUNDLE_VERSION_KEY)) {
                    try {
                        File gitDir = new File(basedir, ".git");
                        if (gitDir.exists()) {
                            GitHelper gitHelper = new GitHelper(gitDir);
                            int numberOfCommits = gitHelper.numberOfCommits();
                            String uniqueShortId = gitHelper.currentHeadRef();

                            if (numberOfCommits > 0 && uniqueShortId != null) {
                                String version = String.valueOf(properties.get(BUNDLE_VERSION_KEY));
                                String versionSuffix = "-" + numberOfCommits + "-" + uniqueShortId;
                                if (!version.contains(versionSuffix)) {
                                    version = version.concat(versionSuffix);
                                    getLog().info("Change version to: " + version);
                                    changeInPlist = true;
                                    properties.put(BUNDLE_VERSION_KEY, version);
                                }
                                if (teamCityLog) {
                                    getLog().info(TeamCityHelper.createVersionLog(version));
                                }
                            }
                        }
                    } catch (GitAPIException e) {
                        getLog().warn("Error while getting version number from git: " + e);
                    }
                }
            }
            if (changeInPlist) {
                Plist.store(properties, infoPlist);
            }
        } catch (XmlParseException e) {
            getLog().warn("Error while parsing plist: " + e);
        } catch (IOException e) {
            getLog().warn("Can't find plist: " + e);
        }
    }
}