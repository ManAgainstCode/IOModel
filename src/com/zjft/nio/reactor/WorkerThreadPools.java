package com.zjft.nio.reactor;

import java.nio.channels.SelectionKey;

public class WorkerThreadPools {
	static PooledExecutor pool = new PooledExecutor(...);
	static final int PROCESSING = 3;
	
	synchronized void read() {
		socket.read(input);
		if (inputIsCompleted) {
			state = PROCESSING;
			pool.execute(new Processor());
		}
	}
	
	synchronized void processAndHandOff() {
		process();
		state = SENDING;
		key.interestOps(SelectionKey.OP_WRITE);
	}
	
	class Processor() implements Runnable() {
		public void run() {processAndHandOff();}
	}
}
