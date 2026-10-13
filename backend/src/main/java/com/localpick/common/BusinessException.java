package com.localpick.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {

    private final HttpStatus status;

    public BusinessException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public static BusinessException notFound(String what) {
        return new BusinessException(HttpStatus.NOT_FOUND, what + "을(를) 찾을 수 없습니다.");
    }

    public static BusinessException forbidden() {
        return new BusinessException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
    }

    public static BusinessException unauthorized() {
        return new BusinessException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
    }

    public static BusinessException badRequest(String message) {
        return new BusinessException(HttpStatus.BAD_REQUEST, message);
    }
}
