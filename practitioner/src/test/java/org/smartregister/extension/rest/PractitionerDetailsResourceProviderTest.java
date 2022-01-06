package org.smartregister.extension.rest;

import ca.uhn.fhir.jpa.api.dao.IFhirResourceDao;
import ca.uhn.fhir.rest.param.TokenParam;
import org.hl7.fhir.r4.model.Practitioner;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.smartregister.extension.model.PractitionerDetails;
import org.springframework.security.core.parameters.P;

import static org.mockito.MockitoAnnotations.initMocks;

public class PractitionerDetailsResourceProviderTest {

    @Mock
    private IFhirResourceDao<Practitioner> practitionerIFhirResourceDao;

    private PractitionerDetailsResourceProvider practitionerDetailsResourceProvider;

    @Before
    public void setup() {
        initMocks(this);
        practitionerDetailsResourceProvider = new PractitionerDetailsResourceProvider();
        practitionerDetailsResourceProvider.setPractitionerIFhirResourceDao(practitionerIFhirResourceDao);
    }

    @Test
    public void testGetPractitionerDetails() {
        TokenParam identifierParam = new TokenParam();
        identifierParam.setValue("0000-11111-2222-3333");
        PractitionerDetails practitionerDetails = practitionerDetailsResourceProvider.getPractitionerDetails(identifierParam);
    }
}
