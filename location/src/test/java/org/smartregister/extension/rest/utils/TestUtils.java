/*
 * Copyright 2023 Ona Systems, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.smartregister.extension.rest.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.Reference;

public class TestUtils {

    public static final int TOTAL_LOCATIONS = 50000; // Generate 50K locations
    public static final int TOTAL_CHILD_LOCATIONS_PER_NODE = 5; // Total sub-locations per location

    public static List<Location> getTestLocations() {

        List<Location> locationList = new ArrayList<>();

        int parentId = 1;

        for (int i = 1; i < TOTAL_LOCATIONS + 1; i++) {

            if (i == 1) {
                parentId = 1;
            } else if (i % TOTAL_CHILD_LOCATIONS_PER_NODE == 0) {
                parentId++;
            }

            locationList.add(
                    getLocation(
                            "Location/" + i,
                            "Test Location " + i,
                            (i == 1 ? null : "Location/" + parentId)));
        }

        return locationList;
    }

    public static Map<String, List<IBaseResource>> getTestLocationsMap() {

        Map<String, List<IBaseResource>> testLocationsMap = new HashMap<>();

        List<IBaseResource> locationList = new ArrayList<>();

        int parentId = 1;

        Location location;
        for (int i = 1; i < TOTAL_LOCATIONS + 1; i++) {

            if (i == 1) {
                parentId = 1;

            } else if (i % TOTAL_CHILD_LOCATIONS_PER_NODE == 0) {

                testLocationsMap.put("location-id-" + parentId, locationList);
                locationList = new ArrayList<>();

                parentId++;
            }

            location =
                    getLocation(
                            "location-id-" + i,
                            "Test Location " + i,
                            (i == 1 ? null : "location-id-" + parentId));

            if (i != 1) locationList.add(location);
        }

        return testLocationsMap;
    }

    private static Location getLocation(String id, String name, String reference) {

        Location location = new Location();
        location.setId(id);
        location.setName(name);

        Reference partOfReference = new Reference();
        partOfReference.setReference(reference);
        location.setPartOf(partOfReference);

        return location;
    }
}
