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

package org.smartregister.extension.rest;

import ca.uhn.fhir.rest.param.TokenParam;
import org.junit.Test;
import org.smartregister.extension.rest.utils.TestLocationIFhirResourceDao;

public class LocationHierarchyResourceProviderTest {

    @Test
    public void testGetLocationHierarchy() {
        LocationHierarchyResourceProvider locationHierarchyResourceProvider =
                new LocationHierarchyResourceProvider();
        locationHierarchyResourceProvider.setLocationIFhirResourceDao(
                new TestLocationIFhirResourceDao());

        TokenParam tokenParam = new TokenParam();
        tokenParam.setValue("location-id-1");
        locationHierarchyResourceProvider.getLocationHierarchy(tokenParam);
    }
}
