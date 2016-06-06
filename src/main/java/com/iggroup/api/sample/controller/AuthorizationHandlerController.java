package com.iggroup.api.sample.controller;

import com.iggroup.api.sample.controller.dto.AccessTokenResponse;
import com.iggroup.api.sample.controller.dto.UserInformationResponse;
import com.iggroup.api.sample.service.OAuthSessions;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
public class AuthorizationHandlerController {

    private static final Logger LOG = LoggerFactory.getLogger(AuthorizationHandlerController.class);

    private final OAuthSessions oAuthSessions;
    private final AccessTokenController accessTokenController;
    private final UserInformationController userInformationController;
    private final String baseUrl;

    @Autowired
    public AuthorizationHandlerController(OAuthSessions oAuthSessions,
                                          AccessTokenController accessTokenController,
                                          UserInformationController userInformationController,
                                          String baseUrl) {
        this.oAuthSessions = oAuthSessions;
        this.accessTokenController = accessTokenController;
        this.userInformationController = userInformationController;
        this.baseUrl = baseUrl;
    }

    @RequestMapping("/authorization-handler")
    public void handle(@RequestParam MultiValueMap<String, String> queryParameters, HttpServletResponse httpResponse) throws Exception {
        if(queryParameters.containsKey("error")) {
            LOG.error(queryParameters.getFirst("error_description"));
            return;
        }
        String authorizationCode = queryParameters.getFirst("code");
        String state = queryParameters.getFirst("state");
        String stateSession = "af0ifjsldkj";
        if(StringUtils.isBlank(state) || !state.equals(stateSession)) {
            LOG.error("The request state={} does not match the session state={}", state, stateSession);
            return;
        }
        LOG.info("Received authorization code={}", authorizationCode);

        // Request an access token
        AccessTokenResponse accessTokenResponse = accessTokenController.getAccessToken(authorizationCode);
        LOG.info("Access token response for authorization code={}: {}", authorizationCode, accessTokenResponse);

        // Retrieve user information
        String accessToken = accessTokenResponse.getAccess_token();
        UserInformationResponse userInformationResponse  = userInformationController.getUserInformation(accessToken);
        LOG.info("User information for access token={}: {}", accessToken, userInformationResponse);

        // Store refresh token
        String clientId = userInformationResponse.getSub();
        String refreshToken = accessTokenResponse.getRefresh_token();
        String expiresIn = accessTokenResponse.getExpires_in();
        LOG.info("Storing refreshToken={} for clientId={}", refreshToken, clientId);
        oAuthSessions.setRefreshToken(clientId, refreshToken);

        // Redirect to result page
        httpResponse.sendRedirect(baseUrl + String.format("/result.html?clientId=%s&accessToken=%s&expiresIn=%s", clientId, accessToken, expiresIn));
    }

}