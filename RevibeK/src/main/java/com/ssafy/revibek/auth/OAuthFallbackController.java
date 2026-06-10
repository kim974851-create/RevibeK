package com.ssafy.revibek.auth;

import com.ssafy.revibek.common.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OAuthFallbackController {

    @Value("${app.oauth.google.enabled:false}")
    private boolean googleOAuthEnabled;

    @GetMapping({"/oauth2/authorization/google", "/auth/google/callback"})
    public ResponseEntity<ErrorResponse> googleOAuthDisabled() {
        if (googleOAuthEnabled) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("OAUTH_FLOW_NOT_HANDLED", "OAuth flow should be handled by Spring Security.", null));
        }
        return ResponseEntity.ok(new ErrorResponse(
            "OAUTH_DISABLED",
            "Google OAuth is not configured in this environment. Please use local login.",
            null
        ));
    }
}
