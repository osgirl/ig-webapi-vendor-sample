package com.iggroup.api.sample.service;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class RESTClient {

    private static final Logger LOG = LoggerFactory.getLogger(RESTClient.class);

    private final RestTemplate restTemplate;

    @Autowired
    public RESTClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public <T> ResponseEntity<T> execute(HttpMethod method, String url, Object entity, Class<T> responseType, Map<String, String> headers, Map<String, String> queryParameters) throws Exception {
        MultiValueMap<String, String> mvHeaders = new LinkedMultiValueMap<>();
        mvHeaders.setAll(headers);
        if(queryParameters == null) {
            queryParameters = new HashMap<>();
        }
        String query = queryParameters.entrySet().stream()
                .map(p -> p.getKey() + "={" + p.getKey() + "}")
                .reduce((p1, p2) -> p1 + "&" + p2)
                .orElse("");
        if(StringUtils.isNotBlank(query)) {
            url += "?" + query;
        }
        Object[] uriVariables = queryParameters.values().stream().toArray();
        LOG.debug("Executing request method={} urlTemplate={} uriVariables={} headers={}", method.name(), url, queryParameters.values().stream().collect(Collectors.joining(", ")), headers.size());
        HttpEntity<Object> httpEntity = new HttpEntity<>(entity, mvHeaders);
        try {
            return restTemplate.exchange(url, method, httpEntity, responseType, uriVariables);
        } catch (HttpStatusCodeException e) {
            throw new Exception("Failed with status=" + e.getStatusCode() + " due to message=" + e.getResponseBodyAsString(), e);
        } catch (RestClientException e) {
            throw new Exception("Failed due to message=" + e.getMessage(), e);
        }
    }
}
