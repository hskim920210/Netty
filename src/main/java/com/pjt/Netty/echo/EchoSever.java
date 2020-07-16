package com.pjt.Netty.echo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class EchoSever {
	public static void main(String[] args) {
		/* 생성자의 인수는 쓰레드 그룹 내에서 생성활 최대 쓰레드 수를 의미한다. */
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		/* 인수가 없으면 서버 애플리케이션이 동작하는 하드웨어 코어 수를 기준으로 결정한다. 쓰레드 수 = 하드웨어 CPU 코어수 * 2 */
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		
		try {
			/* 빌더패턴 */
			ServerBootstrap b = new ServerBootstrap();
			/* 서버 애플리케이션이 사용할 두 쓰레드 그룹을 설정했다. */
			/* 첫번째 쓰레드 그룹은 클라이언터의 연결을 수락하는 부모 쓰레드 그룹 */
			/* 두번째 쓰레드 그룹은 연결된 클라이언트의 소켓으로부터 데이터 입출력 및 이벤트 처리를 담당하는 자식 쓰레드 그룹 */
			b.group(bossGroup, workerGroup)
				/* 서버 소켓(부모 쓰레드)이 사용할 네트워크 입출력 모드를 설정한다. */
				.channel(NioServerSocketChannel.class)
				/* 자식 채널의 초기화 방법을 설정한다. */
				/* ChannelInitailizer는 클라이언트로부터 연결된 채널이 초기화될 때의 기본 동작이 지정된 추상클래스 */
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						/* 채널 파이프라인 객체 생성 */
						ChannelPipeline p = ch.pipeline();
						/* 접속된 클라이언트로부터 수신된 데이터를 처리할 핸들러 지정 */
						p.addLast(new EchoServerHandler());
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
