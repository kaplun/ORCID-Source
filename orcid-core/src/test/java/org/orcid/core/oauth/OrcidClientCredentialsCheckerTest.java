/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.oauth;

import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientScopeEntity;
import org.springframework.security.oauth2.common.exceptions.InvalidScopeException;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;

/**
 * @author Declan Newman (declan) Date: 11/05/2012
 */
public class OrcidClientCredentialsCheckerTest {

    @Mock
    private ClientDetailsService clientDetailsService;

    private OAuth2RequestFactory oAuth2RequestFactory;
    
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        oAuth2RequestFactory = new DefaultOAuth2RequestFactory(clientDetailsService);
    }

    @Test(expected = InvalidScopeException.class)
    public void testInvalidCredentialsScopes() throws Exception {
        ClientDetailsEntity clientDetailsEntity = new ClientDetailsEntity();
        Set<ClientScopeEntity> scopes = new HashSet<ClientScopeEntity>(2);
        scopes.add(new ClientScopeEntity(ScopePathType.ORCID_WORKS_UPDATE.value()));
        scopes.add(new ClientScopeEntity(ScopePathType.ORCID_BIO_READ_LIMITED.value()));
        clientDetailsEntity.setClientScopes(scopes);
        String orcid = "2875-8158-1475-6194";
        when(clientDetailsService.loadClientByClientId(orcid)).thenReturn(clientDetailsEntity);
        OrcidClientCredentialsChecker checker = new OrcidClientCredentialsChecker(clientDetailsService, oAuth2RequestFactory);
        Set<String> requestedScopes = new HashSet<String>(Arrays.asList(ScopePathType.ORCID_WORKS_UPDATE.value()));
        checker.validateCredentials("client_credentials", new TokenRequest(Collections.<String, String> emptyMap(), orcid, requestedScopes, "client_credentials"));        
    }

    @Test
    public void testValidCredentialsScopes() throws Exception {
        ClientDetailsEntity clientDetailsEntity = new ClientDetailsEntity();
        Set<ClientScopeEntity> scopes = new HashSet<ClientScopeEntity>(2);
        scopes.add(new ClientScopeEntity(ScopePathType.ORCID_WORKS_UPDATE.value()));
        scopes.add(new ClientScopeEntity(ScopePathType.ORCID_BIO_READ_LIMITED.value()));
        clientDetailsEntity.setClientScopes(scopes);
        String orcid = "2875-8158-1475-6194";
        when(clientDetailsService.loadClientByClientId(orcid)).thenReturn(clientDetailsEntity);
        OrcidClientCredentialsChecker checker = new OrcidClientCredentialsChecker(clientDetailsService, oAuth2RequestFactory);
        Set<String> requestedScopes = new HashSet<String>(Arrays.asList(ScopePathType.READ_PUBLIC.value()));        
        checker.validateCredentials("client_credentials", new TokenRequest(Collections.<String, String> emptyMap(), orcid, requestedScopes, "client_credentials"));
    }

    @Test
    public void testValidCredentialsScopesForClientOnly() throws Exception {
        ClientDetailsEntity clientDetailsEntity = new ClientDetailsEntity();
        Set<ClientScopeEntity> scopes = new HashSet<ClientScopeEntity>(2);
        scopes.add(new ClientScopeEntity(ScopePathType.ORCID_PROFILE_CREATE.value()));
        clientDetailsEntity.setClientScopes(scopes);
        String orcid = "2875-8158-1475-6194";
        when(clientDetailsService.loadClientByClientId(orcid)).thenReturn(clientDetailsEntity);
        OrcidClientCredentialsChecker checker = new OrcidClientCredentialsChecker(clientDetailsService, oAuth2RequestFactory);
        Set<String> requestedScopes = new HashSet<String>(Arrays.asList(ScopePathType.READ_PUBLIC.value()));        
        checker.validateCredentials("client_credentials", new TokenRequest(Collections.<String, String> emptyMap(), orcid, requestedScopes, "client_credentials"));
    }
}
