package com.hanggu.consumer.annotation;

import com.hanggu.consumer.callback.RpcResponseCallback;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 异步调用注解
 * @author wuzhenhong
 * @date 2023/8/3 16:05
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface HanguMethod {

    /**
     * 超时时间，默认5s
     */
    int timeout() default 5;

    Class<RpcResponseCallback> callback();
}
