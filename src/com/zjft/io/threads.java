package com.zjft.io;

public class Threads implements Strategy{
	@Override
	public void work (Handler handler) {
		new Thread(handler).start();
	}
}
