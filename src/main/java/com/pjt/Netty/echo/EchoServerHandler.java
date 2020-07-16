package com.pjt.Netty.echo;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/* 수신된 데이터를 되돌려주는 핸들러 */
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

	/* 데이터 수신 이벤트처리 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		/* 네티에서 제공하는 바이트 버퍼 객체로부터 문자열 데이터를 읽는다. */
		String readMessage = ((ByteBuf) msg).toString(Charset.defaultCharset());
		System.out.println("수신한 문자열 [" + readMessage + "]");
		
		/* 채널 파이프라인에 대한 이벤트를 처리 */
		/* 여기서는 서버에 연결된 클라이언트 채널로 입력받은 데이터를 그대로 되돌려준다. */
		ctx.write(msg);
	}

	/* ChannelRead 이벤트 처리가 완료된 후 자동으로 수행되는 이벤트. */
	/* 채널 파이프라인에 저장된 버퍼를 전송하는 flush를 수행 */
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}
	
}
