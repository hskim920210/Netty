package com.pjt.Netty.echo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.socket.SocketChannel;

public class EpollEchoServer {
	public static void main(String[] args) {
		EventLoopGroup bossGroup = new EpollEventLoopGroup(1);		/* 1 */
		EventLoopGroup workerGroup = new EpollEventLoopGroup();		/* 2 */
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
				/* Old-Blocking-IO */
				.channel(EpollServerSocketChannel.class)			/* 3 */
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ChannelPipeline p = ch.pipeline();
						p.addLast(new EchoServerHandler());
						
					}
				});
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}
}
