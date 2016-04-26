package com.zjft.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;
import java.util.Set;

public class Server {

	private int port;

	public Server(int port) {
		this.port = port;
	}

	public void start() throws Exception { 
		ServerSocketChannel serverChannel = ServerSocketChannel.open();
		serverChannel.bind(new InetSocketAddress(port)).configureBlocking(false);
		
		Selector selector = Selector.open();
		serverChannel.register(selector, SelectionKey.OP_ACCEPT);

		while (true) {
			if (selector.select() == 0)	//blocked
				continue;
			Set<SelectionKey> keys = selector.selectedKeys();
			Iterator<SelectionKey> iterator = keys.iterator();
			while (iterator.hasNext()) {
				SelectionKey key = iterator.next();
				iterator.remove();
				if (key.isAcceptable()) {
					ServerSocketChannel server = (ServerSocketChannel) key.channel();
					SocketChannel socket = server.accept();
					socket.configureBlocking(false);
					SelectionKey clientKey = socket.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
					ByteBuffer buffer = ByteBuffer.allocate(100);
					clientKey.attach(buffer);
				} 
				if (key.isReadable()) {
					SocketChannel reader = (SocketChannel) key.channel();
					ByteBuffer result = (ByteBuffer) key.attachment();
					reader.read(result);						
				} 
				if (key.isWritable()) {
					SocketChannel writer = (SocketChannel) key.channel();
					ByteBuffer output = (ByteBuffer) key.attachment();
					output.flip();
					writer.write(output);
					output.compact();
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {
		System.out.println("Server started......");
		new Server(9092).start();
	}
}
