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

package de.felixschulze.maven.plugins.xcode.xcodeprojparser;

import java.io.File;

public class XCBuildConfiguration {

    private String name;
    private File infoPlist;

    public XCBuildConfiguration(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public File getInfoPlist() {
        return infoPlist;
    }

    public void setInfoPlist(File infoPlist) {
        this.infoPlist = infoPlist;
    }
}
