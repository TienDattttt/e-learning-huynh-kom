package com.microshop.elearningbackend.common;

public record ApiResponse<T>(boolean success, String message, T data) {
    public ApiResponse(T data) { this(true, "OK", data); }
    public static <T> ApiResponse<T> ok(T data) { return new ApiResponse<>(true, "OK", data); }

    // ➕ Thêm overload này
    public static <T> ApiResponse<T> ok() { return new ApiResponse<>(true, "OK", null); }

    public static <T> ApiResponse<T> fail(String message) { return new ApiResponse<>(false, message, null); }
}
