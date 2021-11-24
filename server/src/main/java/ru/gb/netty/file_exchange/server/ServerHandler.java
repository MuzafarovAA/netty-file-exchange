package ru.gb.netty.file_exchange.server;

import ru.gb.netty.file_exchange.common.message.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.Executor;

public class ServerHandler extends SimpleChannelInboundHandler<Message> {

    public static final int BUFFER_SIZE = 65536;
    private final Executor executor;

    public ServerHandler(Executor executor) {
        this.executor = executor;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        if (msg instanceof TextMessage) {
            TextMessage message = (TextMessage) msg;
            System.out.println("Received from client: " + message.getText());
            ctx.writeAndFlush(msg);
        }
        if (msg instanceof FileRequestMessage) {

            executor.execute(() -> {

                FileRequestMessage requestMessage = (FileRequestMessage) msg;
                try (RandomAccessFile randomAccessFile = new RandomAccessFile(requestMessage.getPath(), "r")) {
                    long fileLength = randomAccessFile.length();

                    do {
                        long position = randomAccessFile.getFilePointer();
                        long availableBytes = fileLength - position;
                        byte[] bytes;
                        if (availableBytes >= BUFFER_SIZE) {
                            bytes = new byte[BUFFER_SIZE];
                        } else {
                            bytes = new byte[(int) availableBytes];
                        }
                        randomAccessFile.read(bytes);
                        FileTransferMessage transferMessage = new FileTransferMessage();
                        transferMessage.setContent(bytes);
                        transferMessage.setStartPosition(position);
                        ctx.writeAndFlush(transferMessage).sync();
                        System.out.println("Sent file part to client.");
                    } while (randomAccessFile.getFilePointer() < fileLength);

                    ctx.writeAndFlush(new FileEndTransferMessage());
                    System.out.println("Sent file to client.");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
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
