package com.zjft.io;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	int port;
	Strategy strategy;
	
	public Server(int port, Strategy s) {this.port = port; strategy = s;}
	
	public void start() throws IOException {
		System.out.println("Server started......");
		ServerSocket ss = new ServerSocket(port);
		while (true) {
			Socket socket = ss.accept();
			strategy.work(new Handler(socket));
		}
	}
	
	public static void main(String[] args) {
		new Thread() {
			public void run() {
				try {
					new Server(9090, new Threadpool()).start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
		new Thread() {
			public void run() {
				try {
					new Server(9091, new Threads()).start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
}
