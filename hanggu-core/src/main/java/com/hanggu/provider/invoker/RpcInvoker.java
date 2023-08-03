package com.hanggu.provider.invoker;

import com.hanggu.common.entity.Request;
import com.hanggu.common.entity.Response;
import com.hanggu.common.entity.RpcInvokerContext;
import com.hanggu.common.enums.ErrorCodeEnum;
import com.hanggu.common.util.CommonUtils;
import io.netty.channel.ChannelHandlerContext;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Created by wuzhenhong on 2023/8/1 22:54
 */
public class RpcInvoker {

    private Object service;

    public void invoke(RpcInvokerContext rpcInvokerContext) {

        String methodName = rpcInvokerContext.getMethodName();
        List<Class<?>> parameterTypes = rpcInvokerContext.getParameterTypeList();
        List<Object> parameterValues = rpcInvokerContext.getParameterValueList();

        Class<?> clss = service.getClass();
        Class<?>[] parameterTypeArr = Optional.ofNullable(parameterTypes).orElse(Collections.emptyList())
            .toArray(new Class<?>[0]);
        Object[] parameterValuesArr = Optional.ofNullable(parameterValues).orElse(Collections.emptyList())
            .toArray(new Object[0]);

        Request request = rpcInvokerContext.getRequest();
        ChannelHandlerContext ctx = rpcInvokerContext.getCtx();

        Response response = null;
        try {
            Method method = clss.getMethod(methodName, parameterTypeArr);
            method.setAccessible(true);
            Object result = method.invoke(service, parameterValuesArr);
            response = CommonUtils.createResponseInfo(request.getId(), request.getSerializationType(),
                ErrorCodeEnum.SUCCESS.getCode(), method.getReturnType(), request);
        } catch (NoSuchMethodException e) {
            response = CommonUtils.createResponseInfo(request.getId(), request.getSerializationType(),
                ErrorCodeEnum.NOT_FOUND.getCode(), e.getClass(), e);
        } catch (InvocationTargetException e) {
            response = CommonUtils.createResponseInfo(request.getId(), request.getSerializationType(),
                ErrorCodeEnum.FAILURE.getCode(), e.getClass(), e);
        } catch (IllegalAccessException e) {
            response = CommonUtils.createResponseInfo(request.getId(), request.getSerializationType(),
                ErrorCodeEnum.FORBID.getCode(), e.getClass(), e);
        }

        ctx.writeAndFlush(response);
    }

    public Object getService() {
        return service;
    }

    public void setService(Object service) {
        this.service = service;
    }
}
