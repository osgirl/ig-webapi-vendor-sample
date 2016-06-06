package com.iggroup.api.sample.service.ig;

import com.google.common.collect.ImmutableMap;
import com.iggroup.api.sample.controller.dto.ClientAccountsResponse;
import com.iggroup.api.sample.service.RESTClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class APIClient {

    private static final Logger LOG = LoggerFactory.getLogger(APIClient.class);

    private final RESTClient restClient;
    private final String igApiServerUrl;

    @Autowired
    public APIClient(RESTClient restClient,
                     @Value("${ig.api.server.url}") String igApiServerUrl) {
        this.restClient = restClient;
        this.igApiServerUrl = igApiServerUrl;
    }

    public ClientAccountsResponse getClientAccountDetails(String apiKey, String accessToken, String accountId) throws Exception {
        LOG.debug("Requesting client account details for access apiKey={} accessToken={} accountId={}", apiKey, accessToken, accountId);
        String url = igApiServerUrl + "/accounts";
        Map<String, String> headers = ImmutableMap.<String, String>builder()
                .put("VERSION", "1")
                .put("X-IG-API-KEY", apiKey)
                .put("X-IG-OAUTH-TOKEN", accessToken)
                .put("IG-ACCOUNT-ID", accountId)
                .build();
        ResponseEntity<ClientAccountsResponse> response = restClient.execute(HttpMethod.GET, url, null, ClientAccountsResponse.class, headers, null);
        if(response.getStatusCode() != HttpStatus.OK) {
            throw new Exception("Failed with status=" + response.getStatusCode());
        }
        return response.getBody();
    }
}
