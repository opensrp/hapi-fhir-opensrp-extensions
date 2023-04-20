/*
 * Copyright 2022 Ona Systems, Inc
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import ca.uhn.fhir.jpa.api.dao.IFhirResourceDao;
import ca.uhn.fhir.jpa.searchparam.SearchParameterMap;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.TokenParam;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.smartregister.model.location.LocationHierarchy;
import org.smartregister.model.practitioner.PractitionerDetails;

public class PractitionerDetailsResourceProviderTest {

    @Mock private IFhirResourceDao<Practitioner> practitionerIFhirResourceDao;

    @Mock private IFhirResourceDao<PractitionerRole> practitionerRoleIFhirResourceDao;

    @Mock private IFhirResourceDao<CareTeam> careTeamIFhirResourceDao;

    @Mock private IFhirResourceDao<OrganizationAffiliation> organizationAffiliationIFhirResourceDao;

    @Mock private IFhirResourceDao<Organization> organizationIFhirResourceDao;

    @Mock private IFhirResourceDao<Location> locationIFhirResourceDao;

    @Mock private IFhirResourceDao<Group> groupIFhirResourceDao;

    @Mock private IBundleProvider practitionersBundleProvider;

    @Mock private IBundleProvider careTeamsBundleProvider;

    @Mock private IBundleProvider practitionerRolesBundleProvider;

    @Mock private IBundleProvider organizationsBundleProvider;

    @Mock private IBundleProvider organizationsAffiliationBundleProvider;

    @Mock private IBundleProvider locationsBundleProvider;

    @Mock private LocationHierarchyResourceProvider locationHierarchyResourceProvider;

    @Mock private IBundleProvider groupsBundleProvider;

    private PractitionerDetailsResourceProvider practitionerDetailsResourceProvider;

    private final List<String> roles = Arrays.asList("ROLE_USER", "ROLE_ADMIN");

    @Before
    public void setup() {
        initMocks(this);
        practitionerDetailsResourceProvider = new PractitionerDetailsResourceProvider();
        practitionerDetailsResourceProvider.setPractitionerIFhirResourceDao(
                practitionerIFhirResourceDao);
        practitionerDetailsResourceProvider.setCareTeamIFhirResourceDao(careTeamIFhirResourceDao);
        practitionerDetailsResourceProvider.setPractitionerRoleIFhirResourceDao(
                practitionerRoleIFhirResourceDao);
        practitionerDetailsResourceProvider.setOrganizationIFhirResourceDao(
                organizationIFhirResourceDao);
        practitionerDetailsResourceProvider.setOrganizationAffiliationIFhirResourceDao(
                organizationAffiliationIFhirResourceDao);
        practitionerDetailsResourceProvider.setLocationHierarchyResourceProvider(
                locationHierarchyResourceProvider);
        practitionerDetailsResourceProvider.setLocationIFhirResourceDao(locationIFhirResourceDao);
        practitionerDetailsResourceProvider.setGroupIFhirResourceDao(groupIFhirResourceDao);
    }

    @Test
    public void testGetPractitionerDetailsWhenPractitionerNotFound() {
        TokenParam identifierParam = new TokenParam();
        identifierParam.setValue("0000-11111-2222-3333");
        when(practitionerIFhirResourceDao.search(any(SearchParameterMap.class)))
                .thenReturn(practitionersBundleProvider);
        List<IBaseResource> practitioners = new ArrayList<>();
        when(practitionersBundleProvider.getResources(anyInt(), anyInt()))
                .thenReturn(practitioners);
        PractitionerDetails practitionerDetails =
                practitionerDetailsResourceProvider.getPractitionerDetails(identifierParam);
        assertNotNull(practitionerDetails);
        assertEquals("Practitioner Not Found", practitionerDetails.getId());
    }

    @Test
    public void testGetPractitionerDetailsReturnsCorrectInformation() {
        LocationHierarchy locationHierarchy = new LocationHierarchy();
        when(practitionerIFhirResourceDao.search(any(SearchParameterMap.class)))
                .thenReturn(practitionersBundleProvider);
        when(careTeamIFhirResourceDao.search(any(SearchParameterMap.class)))
                .thenReturn(careTeamsBundleProvider);
        when(practitionerRoleIFhirResourceDao.search(any(SearchParameterMap.class)))
                .thenReturn(practitionerRolesBundleProvider);
        when(organizationIFhirResourceDao.search(any(SearchParameterMap.class)))
                .thenReturn(organizationsBundleProvider);
        when(organizationAffiliationIFhirResourceDao.search(any(SearchParameterMap.class)))
                .thenReturn(organizationsAffiliationBundleProvider);
        when(organizationAffiliationIFhirResourceDao.search(any(SearchParameterMap.class)))
                .thenReturn(organizationsAffiliationBundleProvider);
        when(organizationAffiliationIFhirResourceDao.search(any(SearchParameterMap.class)))
                .thenReturn(organizationsAffiliationBundleProvider);
        when(locationIFhirResourceDao.search(any(SearchParameterMap.class)))
                .thenReturn(locationsBundleProvider);
        when(groupIFhirResourceDao.search(any(SearchParameterMap.class)))
                .thenReturn(groupsBundleProvider);
        List<IBaseResource> practitioners = getPractitioners();

        List<IBaseResource> careTeams = getCareTeams();

        List<IBaseResource> practitionerRoles = getPractitionerRoles();

        List<IBaseResource> organizations = getOrganizations();

        List<IBaseResource> organizationsAffiliations = getOrganizationAffiliations();

        List<IBaseResource> locations = getLocations();

        List<IBaseResource> groups = getGroups();

        when(practitionersBundleProvider.getResources(anyInt(), anyInt()))
                .thenReturn(practitioners);
        when(careTeamsBundleProvider.getResources(anyInt(), anyInt())).thenReturn(careTeams);
        when(practitionerRolesBundleProvider.getResources(anyInt(), anyInt()))
                .thenReturn(practitionerRoles);
        when(organizationsBundleProvider.getResources(anyInt(), anyInt()))
                .thenReturn(organizations);
        when(organizationsAffiliationBundleProvider.getResources(anyInt(), anyInt()))
                .thenReturn(organizationsAffiliations);
        when(locationsBundleProvider.getResources(anyInt(), anyInt())).thenReturn(locations);
        when(groupsBundleProvider.getResources(anyInt(), anyInt())).thenReturn(groups);

        TokenParam identifierParam = new TokenParam();
        identifierParam.setValue("0000-11111-2222-3333");
        PractitionerDetails practitionerDetails =
                practitionerDetailsResourceProvider.getPractitionerDetails(identifierParam);
        assertNotNull(practitionerDetails);
    }

    private List<IBaseResource> getPractitioners() {
        List<IBaseResource> practitioners = new ArrayList<>();
        Practitioner practitioner = new Practitioner();
        practitioner.setActive(true);
        practitioner.setId("1");
        practitioners.add(practitioner);
        return practitioners;
    }

    private List<IBaseResource> getCareTeams() {
        List<IBaseResource> careTeams = new ArrayList<>();
        CareTeam careTeam = new CareTeam();
        careTeam.setName("Test Care Team");
        careTeam.setId("1");
        careTeams.add(careTeam);
        return careTeams;
    }

    private List<IBaseResource> getPractitionerRoles() {
        List<IBaseResource> practitionerRoles = new ArrayList<>();
        PractitionerRole practitionerRole = new PractitionerRole();
        practitionerRole.setActive(true);
        practitionerRole.setId("1");
        practitionerRoles.add(practitionerRole);
        return practitionerRoles;
    }

    private List<IBaseResource> getOrganizations() {
        List<IBaseResource> organizations = new ArrayList<>();
        Organization organization = new Organization();
        organization.setId("1");
        organization.setName("Test Organization");
        organizations.add(organization);
        return organizations;
    }

    private List<IBaseResource> getOrganizationAffiliations() {
        List<IBaseResource> organizationsAffiliations = new ArrayList<>();
        OrganizationAffiliation organizationAffiliation = new OrganizationAffiliation();
        organizationAffiliation.setId("1");
        List<Reference> locationReferences = new ArrayList<>();
        Reference locationRef = new Reference();
        locationRef.setReference("Location/140");
        locationRef.setDisplay("Location Reference");
        locationReferences.add(locationRef);
        organizationAffiliation.setLocation(locationReferences);
        organizationsAffiliations.add(organizationAffiliation);
        return organizationsAffiliations;
    }

    private List<IBaseResource> getLocations() {
        List<IBaseResource> locations = new ArrayList<>();
        Location location = new Location();
        location.setId("1");
        location.setName("Test Location");
        locations.add(location);
        return locations;
    }

    private List<IBaseResource> getGroups() {
        List<IBaseResource> groups = new ArrayList<>();
        Group group = new Group();
        group.setId("1");
        group.setName("Test Group");
        groups.add(group);
        return groups;
    }
}
