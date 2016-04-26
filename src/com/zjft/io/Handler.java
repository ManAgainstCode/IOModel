package com.zjft.io;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;

public class Handler implements Runnable {

	public static int count = 0;
	private final int id = count++;
	
	private Socket socket;
	
	public Handler(Socket socket) {this.socket = socket;}
	
	@Override
	public void run() {
		try {
			Reader reader = new InputStreamReader(socket.getInputStream());
			char[] buffer = new char[6];
			reader.read(buffer);
			StringBuilder sb = new StringBuilder();
			sb.append(buffer);
			
			for (int i=0; i < 100000; i++) {}
			
			Writer writer = new OutputStreamWriter(socket.getOutputStream());
			writer.write(id + " thread - " + getMemoryStatus());
			writer.flush();
			writer.close();
			reader.close();
			socket.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private String getMemoryStatus() {
		long maxMemory = Runtime.getRuntime().maxMemory();
		long freeMemory = Runtime.getRuntime().freeMemory();
		long totalMemory = Runtime.getRuntime().totalMemory();
		return "Memory: " + freeMemory + "/" + totalMemory + "/" + maxMemory;
	}
}
