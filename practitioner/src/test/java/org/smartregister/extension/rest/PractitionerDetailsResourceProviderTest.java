package org.smartregister.extension.rest;

import ca.uhn.fhir.jpa.api.dao.IFhirResourceDao;
import ca.uhn.fhir.jpa.searchparam.SearchParameterMap;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.TokenParam;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.*;
import org.junit.Before;
import org.junit.Test;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.mockito.Mock;
import org.smartregister.extension.model.LocationHierarchy;
import org.smartregister.extension.model.PractitionerDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.sql.Ref;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PractitionerDetailsResourceProviderTest {

    @Mock
    private IFhirResourceDao<Practitioner> practitionerIFhirResourceDao;

    @Mock
    private IFhirResourceDao<PractitionerRole> practitionerRoleIFhirResourceDao;

    @Mock
    private IFhirResourceDao<CareTeam> careTeamIFhirResourceDao;

    @Mock
    private IFhirResourceDao<OrganizationAffiliation> organizationAffiliationIFhirResourceDao;

    @Mock
    private IFhirResourceDao<Organization> organizationIFhirResourceDao;

    @Mock
    private KeycloakPrincipal<KeycloakSecurityContext> keycloakPrincipal;

    @Mock
    private RefreshableKeycloakSecurityContext securityContext;

    @Mock
    private AccessToken token;

    @Mock
    private Authentication authentication;

    @Mock
    private IBundleProvider practitionersBUndleProvider;

    @Mock
    private IBundleProvider careTeamsBundleProvider;

    @Mock
    private IBundleProvider practitionerRolesBundleProvider;

    @Mock
    private IBundleProvider organizationsBundleProvider;

    @Mock
    private IBundleProvider organizationsAffiliationBundleProvider;

    @Mock
    private LocationHierarchyResourceProvider locationHierarchyResourceProvider;

    private PractitionerDetailsResourceProvider practitionerDetailsResourceProvider;

    private List<String> roles = Arrays.asList("ROLE_USER", "ROLE_ADMIN");

    @Before
    public void setup() {
        initMocks(this);
        practitionerDetailsResourceProvider = new PractitionerDetailsResourceProvider();
        practitionerDetailsResourceProvider.setPractitionerIFhirResourceDao(practitionerIFhirResourceDao);
        practitionerDetailsResourceProvider.setCareTeamIFhirResourceDao(careTeamIFhirResourceDao);
        practitionerDetailsResourceProvider.setPractitionerRoleIFhirResourceDao(practitionerRoleIFhirResourceDao);
        practitionerDetailsResourceProvider.setOrganizationIFhirResourceDao(organizationIFhirResourceDao);
        practitionerDetailsResourceProvider.setOrganizationAffiliationIFhirResourceDao(organizationAffiliationIFhirResourceDao);
        practitionerDetailsResourceProvider.setLocationHierarchyResourceProvider(locationHierarchyResourceProvider);
        when(keycloakPrincipal.getKeycloakSecurityContext()).thenReturn(securityContext);
    }

    @Test
    public void testGetPractitionerDetailsWhenKeycloakUserNotFound() {
        TokenParam identifierParam = new TokenParam();
        identifierParam.setValue("0000-11111-2222-3333");
        PractitionerDetails practitionerDetails = practitionerDetailsResourceProvider.getPractitionerDetails(identifierParam);
        assertNotNull(practitionerDetails);
        assertEquals("Keycloak User Not Found", practitionerDetails.getId());
    }

    @Test
    public void testGetPractitionerDetailsReturnsCorrectKeycloakUserInformation() {
        authentication.setAuthenticated(Boolean.TRUE);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(SecurityContextHolder.getContext().getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(keycloakPrincipal);
        when(keycloakPrincipal.getKeycloakSecurityContext().getToken()).thenReturn(token);
        when(token.getPreferredUsername()).thenReturn("TestUser");
        when(token.getFamilyName()).thenReturn("Test User Family");
        when(token.getGivenName()).thenReturn("Test User");
        when(token.getEmail()).thenReturn("user@testing.com");
        when(token.getEmailVerified()).thenReturn(Boolean.TRUE);
        when(authentication.getName()).thenReturn("Beta Test User");
        when(authentication.getAuthorities()).thenAnswer(a -> roles.stream().map(role -> new GrantedAuthority() {

            private static final long serialVersionUID = 1L;

            @Override
            public String getAuthority() {
                return role;
            }
        }).collect(Collectors.toList()));

        LocationHierarchy locationHierarchy = new LocationHierarchy();
        when(practitionerIFhirResourceDao.search(any(SearchParameterMap.class))).thenReturn(practitionersBUndleProvider);
        when(careTeamIFhirResourceDao.search(any(SearchParameterMap.class))).thenReturn(careTeamsBundleProvider);
        when(practitionerRoleIFhirResourceDao.search(any(SearchParameterMap.class))).thenReturn(practitionerRolesBundleProvider);
        when(organizationIFhirResourceDao.search(any(SearchParameterMap.class))).thenReturn(organizationsBundleProvider);
        when(organizationAffiliationIFhirResourceDao.search(any(SearchParameterMap.class))).thenReturn(organizationsAffiliationBundleProvider);
        when(locationHierarchyResourceProvider.getLocationHierarchy(any(TokenParam.class))).thenReturn(locationHierarchy);
        List<IBaseResource> practitioners = new ArrayList<>();
        Practitioner practitioner = new Practitioner();
        practitioner.setActive(true);
        practitioner.setId("1");
        practitioners.add(practitioner);

        List<IBaseResource> careTeams = new ArrayList<>();
        CareTeam careTeam = new CareTeam();
        careTeam.setName("Test Care Team");
        careTeam.setId("1");
        careTeams.add(careTeam);

        List<IBaseResource> practitionerRoles = new ArrayList<>();
        PractitionerRole practitionerRole = new PractitionerRole();
        practitionerRole.setActive(true);
        practitionerRole.setId("1");
        practitionerRoles.add(practitionerRole);

        List<IBaseResource> organizations = new ArrayList<>();
        Organization organization = new Organization();
        organization.setId("1");
        organization.setName("Test Organization");
        organizations.add(organization);

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
        when(practitionersBUndleProvider.getResources(anyInt(), anyInt())).thenReturn(practitioners);
        when(careTeamsBundleProvider.getResources(anyInt(), anyInt())).thenReturn(careTeams);
        when(practitionerRolesBundleProvider.getResources(anyInt(), anyInt())).thenReturn(practitionerRoles);
        when(organizationsBundleProvider.getResources(anyInt(), anyInt())).thenReturn(organizations);
        when(organizationsAffiliationBundleProvider.getResources(anyInt(), anyInt())).thenReturn(organizationsAffiliations);

        TokenParam identifierParam = new TokenParam();
        identifierParam.setValue("0000-11111-2222-3333");
        PractitionerDetails practitionerDetails = practitionerDetailsResourceProvider.getPractitionerDetails(identifierParam);
        assertNotNull(practitionerDetails);
    }


}
