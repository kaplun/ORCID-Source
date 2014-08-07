/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.api.common.security.oauth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.orcid.core.security.MethodNotAllowedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenEndpointFilter;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 2013-2014 ORCID
 * 
 * @author Angel Montenegro (amontenegro) Date: 27/03/2014
 */
public class OrcidT1Oauth2TokenEndPointFilter extends ClientCredentialsTokenEndpointFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidT1Oauth2TokenEndPointFilter.class);

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        if (request.getMethod().equals(RequestMethod.GET.name())) {
            String message = "Method GET is not supported for token requests. POST IS supported, "
                    + "but BASIC authentication is the preferred method of authenticating clients.";
            InvalidRequestException ire = new InvalidRequestException(message);
            throw new MethodNotAllowedException(message, ire);
        }

        String clientId = request.getParameter("client_id");
        String clientSecret = request.getParameter("client_secret");

        LOGGER.info("About to attempt authentication: clientId={}", clientId);

        // If the request is already authenticated we can assume that this
        // filter is not needed
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            LOGGER.info("Already got authentication in security context holder: principal={}, name={}", authentication.getPrincipal(), authentication.getName());
            return authentication;
        }

        if (clientId == null) {
            throw new BadCredentialsException("No client credentials presented");
        }

        if (clientSecret == null) {
            clientSecret = "";
        }

        clientId = clientId.trim();
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(clientId, clientSecret);

        Authentication authenticationResult = this.getAuthenticationManager().authenticate(authRequest);
        if (authenticationResult != null) {
            LOGGER.info("Got authentication result: principal={}, name={}", authenticationResult.getPrincipal(), authenticationResult.getName());
        }
        return authenticationResult;
    }

}