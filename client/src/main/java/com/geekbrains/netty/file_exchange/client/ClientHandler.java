package com.geekbrains.netty.file_exchange.client;

import com.geekbrains.netty.file_exchange.common.message.FileMessage;
import com.geekbrains.netty.file_exchange.common.message.Message;
import com.geekbrains.netty.file_exchange.common.message.TextMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.RandomAccessFile;

public class ClientHandler extends SimpleChannelInboundHandler<Message> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        if (msg instanceof TextMessage) {
            System.out.println("Received from server: " + ((TextMessage) msg).getText());
        }
        if (msg instanceof FileMessage) {
            FileMessage message = (FileMessage) msg;
            try (final RandomAccessFile randomAccessFile = new RandomAccessFile("1", "rw")) {
                randomAccessFile.write(message.getContent());
                System.out.println("Received file from server.");
            }
            ctx.close();
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Connected to server.");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
