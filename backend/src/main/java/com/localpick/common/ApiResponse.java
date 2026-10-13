package com.localpick.common;

import lombok.Getter;

/** 모든 REST 응답을 같은 모양({success, message, data})으로 맞추기 위한 래퍼 */
@Getter
public class ApiResponse<T> {

    private final boolean success;
    private final String message;
    private final T data;

    private ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, "OK", data);
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    public static ApiResponse<Void> fail(String message) {
        return new ApiResponse<>(false, message, null);
    }
}
