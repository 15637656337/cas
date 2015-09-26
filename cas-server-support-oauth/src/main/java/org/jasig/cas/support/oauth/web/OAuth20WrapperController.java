/*
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.cas.support.oauth.web;

import org.apache.http.HttpStatus;
import org.jasig.cas.support.oauth.OAuthConstants;
import org.jasig.cas.support.oauth.OAuthUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This controller is the main entry point for OAuth version 2.0
 * wrapping in CAS, should be mapped to something like /oauth2.0/*. Dispatch
 * request to specific controllers : authorize, accessToken...
 *
 * @author Jerome Leleu
 * @since 3.5.0
 */
@Component("oauth20WrapperController")
public final class OAuth20WrapperController extends BaseOAuthWrapperController {

    @Resource(name="authorizeController")
    private OAuth20AuthorizeController authorizeController;

    @Resource(name="callbackAuthorizeController")
    private OAuth20CallbackAuthorizeController callbackAuthorizeController;

    @Resource(name="accessTokenController")
    private OAuth20AccessTokenController accessTokenController;

    @Resource(name="profileController")
    private OAuth20ProfileController profileController;

    @Override
    protected ModelAndView internalHandleRequest(final String method, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {

        // authorize
        if (OAuthConstants.AUTHORIZE_URL.equals(method)) {
            return authorizeController.handleRequest(request, response);
        }
        // callback on authorize
        if (OAuthConstants.CALLBACK_AUTHORIZE_URL.equals(method)) {
            return callbackAuthorizeController.handleRequest(request, response);
        }
        //get access token
        if (OAuthConstants.ACCESS_TOKEN_URL.equals(method)) {
            return accessTokenController.handleRequest(request, response);
        }
        // get profile
        if (OAuthConstants.PROFILE_URL.equals(method)) {
            return profileController.handleRequest(request, response);
        }

        // else error
        logger.error("Unknown method : {}", method);
        OAuthUtils.writeTextError(response, OAuthConstants.INVALID_REQUEST, HttpStatus.SC_OK);
        return null;
    }
}
