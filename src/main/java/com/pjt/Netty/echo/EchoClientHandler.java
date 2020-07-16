package com.pjt.Netty.echo;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class EchoClientHandler extends ChannelInboundHandlerAdapter {

	/* 소켓 채널이 최초 활성화 되었을 때 실행된다. */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		String sendMessage = "Hello, Netty!";
		
		ByteBuf messageBuffer = Unpooled.buffer();
		messageBuffer.writeBytes(sendMessage.getBytes());
		
		StringBuilder builder = new StringBuilder();
		builder.append("전송할 문자열 [");
		builder.append(sendMessage);
		builder.append("]");
		
		System.out.println(builder.toString());
		
		/* writeAndFlush는 내부적으로 wirte, flush 두 메소드를 호출하여 데이터 기록과 전송을 한다. */
		ctx.writeAndFlush(messageBuffer);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		String readMessage = ((ByteBuf) msg).toString(Charset.defaultCharset());
		
		StringBuilder builder = new StringBuilder();
		builder.append("수신한 문자열 [");
		builder.append(readMessage);
		builder.append("]");
		
		System.out.println(builder.toString());
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		/* 수신된 데이터를 모두 읽은 후 서버와 연결된 채널을 닫는다. 이후 데이터 송수신 채널은 닫히고 클라이언트는 종료된다. */
		ctx.close();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
}
