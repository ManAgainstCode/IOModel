package com.zjft.io;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Threadpool implements Strategy {
	
	public static final ExecutorService exec = Executors.newCachedThreadPool();
	
	@Override
	public void work(Handler handler) {
		exec.execute(handler);
	}
}
