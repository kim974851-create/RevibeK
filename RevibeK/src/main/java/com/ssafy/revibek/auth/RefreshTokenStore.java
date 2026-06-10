package com.ssafy.revibek.auth;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenStore {

    private final Map<String, String> userRefreshTokenMap = new ConcurrentHashMap<>();

    @Value("${app.refresh-token.store:memory}")
    private String storeType;

    public void save(String userId, String refreshToken) {
        if (userId == null || userId.isBlank() || refreshToken == null || refreshToken.isBlank()) {
            return;
        }
        userRefreshTokenMap.put(userId, refreshToken);
    }

    public boolean isValid(String userId, String refreshToken) {
        if (userId == null || refreshToken == null) {
            return false;
        }
        String storedToken = userRefreshTokenMap.get(userId);
        return storedToken != null && storedToken.equals(refreshToken);
    }

    public void revoke(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return;
        }
        userRefreshTokenMap.entrySet()
            .removeIf(entry -> entry.getValue().equals(refreshToken));
    }

    public void revokeByUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            return;
        }
        userRefreshTokenMap.remove(userId);
    }

    public String getStoreType() {
        return storeType;
    }
}
