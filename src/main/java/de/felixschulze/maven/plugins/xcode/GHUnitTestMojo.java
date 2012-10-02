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

import de.felixschulze.maven.plugins.xcode.helper.ProcessHelper;
import de.felixschulze.maven.plugins.xcode.helper.TeamCityHelper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Run GHUnit Tests with ios-sim
 *
 * @goal ghunittest
 * @phase test
 * @see <a href="https://github.com/phonegap/ios-sim">ios-sim</a>
 * @see <a href="https://github.com/gabriel/gh-unit">GHUnit</a>
 * @author <a href="mail@felixschulze.de">Felix Schulze</a>
 */
public class GHUnitTestMojo extends AbstractXcodeMojo {


    /**
     * Execute the xcode command line utility.
     */
    public void execute() throws MojoExecutionException {

        if (executeGHUnitTests) {

            if (!iosSimCommandLine.exists()) {
                throw new MojoExecutionException("Invalid path for ios-sim: " + iosSimCommandLine.getAbsolutePath());
            }
            if (appName == null) {
                throw new MojoExecutionException("AppName must be defined.");
            }

            if (!xcodeSdk.contains("iphonesimulator")) {
                throw new MojoExecutionException("GHUnit-Tests can only run on simulator");
            }

            File appDirectory = new File(buildDirectory, xcodeConfiguration + "-iphonesimulator");
            File testResultsDirectory = new File(buildDirectory, "test-results");

            File appFile = new File(appDirectory, appName + ".app");

            CommandExecutor executor = CommandExecutor.Factory.createDefaultCommmandExecutor();
            executor.setLogger(this.getLog());
            List<String> commands = new ArrayList<String>();

            commands.add("launch");
            commands.add(appFile.getAbsolutePath());
            if (testDevice != null) {
                commands.add("--family");
                commands.add(testDevice);
            }

            if (!testNoAutoExit) {
                commands.add("--setenv");
                commands.add("GHUNIT_AUTOEXIT=YES");
            }

            if (teamCityLog) {
                commands.add("--setenv");
                commands.add("GHUNIT_AUTORUN=1");
                commands.add("--setenv");
                commands.add("WRITE_JUNIT_XML=1");
                commands.add("--setenv");
                commands.add("JUNIT_XML_DIR="+testResultsDirectory.getAbsolutePath());
            }

            try {
                getLog().info("Shutdown iPhone Simulator.");
                ProcessHelper.killSimulatorProcess();
            } catch (IOException e) {
                throw new MojoExecutionException("Error while shutdown simulator: ", e);
            }

            try {
                getLog().info(iosSimCommandLine.getAbsolutePath() + " " + commands.toString());
                executor.executeCommand(iosSimCommandLine.getAbsolutePath(), commands, false, true);
                final String errorOut = executor.getStandardError();

                String regexSimulatorTimeOut = ".*Simulator session timed out.(.*)";
                Boolean sessionTimedOut = Pattern.compile(regexSimulatorTimeOut, Pattern.DOTALL).matcher(errorOut).matches();
                if (sessionTimedOut) {
                    if (teamCityLog) {
                        getLog().error("##teamcity[buildStatus status='FAILURE' text='Simulator session timed out.']");
                    }
                    getLog().error("Simulator session timed out.");
                    throw new MojoExecutionException("Simulator session timed out.");
                }

                String regex = ".*Executed [0-9]* of [0-9]* tests, with [0-9]* failures in [0-9]*.[0-9]* seconds(.*)";
                Boolean success = Pattern.compile(regex, Pattern.DOTALL).matcher(errorOut).matches();
                if (!success) {
                    if (teamCityLog) {
                        getLog().error("##teamcity[buildStatus status='FAILURE' text='Tests failed - The app may be crashed']");
                    }
                    getLog().error("Tests failed - The app may be crashed");
                    throw new MojoExecutionException("Tests failed - The app may be crashed");
                }
            } catch (ExecutionException e) {
                throw new MojoExecutionException("Error while executing: ", e);
            }

            //Test results
            if (teamCityLog) {
                String[] extension = {"xml"};
                Iterator<File> fileIterator = FileUtils.iterateFiles(testResultsDirectory, extension, true);
                while (fileIterator.hasNext()) {
                    File testXml = fileIterator.next();
                    getLog().info("##teamcity[importData type='junit' path='" + testXml.getAbsolutePath() + "']");
                }

            }


            //Coverage
            if (generateCoverageReport) {

                if (!lcovCommandLine.exists()) {
                    throw new MojoExecutionException("Invalid path for lcov: " + lcovCommandLine.getAbsolutePath());
                }
                if (!genHtmlCommandLine.exists()) {
                    throw new MojoExecutionException("Invalid path for genhtml: " + genHtmlCommandLine.getAbsolutePath());
                }

                commands = new ArrayList<String>();

                commands.add("--directory");

                File coverageObjectsDir = buildDirectory;

                String buildFolderName;
                if (coverageAppName != null) {
                    buildFolderName = coverageAppName + ".build";
                }
                else {
                    buildFolderName = appName + ".build";
                }
                String[] directoryStructure = {
                        xcodeProject.getName().replace(".xcodeproj", ".build"),
                        xcodeConfiguration + "-iphonesimulator",
                        buildFolderName,
                        "Objects-normal",
                        "i386"
                };
                for (String currentDir:directoryStructure) {
                    coverageObjectsDir = new File(coverageObjectsDir.getAbsolutePath(), currentDir);
                }

                commands.add(coverageObjectsDir.getAbsolutePath());
                commands.add("--capture");
                commands.add("--output-file");

                File coverageOutPutFile = new File(buildDirectory, "main.info");
                commands.add(coverageOutPutFile.getAbsolutePath());

                commands.add("-b");
                commands.add(basedir);

                File coverageTargetOutPutFile = new File(buildDirectory, appName + ".info");

                getLog().info(lcovCommandLine.getAbsolutePath() + " " + commands.toString());

                try {
                    executor.executeCommand(lcovCommandLine.getAbsolutePath(), commands, false);

                    commands = new ArrayList<String>();
                    commands.add("-o");
                    commands.add(coverageTargetOutPutFile.getAbsolutePath());

                    commands.add("--extract");
                    commands.add(coverageOutPutFile.getAbsolutePath());
                    commands.add("'*"+coverageTarget+"/*'");

                    getLog().info(lcovCommandLine.getAbsolutePath() + " " + commands.toString());
                    executor.executeCommand(lcovCommandLine.getAbsolutePath(), commands, false);

                    final String errorOut = executor.getStandardOut();

                    String regex = ".*.*lines......: ([0-9]*.[0-9])*% \\(([0-9]*) of ([0-9]*) lines\\)(.*)";
                    Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
                    Matcher matcher = pattern.matcher(errorOut);

                    while (matcher.find()) {
                        if (teamCityLog) {
                            getLog().info("##teamcity[buildStatisticValue key='CodeCoverageL' value='" + matcher.group(1) + "'");
                            getLog().info("##teamcity[buildStatisticValue key='CodeCoverageAbsLCovered' value='" + matcher.group(2) + "'");
                            getLog().info("##teamcity[buildStatisticValue key='CodeCoverageAbsLTotal' value='" + matcher.group(3) + "'");
                        }
                    }
                } catch (ExecutionException e) {
                    throw new MojoExecutionException("Error while executing lcov: ", e);
                }

                //Generate HTML Report
                File coverageReportDir = new File(new File(buildDirectory, "artifacts"), "coverage");
                coverageReportDir.mkdirs();

                try {
                    commands = new ArrayList<String>();
                    commands.add(coverageTargetOutPutFile.getAbsolutePath());
                    commands.add("--prefix");
                    commands.add(basedir);
                    commands.add("--output-directory");
                    commands.add(coverageReportDir.getAbsolutePath());

                    getLog().info(genHtmlCommandLine.getAbsolutePath() + " " + commands.toString());
                    executor.executeCommand(genHtmlCommandLine.getAbsolutePath(), commands, false);
                } catch (ExecutionException e) {
                    throw new MojoExecutionException("Error while executing genhtml: ", e);
                }

                try {
                    getLog().info("Shutdown iPhone Simulator.");
                    ProcessHelper.killSimulatorProcess();
                } catch (IOException e) {
                    throw new MojoExecutionException("Error while shutdown simulator: ", e);
                }
            }
        } else {
            getLog().info("Skipping GHUnit-Tests.");
        }
    }
}
