package com.westee.cake.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.westee.cake.CakeApplication;
import com.westee.cake.controller.AuthController;
import com.westee.cake.entity.LoginResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = CakeApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"spring.config.location=classpath:test-application.properties"})
public class AuthIntegrationTest extends AbstractIntegrationTest {
    private MockMvc mockMvc;
    private MockRestServiceServer mockServer;

    private final RestTemplate restTemplate = new RestTemplate();

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // 创建MockMVC和MockRestServiceServer
//        mockMvc = MockMvcBuilders.standaloneSetup(yourControllerInstance).build();
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }
    @Test
    public void wxcodeLoginTest() throws JsonProcessingException {
        UserLoginResponse userLoginResponse = loginAndGetToken();
        assertNotNull(userLoginResponse.getCookie());
        assertNotNull(userLoginResponse.getUser());
        assertNotNull(userLoginResponse.getToken());
    }

    @Test
    public void testSendWXCode() throws Exception {
        AuthController mock = Mockito.mock(AuthController.class);
        mockServer.expect(requestTo("https://api.weixin.qq.com/sns/jscode2session?appid=" +
                        "yourAppID" + "&secret=" + "yourSecret" +
                        "&js_code=" + "testWXCode" + "&grant_type=authorization_code"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withSuccess("{ \\\"openid\\\": \\\"testOpenID\\\", \\\"session_key\\\": \\\"testSessionKey\\\", \\\"unionid\\\": \\\"testUnionID\\\" }", MediaType.APPLICATION_JSON));

        LoginResult loginResult = mock.sendWXCode(Mockito.anyMap());
    }

}
