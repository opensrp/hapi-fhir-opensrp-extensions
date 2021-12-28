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
package org.smartregister.extension.model;

import ca.uhn.fhir.model.api.annotation.DatatypeDef;
import java.util.ArrayList;
import java.util.List;
import org.hl7.fhir.r4.model.*;

@DatatypeDef(name = "FhirCareTeamExtension")
public class FhirCareTeamExtension extends CareTeam {

    @Override
    public CareTeam copy() {
        CareTeam careTeam = new CareTeam();
        Bundle bundle = new Bundle();
        List<Bundle.BundleEntryComponent> theEntry = new ArrayList<>();
        Bundle.BundleEntryComponent entryComponent = new Bundle.BundleEntryComponent();
        entryComponent.setResource(new Bundle());
        theEntry.add(entryComponent);
        bundle.setEntry(theEntry);
        this.copyValues(careTeam);
        return careTeam;
    }

    public FhirCareTeamExtension mapValues(CareTeam dst) {
        FhirCareTeamExtension fhirCareTeamExtension = new FhirCareTeamExtension();
        if (dst != null) {
            if (dst.getId() != null) {
                fhirCareTeamExtension.setId(dst.getId());
            }
            if (dst.getIdentifier() != null) {
                fhirCareTeamExtension.setIdentifier(dst.getIdentifier());
            }
            if (dst.getStatus() != null) {
                fhirCareTeamExtension.setStatus(dst.getStatus());
            }
            if (dst.getCategory() != null) {
                fhirCareTeamExtension.setCategory(dst.getCategory());
            }
            if (dst.getName() != null) {
                fhirCareTeamExtension.setName(dst.getName());
            }
            if (dst.getSubject() != null) {
                fhirCareTeamExtension.setSubject(dst.getSubject());
            }
            if (dst.getEncounter() != null) {
                fhirCareTeamExtension.setEncounter(dst.getEncounter());
            }
            if (dst.getPeriod() != null) {
                fhirCareTeamExtension.setPeriod(dst.getPeriod());
            }
            if (dst.getParticipant() != null) {
                fhirCareTeamExtension.setParticipant(dst.getParticipant());
            }

            if (dst.getReasonCode() != null) {
                fhirCareTeamExtension.setReasonCode(dst.getReasonCode());
            }
            if (dst.getReasonReference() != null) {
                fhirCareTeamExtension.setReasonReference(dst.getReasonReference());
            }
            if (dst.getManagingOrganization() != null) {
                fhirCareTeamExtension.setManagingOrganization(dst.getManagingOrganization());
            }
            if (dst.getTelecom() != null) {
                fhirCareTeamExtension.setTelecom(dst.getTelecom());
            }
            if (dst.getNote() != null) {
                fhirCareTeamExtension.setNote(dst.getNote());
            }
        }

        return fhirCareTeamExtension;
    }
}
