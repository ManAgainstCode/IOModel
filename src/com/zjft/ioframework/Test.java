package com.zjft.ioframework;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Timer;
import java.util.TimerTask;

public class Test {
	public static void main(String[] args) throws Exception {
		System.out.println("Server started, listen on 9095......");
		Server server = new Server();
		server.bind("127.0.0.1", 9095).setHandler(new Handler() {

			@Override
			public void onRecv(Session session) throws IOException {
				System.out.println("onRecv");	
				String msg = (String) session.read();
				msg = "Hello " + msg;
				session.write(msg);
			}

			@Override
			public void onConnected(Session session) {				
				System.out.println("onConnected");
			}

			@Override
			public void onClosed(Session session) {
				System.out.println("onClosed");
			}			
		});
		server.start();
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					server.close();
				} catch (Exception e) {				
					e.printStackTrace();
				}
			}
		}, 3000);
	}
}
