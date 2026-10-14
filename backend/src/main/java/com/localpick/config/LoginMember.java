package com.localpick.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 컨트롤러 파라미터에 붙이면 세션의 로그인 회원 id 를 넣어준다.
 * required = true 인데 로그인이 안 되어 있으면 401 을 응답한다.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginMember {
    boolean required() default true;
}
