package com.learning.springbootonboarding.weekThreeFourFive.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlackListService {
//    This is good for small app or few users, so for actual prod use Redis for this with same
//    Redis can handle if app running behind loadBlancer with multiple instance(Like user req logout hit instance 1&token details get saved at instance 1 then next req hit instance 2 can process req)
//    also this will solve ram memory and searching prooblem if we use redis
    private final Map<String, Instant> blackListedTokens = new ConcurrentHashMap<>();

    public void blackList(String jti, Instant expiresAt){
        if(jti != null && expiresAt != null){
            blackListedTokens.put(jti, expiresAt);
        }
//        clean up expires token from map, so map stay tiny
        blackListedTokens.entrySet().removeIf(entry-> entry.getValue().isBefore(Instant.now()));
    }

    public boolean isBlackListed(String jti){
        return jti !=null && blackListedTokens.containsKey(jti);
    }
}
