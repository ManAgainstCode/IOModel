package com.zjft.ioframework;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Server implements ServerService {

	String ip;
	int port;
	Handler handler;
	Map<SelectionKey, Session> map = new HashMap<SelectionKey, Session>();
	ServerSocketChannel serverSocketChannel;
	
	@Override
	public Server bind(String ip, int port) {
		this.ip = ip;
		this.port = port;
		return this;
	}


	@Override
	public void start() throws Exception {
		serverSocketChannel = ServerSocketChannel.open();
		Selector selector = Selector.open();
		serverSocketChannel.bind(new InetSocketAddress(ip, port));
		serverSocketChannel.configureBlocking(false);
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		while (true) {
			if (selector.select() == 0) 
				continue;
			Set<SelectionKey> selectionKeys = selector.selectedKeys();
			Iterator<SelectionKey> iterator = selectionKeys.iterator();
			while (iterator.hasNext()) {
				SelectionKey key = iterator.next();
				iterator.remove();
				if (key.isAcceptable()) {
					SocketChannel socketChannel = SocketChannel.open();
					socketChannel = serverSocketChannel.accept();			
					SelectionKey socketKey = socketChannel.configureBlocking(false).register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
					ByteBuffer buffer = ByteBuffer.allocate(100);
					socketKey.attach(buffer);
					Session session;
					if (!map.containsKey(socketKey)) {
						session = new Session(socketKey, socketChannel);
						map.put(socketKey, session);
					} else {
						session = map.get(socketKey);
					}	
					fireConnected(session);
				}
				if (key.isReadable()) {
					SocketChannel socketChannel = (SocketChannel) key.channel();
					Session session;
					if (!map.containsKey(key)) {
						session = new Session(key, socketChannel);
						map.put(key, session);
					} else {
						session = map.get(key);
					}	
					session.setReadable(true);
					fireRecv(session);
				}
				if (key.isWritable()) {
					SocketChannel socketChannel = (SocketChannel) key.channel();
					Session session;
					if (!map.containsKey(key)) {
						session = new Session(key, socketChannel);
						map.put(key, session);
					} else {
						session = map.get(key);
					}	
					ByteBuffer buffer;
					if ((buffer = session.getByteBuffer()) != null) {
						ByteBuffer bf = ByteBuffer.allocate(buffer.capacity());
						bf.put(buffer);
						bf.flip();
						socketChannel.write(bf);
						bf.compact();
						socketChannel.close();
					}					
				}				
			}
		}
	}
	
	@Override
	public void close() throws Exception {	
		serverSocketChannel.close();
		map.clear();
	}

	@Override
	public void setHandler(Handler handler) {
		this.handler = handler;
	}
	
	protected void fireConnected(Session session) throws Exception{
		if (handler == null) 
			throw new RuntimeException();
		handler.onConnected(session);
	}
	
	protected void fireClosed(Session session) {
		if (handler == null) 
			throw new RuntimeException();
		handler.onClosed(session);
	}
	
	protected void fireRecv(Session session) throws IOException {
		if (handler == null) 
			throw new RuntimeException();
		handler.onRecv(session);
	}
}
