package com.pjt.Netty.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;

public class HttpHelloWorldServerInitializer extends ChannelInitializer<SocketChannel> {
	private final SslContext sslCtx;

	public HttpHelloWorldServerInitializer(SslContext sslCtx) {
		this.sslCtx = sslCtx;
	}
	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline p = ch.pipeline();
		if(sslCtx != null) {
			p.addLast(sslCtx.newHandler(ch.alloc()));
		}
		/* HttpServerCodec은 생성자에서 HttpRequestDecoder, HttpResponseEncoder 모두 생성한다. */
		/* 간단한 웹서버를 생성하는데 사용하는 코덱으로, 수신된 ByteBuf 객체를 HttpRequest와 HttpContent 객체로 변환, */
		/* HttpResponse 객체를 ByteBuf로 인코딩하여 송신한다. */
		p.addLast(new HttpServerCodec());
		/* HttpServerCodec이 수신한 이벤트와 데이터를 처리하여 HTTP 객체로 변환한 다음*/
		/* channelRead 이벤트를 HttpHelloWorldServerHandler 클래스로 전달 */
		p.addLast(new HttpHelloWorldServerHandler());
	}
}
