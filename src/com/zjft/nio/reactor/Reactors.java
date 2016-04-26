package com.zjft.nio.reactor;

public class Reactors {
	Selector[] selectors;
	int next = 0;
	class Acceptor {
		public void run() {
		Socket connection = serverSocket.accept();
		if (connection != null) 
			new Handler(selectors[next], connection);
		if (++next == selectors.length) next = 0;	
	}
}
