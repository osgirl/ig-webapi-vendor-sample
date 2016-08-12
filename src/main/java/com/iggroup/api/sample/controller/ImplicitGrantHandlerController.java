package com.iggroup.api.sample.controller;

import com.iggroup.api.sample.controller.dto.UserInformationResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * Implicit grant controller to render the result receive by implicit-grant-handler.html as a result of an OAuth redirect with an access token.
 */
@RestController
public class ImplicitGrantHandlerController {

    private static final Logger LOG = LoggerFactory.getLogger(ImplicitGrantHandlerController.class);

    private final UserInformationController userInformationController;
    private final String baseUrl;

    @Autowired
    public ImplicitGrantHandlerController(UserInformationController userInformationController,
                                          String baseUrl) {
        this.userInformationController = userInformationController;
        this.baseUrl = baseUrl;
    }

    @RequestMapping("/show-result")
    public void handle(@RequestParam MultiValueMap<String, String> queryParameters, HttpServletResponse httpResponse) throws Exception {
        String accessToken = queryParameters.getFirst("access_token");
        String expiresIn = queryParameters.getFirst("expires_in");
        String state = queryParameters.getFirst("state");
        String stateSession = "af0ifjsldkj";
        if(StringUtils.isBlank(state) || !state.equals(stateSession)) {
            LOG.error("The request state={} does not match the session state={}", state, stateSession);
            httpResponse.sendError(403, "tamper protection");
            return;
        }
        LOG.info("Received access token={}", accessToken);

        // Retrieve user information
        UserInformationResponse userInformationResponse  = userInformationController.getUserInformation(accessToken);
        LOG.info("User information for access token={}: {}", accessToken, userInformationResponse);
        String clientId = userInformationResponse.getSub();

        // Redirect to result page
        httpResponse.sendRedirect(baseUrl + String.format("/result.html?clientId=%s&accessToken=%s&expiresIn=%s", clientId, accessToken, expiresIn));
    }
}