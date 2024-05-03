package com.rest.auth;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

class TokenCache {

    private static final Map<String, Instant> cache = new HashMap<String,Instant>();

    private static final int minutesToLive = 30;

    static boolean tokenInCache(String token) {
        Instant d = cache.get(token);
        if (d == null) {
            tokenToCache(token);
            return false;
        }
        Duration timeElapsed = Duration.between(d, Instant.now());
        if (timeElapsed.toMinutes() > minutesToLive) {
            tokenToCache(token);
            return false;
        }
        return true;
    }

    private static void tokenToCache(String token) {
        cache.put(token, Instant.now());
    }
}
