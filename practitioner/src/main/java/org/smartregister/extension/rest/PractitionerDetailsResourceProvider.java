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

import static org.smartregister.extension.utils.Constants.*;
import static org.smartregister.extension.utils.Constants.IDENTIFIER;

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
import org.smartregister.extension.model.*;
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
        KeycloakUserDetails keycloakUserDetails = getKeycloakUserDetails(authentication);

        FhirPractitionerDetails fhirPractitionerDetails = new FhirPractitionerDetails();
        SearchParameterMap paramMap = new SearchParameterMap();
        paramMap.add(IDENTIFIER, identifier);
        IBundleProvider practitionerBundle = practitionerIFhirResourceDao.search(paramMap);
        List<IBaseResource> practitioners =
                practitionerBundle != null
                        ? practitionerBundle.getResources(0, practitionerBundle.size())
                        : new ArrayList<>();

        IBaseResource practitioner =
                practitioners.size() > 0 ? practitioners.get(0) : new Practitioner();
        Long practitionerId =
                practitioner.getIdElement() != null
                        ? practitioner.getIdElement().getIdPartAsLong()
                        : 0;

        List<IBaseResource> careTeams = getCareTeams(practitionerId);
        List<FhirCareTeamExtension> careTeamExtensions = mapToCareTeamExtensionsList(careTeams);

        fhirPractitionerDetails.setFhirCareTeamExtensionList(careTeamExtensions);
        StringType practitionerIdString = new StringType();
        practitionerIdString.setValue(String.valueOf(practitionerId));
        fhirPractitionerDetails.setPractitionerId(practitionerIdString);
        List<IBaseResource> organizationTeams = getOrganizationsOfPractitioner(practitionerId);
        List<FhirOrganizationExtension> teamExtensions = mapToTeamExtensionsList(organizationTeams);
        fhirPractitionerDetails.setFhirOrganizationExtensions(teamExtensions);
        PractitionerDetails practitionerDetails = new PractitionerDetails();
        keycloakUserDetails.setId(identifier.getValue());
        practitionerDetails.setId(keycloakUserDetails.getId());
        practitionerDetails.setUserDetail(keycloakUserDetails);
        fhirPractitionerDetails.setId("test");
        //        fhirPractitionerDetails.setCareteams(careTeams);
        //        List<FhirCareTeamExtension> testttt = new ArrayList<>();
        //        FhirCareTeamExtension fhirCareTeamExtension = new FhirCareTeamExtension();
        //        fhirCareTeamExtension.setId("hey");
        //        fhirCareTeamExtension.setName("name");
        //        testttt.add(fhirCareTeamExtension);
        //        fhirPractitionerDetails.setFhirCareTeamExtensionList(testttt);
        //        fhirPractitionerDetails.setTeam(organizationTeams);
        TokenParam tokenParam = new TokenParam();
        tokenParam.setValue("bbbb");
        locationHierarchyResourceProvider.getLocationHierarchy(tokenParam);
        practitionerDetails.setFhirPractitionerDetails(fhirPractitionerDetails);
        return practitionerDetails;
    }

    private KeycloakUserDetails getKeycloakUserDetails(Authentication authentication) {
        KeycloakUserDetails keycloakUserDetails = new KeycloakUserDetails();
        if (authentication != null) {
            KeycloakPrincipal<KeycloakSecurityContext> kp =
                    (KeycloakPrincipal<KeycloakSecurityContext>) authentication.getPrincipal();
            AccessToken token = kp.getKeycloakSecurityContext().getToken();

            StringType authenticationIdentifier = new StringType();
            authenticationIdentifier.setId(authentication.getName());
            authenticationIdentifier.setValue(authentication.getName());

            UserBioData userBioData = new UserBioData();
            userBioData.setIdentifier(authenticationIdentifier);

            StringType preferredUserName = new StringType();
            preferredUserName.setId("Preferred Username");
            preferredUserName.setValue(token.getPreferredUsername());
            userBioData.setPreferredName(preferredUserName);

            StringType familyName = new StringType();
            familyName.setId("Family Name");
            familyName.setValue(token.getFamilyName());
            userBioData.setFamilyName(familyName);

            StringType givenName = new StringType();
            givenName.setId("Given Name");
            givenName.setValue(token.getGivenName());
            userBioData.setGivenName(givenName);

            StringType email = new StringType();
            email.setId("Email");
            email.setValue(token.getEmail());
            userBioData.setEmail(email);

            StringType emailVerified = new StringType();
            emailVerified.setId("Email verified");
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
                userRole.setId("Role : " + i);
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

    private List<FhirCareTeamExtension> mapToCareTeamExtensionsList(List<IBaseResource> careTeams) {
        List<FhirCareTeamExtension> fhirCareTeamExtensionList = new ArrayList<>();
        CareTeam careTeamObj;
        FhirCareTeamExtension fhirCareTeamExtensionObj = new FhirCareTeamExtension();
        for (IBaseResource careTeam : careTeams) {
            careTeamObj = (CareTeam) careTeam;
            fhirCareTeamExtensionObj = fhirCareTeamExtensionObj.mapValues(careTeamObj);
            fhirCareTeamExtensionList.add(fhirCareTeamExtensionObj);
        }
        return fhirCareTeamExtensionList;
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

    private List<FhirOrganizationExtension> mapToTeamExtensionsList(List<IBaseResource> teams) {
        List<FhirOrganizationExtension> fhirOrganizationExtensions = new ArrayList<>();
        Organization organizationObj;
        FhirOrganizationExtension fhirOrganizationExtension = new FhirOrganizationExtension();
        for (IBaseResource team : teams) {
            organizationObj = (Organization) team;
            fhirOrganizationExtension = fhirOrganizationExtension.mapValues(organizationObj);
            fhirOrganizationExtensions.add(fhirOrganizationExtension);
        }
        return fhirOrganizationExtensions;
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
}
