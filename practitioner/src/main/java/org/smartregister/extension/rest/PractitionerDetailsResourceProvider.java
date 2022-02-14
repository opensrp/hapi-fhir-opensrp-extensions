/*
 * Copyright 2021 Ona Systems, Inc
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

import static org.smartregister.utils.Constants.*;
import static org.smartregister.utils.Constants.IDENTIFIER;

import ca.uhn.fhir.jpa.api.dao.IFhirResourceDao;
import ca.uhn.fhir.jpa.searchparam.SearchParameterMap;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.*;
import ca.uhn.fhir.rest.server.IResourceProvider;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.*;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.smartregister.model.location.LocationHierarchy;
import org.smartregister.model.practitioner.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class PractitionerDetailsResourceProvider implements IResourceProvider {

    @Autowired private IFhirResourceDao<Practitioner> practitionerIFhirResourceDao;

    @Autowired private IFhirResourceDao<PractitionerRole> practitionerRoleIFhirResourceDao;

    @Autowired private IFhirResourceDao<CareTeam> careTeamIFhirResourceDao;

    @Autowired
    private IFhirResourceDao<OrganizationAffiliation> organizationAffiliationIFhirResourceDao;

    @Autowired private IFhirResourceDao<Organization> organizationIFhirResourceDao;

    @Autowired private LocationHierarchyResourceProvider locationHierarchyResourceProvider;

    @Autowired private IFhirResourceDao<Location> locationIFhirResourceDao;

    private static Logger logger =
            LogManager.getLogger(PractitionerDetailsResourceProvider.class.toString());

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return PractitionerDetails.class;
    }

    @Search
    public PractitionerDetails getPractitionerDetails(
            @RequiredParam(name = KEYCLOAK_UUID) TokenParam identifier) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        KeycloakUserDetails keycloakUserDetails = new KeycloakUserDetails();
        if (authentication != null) {
            keycloakUserDetails = getKeycloakUserDetails(authentication);
        }
        PractitionerDetails practitionerDetails = new PractitionerDetails();

        if (keycloakUserDetails != null
                && keycloakUserDetails.getUserBioData() != null
                && keycloakUserDetails.getUserBioData().getIdentifier() != null) {
            FhirPractitionerDetails fhirPractitionerDetails = new FhirPractitionerDetails();
            SearchParameterMap paramMap = new SearchParameterMap();
            paramMap.add(IDENTIFIER, identifier);
            logger.info("Searching for practitioner with identifier: " + identifier.getValue());
            IBundleProvider practitionerBundle = practitionerIFhirResourceDao.search(paramMap);
            List<IBaseResource> practitioners =
                    practitionerBundle != null
                            ? practitionerBundle.getResources(0, practitionerBundle.size())
                            : new ArrayList<>();

            IBaseResource practitioner =
                    practitioners.size() > 0 ? practitioners.get(0) : new Practitioner();
            Long practitionerId =
                    practitioner.getIdElement() != null
                                    && practitioner.getIdElement().getIdPart() != null
                            ? practitioner.getIdElement().getIdPartAsLong()
                            : 0;

            if (practitionerId != null && practitionerId > 0) {
                logger.info("Searching for care teams for practitioner with id: " + practitionerId);
                List<IBaseResource> careTeams = getCareTeams(practitionerId);
                List<CareTeam> careTeamsList = mapToCareTeams(careTeams);
                fhirPractitionerDetails.setCareTeams(careTeamsList);
                StringType practitionerIdString = new StringType();
                practitionerIdString.setValue(String.valueOf(practitionerId));
                fhirPractitionerDetails.setPractitionerId(practitionerIdString);
                logger.info(
                        "Searching for organizations of practitioner with id: " + practitionerId);
                List<IBaseResource> organizationTeams =
                        getOrganizationsOfPractitioner(practitionerId);
                List<Organization> teams = mapToTeams(organizationTeams);
                fhirPractitionerDetails.setOrganizations(teams);
                keycloakUserDetails.setId(identifier.getValue());
                practitionerDetails.setId(keycloakUserDetails.getId());
                practitionerDetails.setUserDetail(keycloakUserDetails);
                fhirPractitionerDetails.setId(practitionerIdString.getValue());
                logger.info("Searching for locations by organizations");
                List<String> locationsIdReferences = getLocationIdentifiersByOrganizations(teams);
                List<Long> locationIds = getLocationIdsFromReferences(locationsIdReferences);
                List<String> locationsIdentifiers = getLocationIdentifiersByIds(locationIds);
                logger.info("Searching for location heirarchy list by locations identifiers");
                List<LocationHierarchy> locationHierarchyList =
                        getLocationsHierarchy(locationsIdentifiers);
                fhirPractitionerDetails.setLocationHierarchyList(locationHierarchyList);
                logger.info("Searching for locations by ids");
                List<Location> locationsList = getLocationsByIds(locationIds);
                fhirPractitionerDetails.setLocations(locationsList);
                practitionerDetails.setFhirPractitionerDetails(fhirPractitionerDetails);
            } else {
                logger.error(
                        "Practitioner with identifier: " + identifier.getValue() + " not found");
                practitionerDetails.setId(PRACTITIONER_NOT_FOUND);
            }
        } else {
            logger.error("User details are null");
            practitionerDetails.setId(KEYCLOAK_USER_NOT_FOUND);
        }
        return practitionerDetails;
    }

    private KeycloakUserDetails getKeycloakUserDetails(Authentication authentication) {
        KeycloakUserDetails keycloakUserDetails = new KeycloakUserDetails();
        if (authentication != null) {
            logger.info("Authentication is not null");
            KeycloakPrincipal<KeycloakSecurityContext> kp =
                    (KeycloakPrincipal<KeycloakSecurityContext>) authentication.getPrincipal();
            AccessToken token = kp.getKeycloakSecurityContext().getToken();

            StringType authenticationIdentifier = new StringType();
            authenticationIdentifier.setId(authentication.getName());
            authenticationIdentifier.setValue(authentication.getName());

            UserBioData userBioData = new UserBioData();
            userBioData.setIdentifier(authenticationIdentifier);

            StringType userName = new StringType();
            userName.setId(USERNAME);
            userName.setValue(token.getPreferredUsername());
            userBioData.setUserName(userName);

            StringType preferredUserName = new StringType();
            preferredUserName.setId(PREFFERED_USERNAME);
            preferredUserName.setValue(token.getPreferredUsername());
            userBioData.setPreferredName(preferredUserName);

            StringType familyName = new StringType();
            familyName.setId(FAMILY_NAME);
            familyName.setValue(token.getFamilyName());
            userBioData.setFamilyName(familyName);

            StringType givenName = new StringType();
            givenName.setId(GIVEN_NAME);
            givenName.setValue(token.getGivenName());
            userBioData.setGivenName(givenName);

            StringType email = new StringType();
            email.setId(EMAIL);
            email.setValue(token.getEmail());
            userBioData.setEmail(email);

            StringType emailVerified = new StringType();
            emailVerified.setId(EMAIL_VERIFIED);
            emailVerified.setValue(String.valueOf(token.getEmailVerified()));
            userBioData.setEmailVerified(emailVerified);

            List<StringType> roles = new ArrayList<>();
            List<String> rolesInString =
                    authentication.getAuthorities().stream()
                            .map(e -> e.getAuthority())
                            .collect(Collectors.toList());
            int i = 0;
            for (String role : rolesInString) {
                StringType userRole = new StringType();
                userRole.setId(ROLE + SPACE + COLON + SPACE + i);
                userRole.setValue(role);
                roles.add(userRole);
                i++;
            }

            keycloakUserDetails.setUserBioData(userBioData);
            keycloakUserDetails.setRoles(roles);
        }
        return keycloakUserDetails;
    }

    private List<IBaseResource> getCareTeams(Long practitionerId) {
        SearchParameterMap careTeamSerachParameterMap = new SearchParameterMap();
        ReferenceParam participantRef = new ReferenceParam();
        participantRef.setValue(String.valueOf(practitionerId));
        ReferenceOrListParam careTeamRefParam = new ReferenceOrListParam();
        careTeamRefParam.addOr(participantRef);
        careTeamSerachParameterMap.add(PARTICIPANT, careTeamRefParam);
        IBundleProvider careTeamsBundle =
                careTeamIFhirResourceDao.search(careTeamSerachParameterMap);
        return careTeamsBundle != null
                ? careTeamsBundle.getResources(0, careTeamsBundle.size())
                : new ArrayList<>();
    }

    private List<Location> getLocationsByIds(List<Long> locationIds) {
        List<Location> locations = new ArrayList<>();
        SearchParameterMap searchParameterMap = new SearchParameterMap();
        for (Long locationId : locationIds) {
            TokenAndListParam idParam = new TokenAndListParam();
            TokenParam id = new TokenParam();
            id.setValue(String.valueOf(locationId));
            idParam.addAnd(id);
            searchParameterMap.add(ID, idParam);
            IBundleProvider locationsBundle = locationIFhirResourceDao.search(searchParameterMap);
            List<IBaseResource> locationsResources =
                    locationsBundle != null
                            ? locationsBundle.getResources(0, locationsBundle.size())
                            : new ArrayList<>();
            Location locationObj;
            for (IBaseResource loc : locationsResources) {
                locationObj = (Location) loc;
                locations.add(locationObj);
            }
        }
        return locations;
    }

    private List<CareTeam> mapToCareTeams(List<IBaseResource> careTeams) {
        List<CareTeam> careTeamList = new ArrayList<>();
        CareTeam careTeamObj;
        for (IBaseResource careTeam : careTeams) {
            careTeamObj = (CareTeam) careTeam;
            careTeamList.add(careTeamObj);
        }
        return careTeamList;
    }

    private List<String> getOrganizationIdentifiers(Long practitionerId) {
        SearchParameterMap practitionerRoleSearchParamMap = new SearchParameterMap();
        ReferenceParam practitionerRef = new ReferenceParam();
        practitionerRef.setValue(String.valueOf(practitionerId));
        ReferenceOrListParam careTeamRefParam = new ReferenceOrListParam();
        careTeamRefParam.addOr(practitionerRef);
        practitionerRoleSearchParamMap.add(PRACTITIONER, practitionerRef);
        IBundleProvider practitionerRoleBundle =
                practitionerRoleIFhirResourceDao.search(practitionerRoleSearchParamMap);
        List<IBaseResource> practitionerRoles =
                practitionerRoleBundle != null
                        ? practitionerRoleBundle.getResources(0, practitionerRoleBundle.size())
                        : new ArrayList<>();
        List<String> organizationIds = new ArrayList<>();
        if (practitionerRoles != null && practitionerRoles.size() > 0) {
            for (IBaseResource practitionerRole : practitionerRoles) {
                PractitionerRole pRole = (PractitionerRole) practitionerRole;
                if (pRole.getOrganization() != null
                        && pRole.getOrganization().getIdentifier() != null) {
                    organizationIds.add(pRole.getOrganization().getIdentifier().getValue());
                }
            }
        }
        return organizationIds;
    }

    private List<IBaseResource> getOrganizationsOfPractitioner(Long practitionerId) {
        List<String> organizationIdentifiers = getOrganizationIdentifiers(practitionerId);
        SearchParameterMap organizationsSearchMap = new SearchParameterMap();
        TokenAndListParam theIdentifier = new TokenAndListParam();
        TokenOrListParam identifiersList = new TokenOrListParam();
        TokenParam identifier;
        for (String organizationIdentifier : organizationIdentifiers) {
            identifier = new TokenParam();
            identifier.setValue(organizationIdentifier);
            identifiersList.add(identifier);
        }

        theIdentifier.addAnd(identifiersList);
        organizationsSearchMap.add(IDENTIFIER, theIdentifier);
        IBundleProvider organizationsBundle =
                organizationIFhirResourceDao.search(organizationsSearchMap);
        return organizationsBundle != null
                ? organizationsBundle.getResources(0, organizationsBundle.size())
                : new ArrayList<>();
    }

    private List<Organization> mapToTeams(List<IBaseResource> teams) {
        List<Organization> organizations = new ArrayList<>();
        Organization organizationObj;
        for (IBaseResource team : teams) {
            organizationObj = (Organization) team;
            organizations.add(organizationObj);
        }
        return organizations;
    }

    private List<LocationHierarchy> getLocationsHierarchy(List<String> locationsIdentifiers) {
        List<LocationHierarchy> locationHierarchyList = new ArrayList<>();
        TokenParam identifier;
        LocationHierarchy locationHierarchy;
        for (String locationsIdentifier : locationsIdentifiers) {
            identifier = new TokenParam();
            identifier.setValue(locationsIdentifier);
            locationHierarchy = locationHierarchyResourceProvider.getLocationHierarchy(identifier);
            locationHierarchyList.add(locationHierarchy);
        }
        return locationHierarchyList;
    }

    private List<String> getLocationIdentifiersByOrganizations(List<Organization> organizations) {
        List<String> locationsIdentifiers = new ArrayList<>();
        SearchParameterMap searchParameterMap = new SearchParameterMap();
        for (Organization team : organizations) {
            ReferenceAndListParam thePrimaryOrganization = new ReferenceAndListParam();
            ReferenceOrListParam primaryOrganizationRefParam = new ReferenceOrListParam();
            ReferenceParam primaryOrganization = new ReferenceParam();
            primaryOrganization.setValue(team.getId());
            primaryOrganizationRefParam.addOr(primaryOrganization);
            thePrimaryOrganization.addAnd(primaryOrganizationRefParam);
            searchParameterMap.add(PRIMARY_ORGANIZATION, thePrimaryOrganization);
            IBundleProvider organizationsAffiliationBundle =
                    organizationAffiliationIFhirResourceDao.search(searchParameterMap);
            List<IBaseResource> organizationAffiliations =
                    organizationsAffiliationBundle != null
                            ? organizationsAffiliationBundle.getResources(
                                    0, organizationsAffiliationBundle.size())
                            : new ArrayList<>();
            OrganizationAffiliation organizationAffiliationObj;
            if (organizationAffiliations.size() > 0) {
                for (IBaseResource organizationAffiliation : organizationAffiliations) {
                    organizationAffiliationObj = (OrganizationAffiliation) organizationAffiliation;
                    List<Reference> locationList = organizationAffiliationObj.getLocation();
                    for (Reference location : locationList) {
                        if (location != null && location.getReference() != null) {
                            locationsIdentifiers.add(location.getReference());
                        }
                    }
                }
            }
        }
        return locationsIdentifiers;
    }

    private List<Long> getLocationIdsFromReferences(List<String> locationReferences) {
        List<Long> locationIds = new ArrayList<>();
        for (String locationRef : locationReferences) {
            if (locationRef.contains(FORWARD_SLASH)) {
                locationRef =
                        locationRef.substring(
                                locationRef.indexOf(FORWARD_SLASH) + 1, locationRef.length());
            }
            locationIds.add(Long.valueOf(locationRef));
        }
        return locationIds;
    }

    private List<String> getLocationIdentifiersByIds(List<Long> locationIds) {
        List<String> locationsIdentifiers = new ArrayList<>();
        SearchParameterMap searchParameterMap = new SearchParameterMap();
        for (Long locationId : locationIds) {
            TokenAndListParam idParam = new TokenAndListParam();
            TokenParam id = new TokenParam();
            id.setValue(String.valueOf(locationId));
            idParam.addAnd(id);
            searchParameterMap.add(ID, idParam);
            IBundleProvider locationsBundle = locationIFhirResourceDao.search(searchParameterMap);
            List<IBaseResource> locationsResources =
                    locationsBundle != null
                            ? locationsBundle.getResources(0, locationsBundle.size())
                            : new ArrayList<>();
            Location locationObj;
            for (IBaseResource loc : locationsResources) {
                locationObj = (Location) loc;
                locationsIdentifiers.addAll(
                        locationObj.getIdentifier().stream()
                                .map(
                                        locationIdentifier ->
                                                getLocationIdentifierValue(locationIdentifier))
                                .collect(Collectors.toList()));
            }
        }
        return locationsIdentifiers;
    }

    private String getLocationIdentifierValue(Identifier locationIdentifier) {
        if (locationIdentifier.getUse() != null
                && locationIdentifier.getUse().equals(Identifier.IdentifierUse.OFFICIAL)) {
            return locationIdentifier.getValue();
        }
        return EMPTY_STRING;
    }

    public IFhirResourceDao<Practitioner> getPractitionerIFhirResourceDao() {
        return practitionerIFhirResourceDao;
    }

    public void setPractitionerIFhirResourceDao(
            IFhirResourceDao<Practitioner> practitionerIFhirResourceDao) {
        this.practitionerIFhirResourceDao = practitionerIFhirResourceDao;
    }

    public IFhirResourceDao<PractitionerRole> getPractitionerRoleIFhirResourceDao() {
        return practitionerRoleIFhirResourceDao;
    }

    public void setPractitionerRoleIFhirResourceDao(
            IFhirResourceDao<PractitionerRole> practitionerRoleIFhirResourceDao) {
        this.practitionerRoleIFhirResourceDao = practitionerRoleIFhirResourceDao;
    }

    public IFhirResourceDao<CareTeam> getCareTeamIFhirResourceDao() {
        return careTeamIFhirResourceDao;
    }

    public void setCareTeamIFhirResourceDao(IFhirResourceDao<CareTeam> careTeamIFhirResourceDao) {
        this.careTeamIFhirResourceDao = careTeamIFhirResourceDao;
    }

    public IFhirResourceDao<OrganizationAffiliation> getOrganizationAffiliationIFhirResourceDao() {
        return organizationAffiliationIFhirResourceDao;
    }

    public void setOrganizationAffiliationIFhirResourceDao(
            IFhirResourceDao<OrganizationAffiliation> organizationAffiliationIFhirResourceDao) {
        this.organizationAffiliationIFhirResourceDao = organizationAffiliationIFhirResourceDao;
    }

    public IFhirResourceDao<Organization> getOrganizationIFhirResourceDao() {
        return organizationIFhirResourceDao;
    }

    public void setOrganizationIFhirResourceDao(
            IFhirResourceDao<Organization> organizationIFhirResourceDao) {
        this.organizationIFhirResourceDao = organizationIFhirResourceDao;
    }

    public LocationHierarchyResourceProvider getLocationHierarchyResourceProvider() {
        return locationHierarchyResourceProvider;
    }

    public void setLocationHierarchyResourceProvider(
            LocationHierarchyResourceProvider locationHierarchyResourceProvider) {
        this.locationHierarchyResourceProvider = locationHierarchyResourceProvider;
    }

    public IFhirResourceDao<Location> getLocationIFhirResourceDao() {
        return locationIFhirResourceDao;
    }

    public void setLocationIFhirResourceDao(IFhirResourceDao<Location> locationIFhirResourceDao) {
        this.locationIFhirResourceDao = locationIFhirResourceDao;
    }
}
