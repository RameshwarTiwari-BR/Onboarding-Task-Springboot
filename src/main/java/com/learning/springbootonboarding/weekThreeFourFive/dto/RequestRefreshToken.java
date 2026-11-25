package com.learning.springbootonboarding.weekThreeFourFive.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RequestRefreshToken (
        @JsonProperty("refresh_token") String refreshToken
)
{
    @Override
    public String toString() {
        return "LogoutRequest[refreshToken=***]";
    }
}
