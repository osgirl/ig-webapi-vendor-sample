package com.iggroup.api.sample.controller;

import com.iggroup.api.sample.controller.dto.ClientAccountsResponse;
import com.iggroup.api.sample.controller.dto.UserInformationResponse;
import com.iggroup.api.sample.service.OAuthSessions;
import com.iggroup.api.sample.service.ig.APIClient;
import com.iggroup.api.sample.service.ig.OAuthClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class UserInformationController {

    private OAuthClient oAuthClient;
    private APIClient apiClient;
    private OAuthSessions oAuthSessions;

    @Autowired
    Environment env;

    @Autowired
    public UserInformationController(OAuthClient oAuthClient, APIClient apiClient, OAuthSessions oAuthSessions) {
        this.oAuthClient = oAuthClient;
        this.apiClient = apiClient;
        this.oAuthSessions = oAuthSessions;
    }


    @RequestMapping("/userinfo")
    public UserInformationResponse getUserInformation(@RequestHeader("X-IG-OAUTH-TOKEN") String accessToken) throws Exception {
        return oAuthClient.getUserInformation(accessToken);
    }

    @RequestMapping("/accounts")
    public ClientAccountsResponse getClientAccounts(@RequestHeader("X-IG-OAUTH-TOKEN") String accessToken,
                                    @RequestParam("accountId") String accountId) throws Exception {
        log.info("Retrieving client id");
        UserInformationResponse userInfo = getUserInformation(accessToken);
        String clientId = userInfo.getSub();
        String apiKey = env.getProperty("ig.b2b.api.key");
        log.info("Retrieving client accounts for clientId={}", clientId);
        ClientAccountsResponse accounts = apiClient.getClientAccountDetails(apiKey, accessToken, accountId);
        if(StringUtils.isNotBlank(accountId)) {
            oAuthSessions.setAccountId(clientId, accountId);        // Keep track of the account id for batch processing
        }
        return accounts;
    }
}