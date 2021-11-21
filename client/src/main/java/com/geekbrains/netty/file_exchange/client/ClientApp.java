package com.geekbrains.netty.file_exchange.client;

import com.geekbrains.netty.file_exchange.common.handler.JsonDecoder;
import com.geekbrains.netty.file_exchange.common.handler.JsonEncoder;
import com.geekbrains.netty.file_exchange.common.message.FileRequestMessage;
import com.geekbrains.netty.file_exchange.common.message.TextMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import java.util.Scanner;

public class ClientApp {

    public static final String HOST = "localhost";
    public static final int PORT = 9000;

    public static void main(String[] args) {

        new ClientApp().start();

    }

    private void start() {


        EventLoopGroup workerGroup = new NioEventLoopGroup(1);

        try {

            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                            nioSocketChannel.pipeline().addLast(
                                    new LengthFieldBasedFrameDecoder(1024,0,2,0,2),
                                    new LengthFieldPrepender(2),
                                    new JsonDecoder(),
                                    new JsonEncoder(),
                                    new ClientHandler()
                            );
                        }
                    })
                    .option(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture channel = bootstrap.connect(HOST, PORT).sync();

                final FileRequestMessage message = new FileRequestMessage();
                message.setPath("testToSend.txt");

//                Scanner scanner = new Scanner(System.in);
//                TextMessage message = new TextMessage();
//                message.setText(scanner.next());

                channel.channel().writeAndFlush(message);
                channel.channel().closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }


    }

}
