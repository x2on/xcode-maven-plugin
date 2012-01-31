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

import de.felixschulze.maven.plugins.xcode.helper.ZipCreator;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Package IPA File
 *
 * @goal ipaBuilder
 * @phase package
 * @requiresDependencyResolution compile
 * @author <a href="mail@felixschulze.de">Felix Schulze</a>
 */
public class IpaBuilderMojo extends AbstractXcodeMojo {

    /**
     * Execute the ipa package.
     */
    public void execute() throws MojoExecutionException {
        if (!xcrunCommandLine.exists()) {
            throw new MojoExecutionException("Invalid path for xcrun: " + xcrunCommandLine.getAbsolutePath());
        }
        if (appName == null) {
            throw new MojoExecutionException("AppName must be defined.");
        }

        CommandExecutor executor = CommandExecutor.Factory.createDefaultCommmandExecutor();
        executor.setLogger(this.getLog());
        List<String> commands = new ArrayList<String>();
        commands.add("-sdk");
        commands.add("iphoneos");
        commands.add("PackageApplication");
        commands.add("-v");

        File appDirectory = new File(buildDirectory, xcodeConfiguration + "-iphoneos");

        File appFile = new File(appDirectory, appName + ".app");

        commands.add(appFile.getAbsolutePath());
        commands.add("-o");
        File ipaFile = new File(buildDirectory, appName + ".ipa");
        commands.add(ipaFile.getAbsolutePath());

        getLog().info(xcrunCommandLine.getAbsolutePath() + " " + commands.toString());

        try {
            executor.executeCommand(xcrunCommandLine.getAbsolutePath(), commands, true);

        } catch (ExecutionException e) {
            getLog().error(executor.getStandardOut());
            getLog().error(executor.getStandardError());
            throw new MojoExecutionException("Error while executing: ", e);
        }

        ZipCreator creator = new ZipCreator(appDirectory);

        File zipFile = new File(buildDirectory, appName + ".app.dSYM.zip");

        try {
            creator.createZipFromDirectory(new File(appDirectory, appName + ".app.dSYM"), zipFile);
        } catch (IOException e) {
            throw new MojoExecutionException("Error while creating dSYM Zip: ", e);
        }
    }
}