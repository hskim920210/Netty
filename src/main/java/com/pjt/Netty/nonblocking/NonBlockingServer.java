package com.pjt.Netty.nonblocking;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class NonBlockingServer {
	private Map<SocketChannel, List<byte[]>> keepDataTrack = new HashMap<>();
	private ByteBuffer buffer = ByteBuffer.allocate(2 * 1024);
	
	private void startEchoServer() {
		try (
			/* Selector는 자신에게 등록된 채널에 변경사항이 있는지 검사하고 */ 
			/* 변경사항이 발생한 채널에 대한 접근을 가능하게 해준다. */
			Selector selector = Selector.open();
			/* 논블로킹 소켓 채널생성 - 채널을 먼저 생성하고 사용할 포트를 바인딩 한다. */
			ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()
		) {
			
			if( (serverSocketChannel.isOpen()) && (selector.isOpen()) ) {
				/* 소켓 채널의 블로킹 모드 기본 값은 true */
				serverSocketChannel.configureBlocking(false);
				serverSocketChannel.bind(new InetSocketAddress(8888));
				
				/* ServerSocketChannel 객체를 Selector 객체에 등록 */
				/* Selector가 감지할 이벤트는 연결요쳥에 해당하는 SelectionKey.OP_ACCEPT */
				serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
				System.out.println("접속 대기중 ...");
				
				while (!Thread.currentThread().isInterrupted()) {
					/* Selector에 등록된 채널에서 변경사항이 발생했는지 검사한다. */
					/* Selector에 아무런 I/O 이벤트도 발생하지 않으면, 쓰레드는 이 부분에서 블로킹 된다. */
					/* I/O 이벤트가 발생하지 않았을 때 블로킹을 피하고 싶다면 selectNow 메소드를 사용하면 된다. */
					selector.select();
					/* Selector에 등록된 채널 중 I/O 이벤트가 발생한 채널의 목록을 조회한다. */
					Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
					
					while (keys.hasNext()) {
						SelectionKey key = keys.next();
						/* 이벤트가 발생한 채널에서 동일한 이벤트가 감지되는 것을 방지하기 위해 조회된 목록에서 제거한다. */
						keys.remove();
						
						if(!key.isValid()) {
							continue;
						}
						
						/* 조회된 I/O 이벤트의 종류가 연결요청인지 확인한다. */
						if(key.isAcceptable()) {
							this.acceptOP(key, selector);
						} 
						/* 조회된 I/O 이벤트의 종류가 데이터 수신인지 확인한다. */
						else if(key.isReadable()) {
							this.readOP(key);
						} 
						/* 조회된 I/O 이벤트의 종류가 데이터 쓰기 기능인지 확인한다. */
						else if(key.isWritable()) {	
							this.writeOP(key);
						}
					}
				}
			} else {
				System.out.println("서버 소켓을 생성하지 못했습니다.");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e);
		}
	}
	
	private void acceptOP(SelectionKey key, Selector selector) throws IOException {
		/* 연결 요청 이벤트가 발생한 채널은 항상 ServerSocketChannel이다. */
		ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
		/* ServerSocketChannel을 이용하여 클라이언트의 연결을 수락하고 연결된 소켓 채널을 가져온다. */
		SocketChannel socketChannel = serverChannel.accept();
		/* 소켓 채널을 논블로킹 모드로 설정 */
		socketChannel.configureBlocking(false);
		
		System.out.println("클라이언트 연결됨 : " + socketChannel.getRemoteAddress());
		
		keepDataTrack.put(socketChannel, new ArrayList<byte[]>());
		/* 클라이언트 소켓 채널을 Selector에 등록하여 I/O 이벤트를 감시한다. */
		socketChannel.register(selector, SelectionKey.OP_READ);
	}
	
	private void readOP(SelectionKey key) {
		try {
			SocketChannel socketChannel = (SocketChannel) key.channel();
			buffer.clear();
			int numRead = -1;
			
			try {
				numRead = socketChannel.read(buffer);
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("데이터 읽기 에러 !");
			}
			
			if(numRead == -1) {
				this.keepDataTrack.remove(socketChannel);
				System.out.println("클라이언트 연결 종료 : " + socketChannel.getRemoteAddress());
				socketChannel.close();
				key.cancel();
				return;
			}
			
			byte[] data = new byte[numRead];
			System.arraycopy(buffer.array(), 0, data, 0, numRead);
			System.out.println(new String(data,"UTF-8") + " from " + socketChannel.getRemoteAddress());
			doEchoJob(key, data);
			
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println(e);
		}
	}
	
	private void writeOP(SelectionKey key) throws IOException {
		SocketChannel socketChannel = (SocketChannel) key.channel();
		
		List<byte[]> channelData = keepDataTrack.get(socketChannel);
		Iterator<byte[]> its = channelData.iterator();
		
		while(its.hasNext()) {
			byte[] it = its.next();
			its.remove();
			socketChannel.write(ByteBuffer.wrap(it));
		}
		
		key.interestOps(SelectionKey.OP_READ);
	}
	
	private void doEchoJob(SelectionKey key, byte[] data) {
		SocketChannel socketChannel = (SocketChannel) key.channel();
		List<byte[]> channelData = keepDataTrack.get(socketChannel);
		channelData.add(data);
		
		key.interestOps(SelectionKey.OP_WRITE);
	}
	
	public static void main(String[] args) {
		NonBlockingServer main = new NonBlockingServer();
		main.startEchoServer();
	}
}