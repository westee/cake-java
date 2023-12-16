package com.westee.cake.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.westee.cake.CakeApplication;
import com.westee.cake.entity.LoginResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = CakeApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"spring.config.location=classpath:test-application.properties"})
public class AuthIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void passwordLoginTest() throws JsonProcessingException {
        UserLoginResponse userLoginResponse = loginAndGetToken();
        assertNotNull(userLoginResponse.getCookie());
        assertNotNull(userLoginResponse.getUser());
        assertNotNull(userLoginResponse.getToken());
    }

    @Test
    public void logoutTest() throws JsonProcessingException {
        UserLoginResponse userLoginResponse = loginAndGetToken();
        String cookie = userLoginResponse.getCookie();
        // 根据cookie生成headers
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("cookie", cookie);

        ResponseEntity<String> logoutResponseEntity = doHttpRequest("/api/v1/logout", HttpMethod.GET, headers, null);
        int responseCode = logoutResponseEntity.getStatusCode().value();
        assertEquals(HttpStatus.OK.value(), responseCode);

        String body = doHttpRequest("/api/v1/status", HttpMethod.GET, null, null).getBody();
        LoginResponse loginResponse = objectMapper.readValue(body, LoginResponse.class);
        assertFalse(loginResponse.isLogin());
    }

}
