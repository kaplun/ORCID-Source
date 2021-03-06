<#--

    =============================================================================

    ORCID (R) Open Source
    http://orcid.org

    Copyright (c) 2012-2014 ORCID, Inc.
    Licensed under an MIT-Style License (MIT)
    http://orcid.org/open-source-license

    This copyright and license information (including a link to the full license)
    shall be included in its entirety in all copies or substantial portion of
    the software.

    =============================================================================

-->
<@base>
<#assign displayName = "">
<#if client_name??>
	<#assign displayName = client_name>
</#if>
<!-- colorbox-content -->
<div class="container top-green-border confirm-oauth-access oneStepWidth" ng-controller="OauthAuthorizationController">		
	<!-- Freemarker and GA variables -->
	<#assign user_id = "">			
	<#if userId??>
		<#assign user_id = userId>
	</#if>
	
	<#assign js_scopes_string = "">                
	<#list scopes as scope>
       	<#assign js_scopes_string = js_scopes_string + scope.name()?replace("ORCID_", "")?js_string + " ">
	</#list>
	
	<!-- /Freemarker and GA variables -->
	<@security.authorize ifAnyGranted="ROLE_USER">
	<div class="row top-header">
		<div class="col-md-4 col-sm-12 col-xs-12">
			<div class="logo">
	        	<h1><a href="${aboutUri}"><img src="${staticCdn}/img/orcid-logo.png" alt="ORCID logo" /></a></h1>
	        	<!-- <p>${springMacroRequestContext.getMessage("confirm-oauth-access.connectingresearchandresearchers")}</p> -->
	        </div>		
		</div>
		
	    <div class="col-md-8 col-sm-12 col-xs-12">
	         <#include "includes/mini_id_banner.ftl"/>	              
	    </div>	    
	</div>	
	<div class="row">
		<div class="col-md-12">	
		<div class="app-client-name" ng-init="initGroupClientNameAndScopes('${client_group_name?js_string}','${client_name?js_string}','${js_scopes_string}')">
			<h3 ng-click="toggleClientDescription()">${client_name}
				<a class="glyphicon glyphicon-question-sign oauth-question-sign"></a>				
			</h3>
		</div>
		<div class="app-client-description">
			<p ng-show="showClientDescription">
				<span class="uppercase gray-bold-about"><@orcid.msg 'oauth_sign_in.about'/></span> ${client_description}
			</p>
		</div>
		<div>
			<p><@orcid.msg 'orcid.frontend.oauth.have_asked'/></p>
		</div>
		<div>
			<#include "includes/oauth/scopes.ftl"/>
		</div>
		<div>
			<p><@orcid.msg 'orcid.frontend.web.oauth_is_secure'/>.&nbsp;<a href="${aboutUri}/footer/privacy-policy" target="_blank"><@orcid.msg 'public-layout.privacy_policy'/></a>.</p>
		</div>			
		<div id="login-buttons" ng-init="loadAndInitAuthorizationForm('${scopesString}','${redirect_uri}','${client_id}','${response_type}')">
			<div class="row">
	            <div class="col-md-12">                     		            		               					
					<button class="btn btn-primary pull-right" name="authorize" value="<@orcid.msg 'confirm-oauth-access.Authorize'/>" ng-click="authorize()">
						<@orcid.msg 'confirm-oauth-access.Authorize' />
					</button>		                 	            
					<a class="oauth_deny_link pull-right" name="deny" value="<@orcid.msg 'confirm-oauth-access.Deny'/>" ng-click="deny()">
						<@orcid.msg 'confirm-oauth-access.Deny' />
					</a>
				</div>					
			</div>
		</div>
	</div>
	</@security.authorize>
</div>
</@base>
