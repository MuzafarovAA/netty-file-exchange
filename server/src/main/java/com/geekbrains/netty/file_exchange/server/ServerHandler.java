package com.geekbrains.netty.file_exchange.server;

import com.geekbrains.netty.file_exchange.common.message.FileMessage;
import com.geekbrains.netty.file_exchange.common.message.FileRequestMessage;
import com.geekbrains.netty.file_exchange.common.message.Message;
import com.geekbrains.netty.file_exchange.common.message.TextMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.RandomAccessFile;

public class ServerHandler extends SimpleChannelInboundHandler<Message> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        if (msg instanceof TextMessage) {
            TextMessage message = (TextMessage) msg;
            System.out.println("Received from client: " + message.getText());
            ctx.writeAndFlush(msg);
        }
        if (msg instanceof FileRequestMessage) {
            FileRequestMessage message = (FileRequestMessage) msg;
            try (RandomAccessFile accessFile = new RandomAccessFile(message.getPath(), "r")) {
                final FileMessage fileMessage = new FileMessage();
                byte[] content = new byte[(int) accessFile.length()];
                accessFile.read(content);
                fileMessage.setContent(content);
                ctx.writeAndFlush(fileMessage);
            }

        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client connected.");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client disconnected.");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
