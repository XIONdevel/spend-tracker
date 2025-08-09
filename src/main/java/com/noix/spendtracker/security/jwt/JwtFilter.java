package com.noix.spendtracker.security.jwt;

import com.noix.spendtracker.security.token.RefreshToken;
import com.noix.spendtracker.security.token.RefreshTokenService;
import com.noix.spendtracker.user.User;
import com.noix.spendtracker.user.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

//todo: add exception handling
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;
    private final RefreshTokenService refreshService;
    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class); //todo: add logging

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        String jwt = null;
        String username = null;

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = header.substring(7);
        if (jwtService.isExpired(jwt)) {
            RefreshToken refreshToken = refreshService.extractToken(request);
            if (refreshToken.isPresent()) {
                User user = refreshToken.getUser();
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                user.getAuthorities()
                        );
                auth.setDetails(new WebAuthenticationDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);

                jwt = jwtService.createJwt(refreshToken.getUser());
                response.addHeader("Authorization", "Bearer " + jwt);
            } else {
                response.getWriter().write("Tokens expired");
                filterChain.doFilter(request, response);
                return;
            }
        }

        username = jwtService.extractUsername(jwt);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            User user = userService.loadUserByUsername(username);
            if (jwtService.validateJwt(jwt, user)) {
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                user.getAuthorities()
                        );
                auth.setDetails(new WebAuthenticationDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        filterChain.doFilter(request, response);
    }
}