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

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XcodeprojParser {

    File xcodprojJsonFile;

    public XcodeprojParser(File xcodeprojJsonFile) {
        this.xcodprojJsonFile = xcodeprojJsonFile;
    }

    public PBXProject parseXcodeFile() throws IOException, JSONException {
        String content = FileUtils.readFileToString(xcodprojJsonFile);
        JSONObject object = new JSONObject(content);
        JSONObject objects = object.getJSONObject("objects");

        String[] mainNames = JSONObject.getNames(objects);
        List<PBXNativeTarget> targets = null;
        for (String name : mainNames) {
            JSONObject currentObject = objects.getJSONObject(name);
            if (currentObject.has("isa")) {
                String isa = currentObject.getString("isa");
                if (isa.equalsIgnoreCase("PBXProject")) {
                    List<String> targetRefs = getTargetRefs(currentObject);
                    targets = getTargetsFromRefs(targetRefs, objects, currentObject);
                }
            }
        }
        if (targets != null) {
            return new PBXProject(targets);
        }
        return null;
    }

    private List<String> getTargetRefs(JSONObject json) throws JSONException {
        JSONArray array = json.getJSONArray("targets");
        List<String> targetRefs = new ArrayList<String>();
        for (int i = 0; i < array.length(); i++) {
            targetRefs.add(array.getString(i));
        }
        return targetRefs;
    }

    private List<PBXNativeTarget> getTargetsFromRefs(List<String> targetRefs, JSONObject json, JSONObject projectJson) throws JSONException {
        List<PBXNativeTarget> pbxNativeTargets = new ArrayList<PBXNativeTarget>();

        for (String id : targetRefs) {
            JSONObject targetObject = json.getJSONObject(id);
            if (targetObject.has("isa")) {
                if (targetObject.getString("isa").equalsIgnoreCase("PBXNativeTarget")) {
                    String name = targetObject.getString("name");
                    String productName = targetObject.getString("productName");
                    String type = targetObject.getString("productType");
                    String productReference = targetObject.getString("productReference");
                    String buildConfigurationList = targetObject.getString("buildConfigurationList");

                    List<XCBuildConfiguration> xcBuildConfigurations = getBuildConfigurations(json, buildConfigurationList);

                    JSONObject productReferenceObject = json.getJSONObject(productReference);
                    String appName = productReferenceObject.getString("path");

                    PBXNativeTarget target = new PBXNativeTarget(name,productName,appName,type,xcBuildConfigurations);

                    pbxNativeTargets.add(target);
                }
            }
        }
        return pbxNativeTargets;
    }

    public List<XCBuildConfiguration> getBuildConfigurations(JSONObject jsonObject, String buildConfigurationList) throws JSONException {
        List<XCBuildConfiguration> buildConfigurations = new ArrayList<XCBuildConfiguration>();
        JSONObject buildConfigurationListIdJson = jsonObject.getJSONObject(buildConfigurationList);

        if (buildConfigurationListIdJson.has("isa")) {
            if (buildConfigurationListIdJson.getString("isa").equalsIgnoreCase("XCConfigurationList")) {

                JSONArray buildConfigurationIds = buildConfigurationListIdJson.getJSONArray("buildConfigurations");

                for (int i = 0; i < buildConfigurationIds.length(); i++) {
                    JSONObject buildConfigurationJson = jsonObject.getJSONObject(buildConfigurationIds.getString(i));
                    String name = buildConfigurationJson.getString("name");

                    XCBuildConfiguration buildConfiguration = new XCBuildConfiguration(name);

                    JSONObject buildSettings = buildConfigurationJson.getJSONObject("buildSettings");

                    if (buildSettings.has("INFOPLIST_FILE")) {
                        String infoPlistString = buildSettings.getString("INFOPLIST_FILE");
                        buildConfiguration.setInfoPlist(new File(infoPlistString));
                    }
                    buildConfigurations.add(buildConfiguration);
                }
            }

        }
        return buildConfigurations;
    }
}
