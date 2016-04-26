package com.zjft.ioframework;

import java.io.IOException;

public interface Handler {
	public void onRecv (Session session) throws IOException ;
	public void onConnected(Session session);
	public void onClosed(Session session);
}
