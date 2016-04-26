package com.zjft.nio.reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Reactor implements Runnable{
	
	final Selector selector;
	final ServerSocketChannel serverSocket;
	
	public Reactor(int port) throws IOException {
		selector = Selector.open();
		serverSocket = ServerSocketChannel.open();
		serverSocket.socket().bind(new InetSocketAddress(port));
		serverSocket.configureBlocking(false);
		SelectionKey key = serverSocket.register(selector, SelectionKey.OP_ACCEPT);
		key.attach(new Acceptor());
	}
	
	@Override
	public void run() {
		try {
			while (!Thread.interrupted()) {
				selector.select();
				Set<SelectionKey> selected = selector.selectedKeys();
				Iterator<SelectionKey> iterator = selected.iterator();
				while (iterator.hasNext())
					dispatch((SelectionKey) iterator.next());				
			}
		} catch (IOException e) {}
	}
	
	void dispatch(SelectionKey key) {
		Runnable r = (Runnable) key.attachment();
		if (r != null)
			r.run();
	}
	
	class Acceptor implements Runnable {
		
		@Override
		public void run() {		
			try {
				 SocketChannel c = serverSocket.accept();
				 if (c != null)
					 new Handler(selector, c);					 	
			} catch (IOException e) {}
		}
	}
}
