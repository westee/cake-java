package com.westee.cake.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.westee.cake.controller.AuthController;
import com.westee.cake.entity.LoginResponse;
import com.westee.cake.entity.LoginResult;
import com.westee.cake.entity.TelAndPassword;
import com.westee.cake.entity.UsernameAndPassword;
import com.westee.cake.entity.WeChatSession;
import com.westee.cake.generate.User;
import jakarta.inject.Inject;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.ClassicConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.startsWith;

public class AbstractIntegrationTest {

    @Inject
    private JdbcTemplate jdbcTemplate;
    @Inject
    Environment environment;

    @InjectMocks
    UserService userService;

    MockMvc mockMvc;

    @Inject
    private WebApplicationContext context;

    private final RestTemplate restTemplate = new RestTemplate();

    public static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Value("${spring.datasource.url}")
    private String databaseUrl;
    @Value("${spring.datasource.username}")
    private String databaseUsername;
    @Value("${spring.datasource.password}")
    private String databasePassword;

    private final String TEL = "15611111111";
    private final String PASSWORD = "123456";
    private final String INVALID_PASSWORD = "admin";

    TelAndPassword VALID_PARAMS = new TelAndPassword(TEL, PASSWORD);
    TelAndPassword INVALID_PARAMS = new TelAndPassword(TEL, INVALID_PASSWORD);

    @BeforeEach
    public void initDataBase() {
        ClassicConfiguration conf = new ClassicConfiguration();
        conf.setDataSource(databaseUrl, databaseUsername, databasePassword);
        conf.setCleanDisabled(false);
        Flyway flyway = new Flyway(conf);
        flyway.clean();
        flyway.migrate();
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
//        String sql = "INSERT INTO cake.USER (ID, NAME, TEL, AVATAR_URL, ADDRESS, ROLE_ID, WX_OPEN_ID, WX_SESSION_KEY, " +
//                "BIRTHDAY, SEX, PASSWORD, NICKNAME, BALANCE, CREATED_AT, UPDATED_AT) VALUES" +
//                " (1, null, '15612345678', '1699282157470_p20.jpg', null, 2, " +
//                "'x', 'y', '2023-10-06 08:00:00', 0, null, '1122', " +
//                "null, '2023-11-06 17:33:08', '2023-11-06 17:33:08');";
//
//        jdbcTemplate.update(sql);
    }


    public String getUrl(String apiName) {
        // 获取集成测试的端口号
        return "http://localhost:" + environment.getProperty("local.server.port") + apiName;
    }


    public UserLoginResponse loginAndGetToken() throws JsonProcessingException {

        ResponseEntity<String> stringResponseEntity = doHttpRequest("/api/v1/register-password", HttpMethod.POST, null,
                objectMapper.writeValueAsString(generateUsernameAndPassword()));
        int responseCode = stringResponseEntity.getStatusCode().value();
        Assertions.assertEquals(HttpStatus.OK.value(), responseCode);

//        // 微信登录
//        ResponseEntity<String> stringResponseEntity = doHttpRequest("/api/v1/send-wxcode", HttpMethod.GET, null,
//                objectMapper.writeValueAsString(generateRegister()));

        doHttpRequest("/api/v1/status", HttpMethod.GET, null, null).getBody();

        return  null;//new UserLoginResponse(null, statusResponseData.getUser(), loginResult.getToken());
    }

    // 获取cookie
    private String getSessionIdFromSetCookie(String session) {
        int semiColonIndex = session.indexOf(";");
        return session.substring(0, semiColonIndex);
    }

    public static class UserLoginResponse {
        String cookie;
        User user;
        String token;

        public UserLoginResponse() {
        }

        public UserLoginResponse(String cookie, User user, String token) {
            this.cookie = cookie;
            this.user = user;
            this.token = token;
        }

        public String getCookie() {
            return cookie;
        }

        public void setCookie(String cookie) {
            this.cookie = cookie;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

    public static class HttpResponse {
        int code;
        String body;
        Map<String, List<String>> headers;

        public HttpResponse(int code, String body, Map<String, List<String>> headers) {
            this.code = code;
            this.body = body;
            this.headers = headers;
        }

        HttpResponse assertOkStatusCode() {
            Assertions.assertTrue(code >= 200 && code < 300, "" + code + ": " + body);
            return this;
        }

        public <T> T asJsonObject(TypeReference<T> data) throws JsonProcessingException {
            return objectMapper.readValue(body, data);
        }
    }

    public ResponseEntity<String> doHttpRequest(String apiName, HttpMethod method, MultiValueMap<String, String> headers, Object body) {
        HttpHeaders httpHeaders = new HttpHeaders();
        if (headers != null) {
            httpHeaders.addAll(headers);
        }
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> requestEntity = new HttpEntity<>(body, httpHeaders);
        ResponseEntity<String> exchange = null;

        exchange = restTemplate.exchange(getUrl(apiName), method, requestEntity, String.class);
        return exchange;
    }


    public HashMap<Object, Object> generateRegister() {
        HashMap<Object, Object> map = new HashMap<>();
        map.put("wxcode", "this_is_wxcode");
        map.put("avatar", "my_avatar");
        map.put("name", "张三");
        return map;
    }

    public UsernameAndPassword generateUsernameAndPassword() {
        return new UsernameAndPassword("username", "password");
    }

    public WeChatSession generateWeChatSession() {
        WeChatSession weChatSession = new WeChatSession();
        weChatSession.setSession_key("session_key");
        weChatSession.setOpenid("openid");
        weChatSession.setUnionid("unionid");
        return weChatSession;
    }
}
