package com.noix.spendtracker.bank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noix.spendtracker.bank.token.ApiTokenService;
import com.noix.spendtracker.bank.token.Bank;
import com.noix.spendtracker.security.jwt.JwtService;
import com.noix.spendtracker.user.User;
import com.noix.spendtracker.user.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureWebTestClient
class BankControllerTest {

    @Autowired
    WebTestClient webTestClient;


    String jwt;
    User user;
    static ObjectMapper mapper = new ObjectMapper();
    final String testKey = "TEST_API_KEY";

    @Autowired
    JwtService jwtService;
    @Autowired
    UserService userService;
    @Autowired
    ApiTokenService apiService;


    @BeforeEach
    void setUp() {
        user = userService.createUser("testuser", "1234");
        jwt = jwtService.createJwt(user);
    }

    @Test
    void monoClientData_shouldReturn400Http() throws Exception {
        apiService.save(user, testKey, Bank.MONO);

        webTestClient.post().uri("/api/bank/personal/client-info")
                .header("Authorization", "Bearer " + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(mapper.writeValueAsString(Bank.MONO))
                .exchange()
                .expectStatus().is4xxClientError();
    }
}