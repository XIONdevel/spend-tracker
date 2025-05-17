package com.noix.spendtracker.security.authentication;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authService;

    @PostMapping("/auth")
    public void authenticate(@RequestBody AuthenticationRequest request, HttpServletResponse response) {
        authService.authenticate(request, response);
    }

    @PostMapping("/register")
    public void register(@RequestBody AuthenticationRequest request, HttpServletResponse response) throws IOException {
        authService.register(request, response);
    }

    @GetMapping("/check")
    public ResponseEntity<String> check() {
        return ResponseEntity.ok("Success");
    }
}
