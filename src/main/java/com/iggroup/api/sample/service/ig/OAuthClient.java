package com.iggroup.api.sample.service.ig;

import com.google.common.collect.ImmutableMap;
import com.iggroup.api.sample.controller.dto.AccessTokenResponse;
import com.iggroup.api.sample.controller.dto.UserInformationResponse;
import com.iggroup.api.sample.service.RESTClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Map;

@Component
public class OAuthClient {

    private static final Logger LOG = LoggerFactory.getLogger(OAuthClient.class);

    private final RESTClient restClient;
    private final String redirectBaseUrl;
    private final String oauthServerUrl;
    private final String clientId;
    private final String clientSecret;
    private final String realm;

    @Autowired
    public OAuthClient(RESTClient restClient,
                       @Qualifier(value = "redirect.base.url") String redirectBaseUrl,
                       @Value("${ig.oauth.server.url}") String oauthServerUrl,
                       @Value("${ig.oauth.client.id}") String clientId,
                       @Value("${ig.oauth.client.secret}") String clientSecret,
                       @Value("${ig.oauth.realm}") String realm) {
        this.restClient = restClient;
        this.redirectBaseUrl = redirectBaseUrl;
        this.oauthServerUrl = oauthServerUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.realm = realm;
    }

    /**
     * Requests an access token for the specified authorization code.
     *
     * @param authorizationCode authorization code
     * @return access token
     * @throws Exception
     */
    public AccessTokenResponse getAccessToken(String authorizationCode) throws Exception {
        LOG.info("Requesting access token for authorization code={} redirectBaseUrl={}", authorizationCode, redirectBaseUrl);
        String url = oauthServerUrl + "/oauth2/access_token";
        ImmutableMap<String, String> queryParameters = ImmutableMap.<String, String>builder()
                .put("grant_type", "authorization_code")
                .put("code", authorizationCode)
                .put("realm", realm)
                .put("redirect_uri", redirectBaseUrl + "/authorization-handler")
                .build();
        Map<String, String> headers = ImmutableMap.<String, String>builder()
                .put("Authorization", authorizationHeader(clientId, clientSecret))
                .put("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();
        ResponseEntity<AccessTokenResponse> response = restClient.execute(HttpMethod.POST, url, null, AccessTokenResponse.class, headers, queryParameters);
        if(response.getStatusCode() != HttpStatus.OK) {
            throw new Exception("Failed with status=" + response.getStatusCode());
        }
        return response.getBody();
    }

    /**
     * Requests an access token for the specified refresh token.
     *
     * @param refreshToken refresh token
     * @return access token
     * @throws Exception
     */
    public AccessTokenResponse refreshAccessToken(String refreshToken) throws Exception {
        LOG.debug("Requesting access token for refreshToken={}", refreshToken);
        String url = oauthServerUrl + "/oauth2/access_token";
        ImmutableMap<String, String> queryParameters = ImmutableMap.<String, String>builder()
                .put("grant_type", "refresh_token")
                .put("refresh_token", refreshToken)
                .put("realm", realm)
                .build();
        Map<String, String> headers = ImmutableMap.<String, String>builder()
                .put("Authorization", authorizationHeader(clientId, clientSecret))
                .put("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();
        ResponseEntity<AccessTokenResponse> response = restClient.execute(HttpMethod.POST, url, null, AccessTokenResponse.class, headers, queryParameters);
        if(response.getStatusCode() != HttpStatus.OK) {
            throw new Exception("Failed with status=" + response.getStatusCode());
        }
        return response.getBody();
    }

    /**
     * Requests user information with the specified access token.
     *
     * @param accessToken access token
     * @return user details
     * @throws Exception
     */
    public UserInformationResponse getUserInformation(String accessToken) throws Exception {
        LOG.info("Requesting user information for access token={}", accessToken);
        String url = oauthServerUrl + "/oauth2/userinfo";
        Map<String, String> headers = ImmutableMap.<String, String>builder()
                .put("Authorization", authorizationHeader(accessToken))
                .build();
        ResponseEntity<UserInformationResponse> response = restClient.execute(HttpMethod.GET, url, null, UserInformationResponse.class, headers, null);
        if(response.getStatusCode() != HttpStatus.OK) {
            throw new Exception("Failed with status=" + response.getStatusCode());
        }
        return response.getBody();
    }

    private String authorizationHeader(String clientId, String clientSecret) throws UnsupportedEncodingException {
        String credentials  = clientId + ':' + clientSecret;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes("UTF-8"));
        String header = "Basic " + encodedCredentials;
        LOG.debug("Authorization header={}", header);
        return header;
    }

    private String authorizationHeader(String accessToken) {
        String header = "Bearer " + accessToken;
        LOG.debug("Authorization header={}", header);
        return header;
    }
}
