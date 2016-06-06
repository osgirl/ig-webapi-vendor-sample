package com.iggroup.api.sample.controller;

import com.iggroup.api.sample.controller.dto.AccessTokenResponse;
import com.iggroup.api.sample.service.ig.OAuthClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccessTokenController {

    private final OAuthClient oAuthClient;

    @Autowired
    public AccessTokenController(OAuthClient oAuthClient) {
        this.oAuthClient = oAuthClient;
    }

    @RequestMapping("/access-token")
    public AccessTokenResponse getAccessToken(@RequestParam("code") String authorizationCode) throws Exception {
        return oAuthClient.getAccessToken(authorizationCode);
    }

    @RequestMapping("/refresh/access-token")
    public AccessTokenResponse refreshAccessToken(@RequestParam("refreshToken") String refreshToken) throws Exception {
        return oAuthClient.refreshAccessToken(refreshToken);
    }
}