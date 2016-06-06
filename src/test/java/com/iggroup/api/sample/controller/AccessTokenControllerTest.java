package com.iggroup.api.sample.controller;

import com.iggroup.api.sample.controller.dto.AccessTokenResponse;
import com.iggroup.api.sample.service.ig.OAuthClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MockServletContext.class)
@WebAppConfiguration
public class AccessTokenControllerTest {

	private MockMvc mvc;

	@Before
	public void setUp() throws Exception {
		OAuthClient oAuthClient = mock(OAuthClient.class);
		AccessTokenResponse accessTokenResponse = new AccessTokenResponse();
		accessTokenResponse.setAccess_token("MyAccessToken");
		when(oAuthClient.getAccessToken(anyString())).thenReturn(accessTokenResponse);
		mvc = MockMvcBuilders.standaloneSetup(new AccessTokenController(oAuthClient)).build();
	}

	@Test
	public void getAccessToken() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/access-token?code=abc12345").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string("{\"access_token\":\"MyAccessToken\",\"refresh_token\":null,\"scope\":null,\"id_token\":null,\"token_type\":null,\"expires_in\":null}"));
	}
}
