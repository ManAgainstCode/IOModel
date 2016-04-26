package com.zjft.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server2 {

	private int port;
	public static int DEFAULT_PORT = 9094;
	
	public Server2() {this.port = DEFAULT_PORT;}
	public Server2(int port) {this.port = port;}

	public void start() throws IOException, InterruptedException {
		AsynchronousChannelGroup group = AsynchronousChannelGroup.withCachedThreadPool(Executors.newCachedThreadPool(), 3);
		AsynchronousServerSocketChannel listener = AsynchronousServerSocketChannel.open(group).bind(new InetSocketAddress(port));
		listener.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
			@Override
			public void completed(AsynchronousSocketChannel result, Void attachment) {				
				try {
					listener.accept(null, this);
					System.out.println(result.getRemoteAddress().toString());
					ByteBuffer buffer = ByteBuffer.allocate(100);
					while (result.read(buffer).get() != -1) {
						buffer.flip();
						result.write(buffer).get();
						if (buffer.hasRemaining())
							buffer.compact();
						else 
							buffer.clear();						
					}
					result.close();
				} catch (IOException e) {				
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void failed(Throwable exc, Void attachment) {
				System.out.println(exc.toString());
			}
		});
		
		group.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		System.out.println("Server started......");
		new Server2(9094).start();
	}
}
