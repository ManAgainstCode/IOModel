package com.zjft.ioframework;

import java.io.IOException;

public interface ServerService {
	public ServerService bind(String ip, int port);
	public void start() throws Exception;
	public void close() throws Exception;
	public void setHandler(Handler handler);
}
