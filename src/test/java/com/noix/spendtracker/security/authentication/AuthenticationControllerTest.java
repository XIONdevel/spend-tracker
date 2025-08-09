package com.noix.spendtracker.security.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noix.spendtracker.security.jwt.JwtService;
import com.noix.spendtracker.user.User;
import com.noix.spendtracker.user.UserService;
import com.noix.spendtracker.user.role.Role;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@AutoConfigureMockMvc
class AuthenticationControllerTest {

    @Autowired
    MockMvc mockMvc;

    static String username;
    static String password;
    static ObjectMapper mapper;

    @BeforeAll
    static void beforeAll() {
        username = "testuser";
        password = "1234";
        mapper = new ObjectMapper();
    }


    @Test
    void register_user() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new AuthenticationRequest(username, password)))
                ).andExpect(status().isOk())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        assertThat(response.getHeader("Authorization")).isNotNull();
        assertThat(response.getCookie("token")).isNotNull();

        assertThat(response.getHeader("Authorization").length() > 7).isTrue(); //more than just a 'Bearer '
        assertThat(response.getCookie("token").getValue().isEmpty()).isFalse();
    }


    @Nested
    @Transactional
    class TestsDependsOnRegister {

        @Autowired
        JwtService jwtService;
        @Autowired
        UserService userService;

        String jwt;
        User user;

        @BeforeEach
        void setUp() {
            user = userService.createUser(username, password);
            jwt = jwtService.createJwt(user);
        }

        @Test
        void authenticate() throws Exception {
            MvcResult result = mockMvc.perform(post("/api/auth")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(new AuthenticationRequest(username, password)))
                    ).andExpect(status().isOk())
                    .andReturn();

            MockHttpServletResponse response = result.getResponse();
            assertThat(response.getHeader("Authorization")).isNotNull();
            assertThat(response.getCookie("token")).isNotNull();

            assertThat(response.getHeader("Authorization").length() > 7).isTrue(); //more than just a 'Bearer '
            assertThat(response.getCookie("token").getValue().isEmpty()).isFalse();
        }

        @Test
        void check() throws Exception {
            mockMvc.perform(get("/api/check")
                            .header("Authorization", "Bearer " + jwt)
                    ).andExpect(status().isOk())
                    .andReturn();
        }
    }
}