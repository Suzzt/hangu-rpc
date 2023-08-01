package com.hanggu.common.channel.handler;

import com.hanggu.common.constant.HangguCons;
import com.hanggu.common.entity.ParameterInfo;
import com.hanggu.common.entity.PingPong;
import com.hanggu.common.entity.Request;
import com.hanggu.common.entity.Response;
import com.hanggu.common.entity.RpcRequestTransport;
import com.hanggu.common.entity.RpcResponseTransport;
import com.hanggu.common.enums.MsgTypeMarkEnum;
import com.hanggu.common.enums.SerializationTypeEnum;
import com.hanggu.common.serialization.SerialInput;
import com.hanggu.common.serialization.SerialOutput;
import com.hanggu.common.util.DescClassUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResponseMessageEncoder extends MessageToMessageCodec<ByteBuf, Response> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Response response, List<Object> out) throws Exception {
        ByteBuf byteBuf = ctx.alloc().buffer();
        // 魔数 2bytes
        byteBuf.writeShort(HangguCons.MAGIC);
        // 请求类型，序列化方式 1bytes
        byte finalMsgType = (byte) (MsgTypeMarkEnum.REQUEST_FLAG.getMark() & 0);
        byte serializationType = response.getSerializationType();
        finalMsgType |= serializationType;
        // 消息类型 1byte
        byteBuf.writeByte(finalMsgType);
        // 写入请求id
        byteBuf.writeLong(response.getId());
        // 写入内容
        RpcResponseTransport rpcResponseTransport = response.getRpcResponseTransport();
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            SerialOutput serialOutput = SerializationTypeEnum.getOutputByType(outputStream, serializationType);
            int code = rpcResponseTransport.getCode();
            serialOutput.writeInt(code);
            Class<?> aClass = rpcResponseTransport.getType();
            String typeDesc = DescClassUtils.getDesc(aClass);
            serialOutput.writeString(typeDesc);
            Object value = rpcResponseTransport.getVale();
            serialOutput.writeObject(value);
            byte[] contentBuff = outputStream.toByteArray();
            //内容对象长度 int 4bytes
            byteBuf.writeInt(contentBuff.length);
            //内容数据
            byteBuf.writeBytes(contentBuff);
        }
        out.add(byteBuf);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("请求编码失败！", cause);
        super.exceptionCaught(ctx, cause);
    }
}