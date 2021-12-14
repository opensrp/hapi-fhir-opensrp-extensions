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

import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.param.*;
import ca.uhn.fhir.rest.server.IResourceProvider;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.StringType;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.smartregister.extension.model.KeycloakUserDetails;
import org.smartregister.extension.model.PractitionerDetails;
import org.smartregister.extension.model.UserBioData;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class PractitionerDetailsResourceProvider implements IResourceProvider {

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

        PractitionerDetails practitionerDetails = new PractitionerDetails();
        practitionerDetails.setId("testttt");
        practitionerDetails.setUserDetail(keycloakUserDetails);
        return practitionerDetails;
    }
}
