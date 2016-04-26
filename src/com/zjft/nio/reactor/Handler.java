package com.zjft.nio.reactor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

final class Handler implements Runnable {

	final SocketChannel socket;
	final SelectionKey key;
	ByteBuffer input = ByteBuffer.allocate(100);
	ByteBuffer output = ByteBuffer.allocate(100);
	static final int READING = 0, SENDING = 1;
	int state = READING;
	
	Handler(Selector selector, SocketChannel sc) throws IOException {
		socket = sc;
		socket.configureBlocking(false);
		key = sc.register(selector, 0);
		key.attach(this);
		key.interestOps(SelectionKey.OP_READ);
		selector.wakeup();
	}
	
	boolean inputIsComplete() {/*default true*/ return true;}
	boolean outputIsComplete() {/*default true*/ return true;}
	void process() {}
	
	@Override
	public void run() {
		try {
			if (state == READING) read();
			else if (state == SENDING) send();			
		} catch (IOException e) {}
	}
	
	void read() throws IOException {
		socket.read(input);
		if (inputIsComplete()) {
			process();
			state = SENDING;
			key.interestOps(SelectionKey.OP_WRITE);
		}
	}
	
	void send() throws IOException {
		socket.write(output);
		if (outputIsComplete()) key.cancel();
	}
}
