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

/**
 * Helper for TeamCity Service Messages
 *
 * @author <a href="mail@felixschulze.de">Felix Schulze</a>
 */
public class TeamCityHelper {

    public static String escapeString(String string) {
        String tmp = string.replace("|", "||");
        tmp = tmp.replace("'", "|'");
        tmp = tmp.replace("\"", "|'");
        tmp = tmp.replaceAll("\\n", "|n");
        tmp = tmp.replaceAll("\\r", "|r");
        tmp = tmp.replace("[", "|[");
        tmp = tmp.replace("]", "|]");
        return tmp;
    }
    
    public static String createVersionLog(String version) {
        return "##teamcity[buildNumber '"+TeamCityHelper.escapeString(version)+"']";
    }

    public static String createBuildStatusFailureLog(String text) {
        return "##teamcity[buildStatus status='FAILURE' text='"+TeamCityHelper.escapeString(text)+"']";
    }
}
