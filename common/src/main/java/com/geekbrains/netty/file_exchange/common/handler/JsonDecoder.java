package com.geekbrains.netty.file_exchange.common.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geekbrains.netty.file_exchange.common.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class JsonDecoder extends MessageToMessageDecoder<ByteBuf> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        byte[] bytes = ByteBufUtil.getBytes(msg);
        System.out.println("Received String: " + new String(bytes));
        Message message = OBJECT_MAPPER.readValue(bytes, Message.class);
        out.add(message);
    }
}
