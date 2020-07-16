package com.pjt.Netty.echo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class EchoServerV4 {
	public static void main(String[] args) {
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				/* 커널 버퍼에 남은 데이터를 상대방 소켓 채널로 모두 전송하고 즉시 연결을 끊는다. */
				/* 이 방법은 TIME_WAIT이 발생하지 않는 장점이 있지만 */
				/* 반대로 마지막으로 전송한 데이터가 클라이언트로 모두 전송되었는지 확인할 방법이 없다. */
				/* 또한 블로킹 모드에서 타임아웃을 1초로 지정하면 ACK 패킷이 도착하지 않았을 때, 1초간 블로킹이 된다. */
				.childOption(ChannelOption.SO_LINGER, 0)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ChannelPipeline p = ch.pipeline();
//						p.addLast(new EchoServerHandler());
						p.addLast(new EchoServerV4FirstHandler());
						p.addLast(new EchoServerV4SecondHandler());
					}
				});
			
			/* bind를 통해 접속할 포트를 지정한다. */
			ChannelFuture f = b.bind(8888).sync();
			
			f.channel().closeFuture().sync();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}
}
