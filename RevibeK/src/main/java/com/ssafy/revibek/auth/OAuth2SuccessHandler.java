package com.ssafy.revibek.auth;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.revibek.auth.dto.AuthTokenResponseDto;
import com.ssafy.revibek.user.service.AuthService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthService authService;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = (String) oAuth2User.getAttributes().get("email");
        String providerId = (String) oAuth2User.getAttributes().get("sub");
        String nickname = (String) oAuth2User.getAttributes().getOrDefault("name", "google-user");

        if (email == null || email.isBlank() || providerId == null || providerId.isBlank()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json;charset=UTF-8");
            objectMapper.writeValue(response.getWriter(),
                java.util.Map.of(
                    "code", "OAUTH_PROFILE_MISSING",
                    "message", "Google OAuth profile does not contain required email or subject."
                )
            );
            clearAuthenticationAttributes(request);
            return;
        }

        AuthTokenResponseDto tokenResponse = authService.loginWithGoogle(email, providerId, nickname);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        objectMapper.writeValue(response.getWriter(), tokenResponse);
        clearAuthenticationAttributes(request);
    }
}
