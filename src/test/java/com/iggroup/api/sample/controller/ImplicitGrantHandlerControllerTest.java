package com.iggroup.api.sample.controller;

import com.iggroup.api.sample.controller.dto.UserInformationResponse;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MockServletContext.class)
@WebAppConfiguration
public class ImplicitGrantHandlerControllerTest {

    private MockMvc mvc;

    @Before
    public void setUp() throws Exception {
        UserInformationController userInformationController = mock(UserInformationController.class);
        UserInformationResponse userInformationResponse = new UserInformationResponse();
        userInformationResponse.setSub("myClientId");
        userInformationResponse.setFamily_name("Flinstone");
        when(userInformationController.getUserInformation(anyString())).thenReturn(userInformationResponse);
        mvc = MockMvcBuilders.standaloneSetup(new ImplicitGrantHandlerController(userInformationController, "/api-vendor-sample")).build();
    }

    @Test
    public void showResultSuccess() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/show-result?access_token=abc12345&expires=60&state=af0ifjsldkj").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(302));
    }

    @Test
    public void showResultMissingState() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/show-result?access_token=abc12345&expires=60").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}