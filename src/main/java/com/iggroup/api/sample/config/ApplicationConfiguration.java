package com.iggroup.api.sample.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
@Slf4j
public class ApplicationConfiguration {

   @Bean
   public RestTemplate restTemplate() {
      HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
      requestFactory.setHttpClient(HttpClients.createDefault());
      RestTemplate restTemplate = new RestTemplate();
      restTemplate.setRequestFactory(requestFactory);
      return restTemplate;
   }

   @Bean
   @Qualifier(value = "redirect.base.url")
   public String redirectUrl(Environment env) throws UnknownHostException {
      String redirectServerUrl = env.getProperty("redirect.server.url");
      String serverPort = env.getProperty("server.port");
      String contextPath = env.getProperty("server.context-path");
      String redirectBaseUrl =  (StringUtils.isNotBlank(redirectServerUrl) ? redirectServerUrl : "http://" + InetAddress.getLocalHost().getHostName() + ":" + serverPort) + contextPath;
      log.info("Server redirectBaseUrl={}", redirectBaseUrl);
      return redirectBaseUrl;
   }

}
