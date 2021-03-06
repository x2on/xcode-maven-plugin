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

package de.felixschulze.maven.plugins.xcode.helper;

import org.junit.Test;
import static junit.framework.Assert.assertEquals;

public class TeamCityHelperTest {

    @Test
    public void testShouldEscapeString() {
        String exampleString = "Test|Test'Test\"Test\\nTest\\rTest[Test]";
        String escapedString = TeamCityHelper.escapeString(exampleString);
        assertEquals("Test||Test|'Test|'Test\\nTest\\rTest|[Test|]", escapedString);
    }

    @Test
    public void testShouldCreateVersionLog() {
        String versionLog = TeamCityHelper.createVersionLog("3.7");
        assertEquals("##teamcity[buildNumber '3.7']", versionLog);
    }

    @Test
    public void testShouldCreateBuildStatusFailureLog() {
        String buildStatusFailureLog = TeamCityHelper.createBuildStatusFailureLog("Tests failed - The app may be crashed");
        assertEquals("##teamcity[buildStatus status='FAILURE' text='Tests failed - The app may be crashed']", buildStatusFailureLog);
    }

}
