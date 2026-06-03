package br.com.dgdev.sulwork.cafe.exception;

import java.util.List;

public record ApiErrorResponse(
    String message,
    int status,
    List<String> errors
) {
    public static ApiErrorResponse of(String message, int status) {
        return new ApiErrorResponse(message, status, List.of());
    }

    public static ApiErrorResponse of(String message, int status, List<String> errors) {
        return new ApiErrorResponse(message, status, errors);
    }
}
