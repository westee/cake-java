package com.westee.cake;

import com.westee.cake.controller.AuthController;
import com.westee.cake.service.AuthService;
import com.westee.cake.service.CheckTelService;
import com.westee.cake.service.MockSmsCodeService;
import com.westee.cake.service.UserService;
import com.westee.cake.service.VerificationCodeCheckService;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.SubjectThreadState;
import org.apache.shiro.util.ThreadState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class AuthControllerTest {
    private MockMvc mvc;

    @Mock
    private UserService userService;
    @Mock
    private CheckTelService checkTelService;
    @Mock
    private MockSmsCodeService mockSmsCodeService;
    private VerificationCodeCheckService verificationCodeCheckService;


    @BeforeEach
    void setUp() {
        // fixed org.apache.shiro.UnavailableSecurityManagerException: No SecurityManager accessible to the calling code
        Subject mockSubject = Mockito.mock(Subject.class);
        ThreadState threadState = new SubjectThreadState(mockSubject);
        threadState.bind();

        AuthService authService = new AuthService(userService, mockSmsCodeService, verificationCodeCheckService);
        mvc = MockMvcBuilders.standaloneSetup(new AuthController(authService, checkTelService, userService)).build();
    }

    @Test
    void returnNotLoginByDefault() throws Exception {
        mvc.perform(get("/api/v1/status")).andExpect(status().isOk())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("{\"login\":false,\"user\":null}")));
    }

}
