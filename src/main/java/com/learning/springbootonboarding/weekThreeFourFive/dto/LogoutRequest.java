package com.learning.springbootonboarding.weekThreeFourFive.dto;


import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;

public record LogoutRequest(
        @NotBlank(message = "Refresh token is required")
        @JsonAlias("refresh_token")
        String refreshToken
) {
                @Override
                public String toString() {
                        return "LogoutRequest[refreshToken=***]";
                }
}

