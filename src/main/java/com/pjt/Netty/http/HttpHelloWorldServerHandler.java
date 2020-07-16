package com.pjt.Netty.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.AsciiString;

/* HttpServerCodec으로부터 수신된 channelRead 이벤트 처리를 위해 ChannelInboundHandlerAdapter 상속 */
public class HttpHelloWorldServerHandler extends ChannelInboundHandlerAdapter {
	/* 웹브라우저로 전송할 Hello World 메세지 */
	private static final byte[] CONTENT = {'H', 'e', 'l', 'l', 'o', ' ', 'W', 'o', 'r', 'l', 'd'};
	private static final AsciiString CONTENT_TYPE = new AsciiString("Content-Type");
	private static final AsciiString CONTENT_LENGTH = new AsciiString("Content-Length");
	private static final AsciiString CONNECTION = new AsciiString("Connection");
	private static final AsciiString KEEP_ALIVE = new AsciiString("keep-alive");
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		/* 웹브라우저로부터 데이터가 모두 수신되었을 때 채널 버퍼의 내용을 웹브라우저로 전송 */
		ctx.flush();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		/* 수신 객체가 HttpRequest인 경우 HttpResponse 객체를 생성하고 헤더와 Hello World 메세지를 저장 */
		if(msg instanceof HttpRequest) {
			HttpRequest req = (HttpRequest) msg;
			
			if(HttpUtil.is100ContinueExpected(req)) {
				ctx.write(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE));
			}
			boolean keepAlive = HttpUtil.isKeepAlive(req);
			FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(CONTENT));
			response.headers().set(CONTENT_TYPE, "text/plain");
			response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
			
			if(!keepAlive) {
				ctx.write(response).addListener(ChannelFutureListener.CLOSE);
			} else {
				response.headers().set(CONNECTION, KEEP_ALIVE);
				ctx.write(response);
			}
			
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
	
	
}
