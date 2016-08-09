package com.iggroup.api.sample;

import com.iggroup.api.sample.controller.AccessTokenController;
import com.iggroup.api.sample.controller.dto.AccessTokenResponse;
import com.iggroup.api.sample.controller.dto.ClientAccount;
import com.iggroup.api.sample.controller.dto.ClientAccountsResponse;
import com.iggroup.api.sample.service.OAuthSessions;
import com.iggroup.api.sample.service.ig.APIClient;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.util.Set;
import java.util.stream.Collectors;


@SpringBootApplication
public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    private OAuthSessions oAuthSessions;
    private AccessTokenController accessTokenController;
    private APIClient apiClient;

    @Autowired
    Environment env;

    @Autowired
    public void Application(OAuthSessions oAuthSessions,
                            AccessTokenController accessTokenController,
                            APIClient apiClient) {
        this.accessTokenController = accessTokenController;
        this.oAuthSessions = oAuthSessions;
        this.apiClient = apiClient;
    }

    public static void main(String args[]) {
        // Start and wait for the application to be up and running
        ApplicationContext ctx = SpringApplication.run(Application.class, args);

        // Run a batch job for authorized users
        Application app = (Application) ctx.getBean("application");
        try {
            app.run();
        } catch (Exception e) {
            LOG.error("ERROR: {}", e.getMessage(), e);
        }
    }

    private void run() throws InterruptedException {
        while(true) {
            LOG.info("Next batch job will run in 80 seconds");
            Thread.sleep(80000L);
            offlineBatchJob();
        }
    }

    private void offlineBatchJob() {
        Set<String> clientIdList = oAuthSessions.getAuthorizedUsers();
        LOG.info("Processing {} users.", clientIdList.size());
        clientIdList.parallelStream()
                .filter(clientId -> StringUtils.isNotBlank(oAuthSessions.getRefreshToken(clientId)))
                .forEach(clientId -> {
                    try {
                        processUser(clientId);
                    } catch (Exception e) {
                        LOG.error("Batch job for clientId={} failed with message={}", clientId, e.getMessage(), e);
                    }
                });
    }

    private void processUser(String clientId) throws Exception {
        String refreshToken = oAuthSessions.getRefreshToken(clientId);
        LOG.debug("Running batch job for clientId={} refreshToken={}", clientId, refreshToken);

        // Refresh access token
        AccessTokenResponse accessTokenResponse = accessTokenController.refreshAccessToken(refreshToken);
        refreshToken = accessTokenResponse.getRefresh_token();
        String accessToken = accessTokenResponse.getAccess_token();

        LOG.debug("Storing new refreshToken={} for clientId={}", refreshToken, clientId);
        oAuthSessions.setRefreshToken(clientId, refreshToken);

        LOG.debug("Processing user with clientId={}", clientId);
        String apiKey = env.getProperty("ig.b2b.api.key");
        ClientAccountsResponse accounts = apiClient.getClientAccountDetails(apiKey, accessToken);
        LOG.info("Accounts for clientId={}:  {}", clientId, accounts.getAccounts().stream().map(ClientAccount::getAccountId).collect(Collectors.joining(",")));
    }
}
