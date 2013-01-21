/*
 * Copyright (C) 2013 Felix Schulze
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

package de.felixschulze.maven.plugins.xcode.helper;

import java.io.File;

/**
 * Helper for creating and referencing folders.
 *
 * @author <a href="mail@felixschulze.de">Felix Schulze</a>
 */
public class FolderHelper {

    public static File getAndCreateArtifactsDir(File buildDirectory) {
        File artifactsDir = new File(buildDirectory, "artifacts");
        artifactsDir.mkdirs();
        return artifactsDir;
    }
}
