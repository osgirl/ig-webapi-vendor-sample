package com.iggroup.api.sample.service;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class OAuthSessions {

    // Map of client IDs to refresh tokens
    private Map<String, String> refreshTokens = Collections.synchronizedMap(new HashMap<>());

    // Map of client IDs to accountIds (account IDs are added when a user request account data)
    private Map<String, String> accountIDs = Collections.synchronizedMap(new HashMap<>());

    public Set<String> getAuthorizedUsers() {
        return refreshTokens.keySet();
    }

    public String getRefreshToken(String clientId) {
        return refreshTokens.get(clientId);
    }

    public void setRefreshToken(String clientId, String refreshToken) {
        refreshTokens.put(clientId, refreshToken);
    }

    public String getAccountId(String clientId) {
        return accountIDs.get(clientId);
    }

    public void setAccountId(String clientId, String accountId) {
        accountIDs.put(clientId, accountId);
    }
}
