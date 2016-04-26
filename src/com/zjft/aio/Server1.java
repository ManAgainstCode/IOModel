package com.zjft.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Server1 {	
	
	private int port;
	private static int DEFAULT_PORT = 9093;	
	
	public Server1() {port = DEFAULT_PORT;}	
	public Server1(int port) {this.port = port;}
	
	
	
	public void start() throws Exception {
		ExecutorService exec = Executors.newCachedThreadPool();
		AsynchronousServerSocketChannel asynServerSocketChannel = AsynchronousServerSocketChannel.open(); 
		if (asynServerSocketChannel.isOpen()) {
			asynServerSocketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 4 * 1024);
			asynServerSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
			asynServerSocketChannel.bind(new InetSocketAddress(port));
			while (true) {
				Future<AsynchronousSocketChannel> asynSocketChannelFuture = asynServerSocketChannel.accept();
				try {
					AsynchronousSocketChannel asynSocketChannel = asynSocketChannelFuture.get();
					exec.execute(new Runnable() {
						@Override
						public void run() {
							try {
								String host = asynSocketChannel.getRemoteAddress().toString();
								System.out.println("Remote addr is " + host);
								final ByteBuffer buffer = ByteBuffer.allocateDirect(100);
								while (asynSocketChannel.read(buffer).get() != -1) {
									buffer.flip();
									asynSocketChannel.write(buffer).get();
									if (buffer.hasRemaining())
										buffer.compact();
									else 
										buffer.clear();
								}
								asynSocketChannel.close();
							} catch (Exception e) {
								e.printStackTrace();
							}												
						}					
					});							
				} catch (InterruptedException | ExecutionException ex) {
					System.err.println(ex);
					System.err.println("Server is shutting down...");
					exec.shutdown();
					while (!exec.isTerminated()) {}
					break;
				}
			}
		} else {
			System.out.println("The asynchronous server socket cannot be opened");
		}
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println("Server started......");
		new Server1(9093).start();
	}
}
