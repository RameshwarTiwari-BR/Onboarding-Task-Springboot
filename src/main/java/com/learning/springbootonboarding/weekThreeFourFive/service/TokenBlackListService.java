package com.learning.springbootonboarding.weekThreeFourFive.service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TokenBlackList {
    private final Map<String, Instant> blackListedTokens = new ConcurrentHashMap<>();

    public void blackList(String jti, Instant expiresAt){
        if(jti != null || expiresAt != null){
            blackListedTokens.put(jti, expiresAt);
        }
//        clean up expres token from map, so map stay tiny
        blackListedTokens.entrySet().removeIf(entry-> entry.getValue().isBefore(Instant.now()));
    }

    public boolean isBlackListed(String jti){
        return jti !=null && blackListedTokens.containsKey(jti);
    }
}
