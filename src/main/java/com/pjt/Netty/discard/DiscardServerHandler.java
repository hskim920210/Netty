package com.pjt.Netty.discard;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/* 데이터가 수신되었을 때 자동으로 수행되는 Discard 서버의 데이터 처리 핸들러 */
public class DiscardServerHandler extends SimpleChannelInboundHandler<Object> {

	/* Netty 5.0 이후부터 messageReceived 메소드로 바뀔 예정 */
	/* 클라이언트가 데이터를 전송하면 이 메소드가 자동으로 호출된다. */
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		/* 아무것도 하지 않는다. */
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
	
}
