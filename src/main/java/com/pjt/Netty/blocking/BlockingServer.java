package com.pjt.Netty.blocking;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class BlockingServer {
	public static void main(String[] args) throws Exception {
		BlockingServer server = new BlockingServer();
		server.run();
	}
	
	private void run() throws IOException {
		@SuppressWarnings("resource")
		ServerSocket server = new ServerSocket(8888);	/* 1 */
		System.out.println("접속 대기중 ...");
		
		while (!Thread.currentThread().isInterrupted()) {
			Socket sock = server.accept();	/* 2 */
			System.out.println("클라이언트 연결됨.");
			
			OutputStream out = sock.getOutputStream();	/* 3 */
			InputStream in = sock.getInputStream();	/* 4 */
			
			while (!Thread.currentThread().isInterrupted()) {
				try {
					int request = in.read();	/* 5 */
					out.write(request);
				} catch (IOException e) {
					break;
				}
			}
			
		}
	}
}
