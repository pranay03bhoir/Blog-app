package com.substring.utils;

public record APIResponse(
        String message,
        int statusCode,
        String status
) {

    public static APIResponse response(String message, int statusCode, String status) {
        return new APIResponse(message, statusCode, status);
    }
}
