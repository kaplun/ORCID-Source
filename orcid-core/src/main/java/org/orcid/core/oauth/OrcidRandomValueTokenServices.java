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

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;

/**
 * @author Angel Montenegro
 * */
public interface OrcidRandomValueTokenServices {

    OAuth2AccessToken createAccessToken(OAuth2Authentication authentication) throws AuthenticationException;

    OAuth2Authentication loadAuthentication(String accessTokenValue) throws AuthenticationException;

    int getWriteValiditySeconds();

    int getReadValiditySeconds();

    void setOrcidtokenStore(TokenStore orcidtokenStore);

    void setCustomTokenEnhancer(TokenEnhancer customTokenEnhancer);
}
