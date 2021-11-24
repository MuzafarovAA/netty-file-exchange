package ru.gb.netty.file_exchange.client;

import ru.gb.netty.file_exchange.common.message.FileEndTransferMessage;
import ru.gb.netty.file_exchange.common.message.FileTransferMessage;
import ru.gb.netty.file_exchange.common.message.Message;
import ru.gb.netty.file_exchange.common.message.TextMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.RandomAccessFile;

public class ClientHandler extends SimpleChannelInboundHandler<Message> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        if (msg instanceof TextMessage) {
            System.out.println("Received from server: " + ((TextMessage) msg).getText());
        }
        if (msg instanceof FileTransferMessage) {
            FileTransferMessage message = (FileTransferMessage) msg;
            try (RandomAccessFile randomAccessFile = new RandomAccessFile("1.txt", "rw")) {
                randomAccessFile.seek(message.getStartPosition());
                randomAccessFile.write(message.getContent());
                System.out.println("Received file part from server.");
            }

        }
        if (msg instanceof FileEndTransferMessage) {
            System.out.println("Received file from server.");
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
