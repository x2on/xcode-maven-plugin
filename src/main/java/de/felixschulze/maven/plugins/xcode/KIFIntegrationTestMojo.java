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
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Run KIF Integration-Tests with ios-sim
 *
 * @goal kif-integration-test
 * @phase test
 * @see <a href="https://github.com/Fingertips/ios-sim">ios-sim</a>
 * @see <a href="https://github.com/square/KIF">KIF</a>
 * @see <a href="https://github.com/x2on/KIF">KIF with TeamCity integration</a>
 * @author <a href="mail@felixschulze.de">Felix Schulze</a>
 */

public class KIFIntegrationTestMojo extends AbstractXcodeMojo {

    /**
     * Execute KIF Integration Tests
     *
     * @parameter default-value="False"
     */
    protected Boolean executeKIFIntegrationTests;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (executeKIFIntegrationTests) {

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
            if (teamCityLog) {
                commands.add("--setenv");
                commands.add("TEAMCITY_LOG=YES");
            }
            if (!testNoAutoExit) {
                commands.add("--setenv");
                commands.add("KIF_AUTOEXIT=YES");
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
                        getLog().error(TeamCityHelper.createBuildStatusFailureLog("Simulator session timed out."));
                    }
                    getLog().error("Simulator session timed out.");
                    throw new MojoExecutionException("Simulator session timed out.");
                }

            } catch (ExecutionException e) {
                throw new MojoExecutionException("Error while executing: ", e);
            }
        }
    }
}
