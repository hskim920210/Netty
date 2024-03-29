package com.pjt.Netty.echo;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class EchoServerV3FirstHandler extends ChannelInboundHandlerAdapter {
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf readMessage = (ByteBuf) msg;
		System.out.println("channelRead : " + readMessage.toString(Charset.defaultCharset()));
		ctx.write(msg);
	}
}
