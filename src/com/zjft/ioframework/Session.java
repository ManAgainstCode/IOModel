
package com.zjft.ioframework;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class Session {
	SelectionKey key;
	SocketChannel channel;
	volatile boolean canWrite = false;
	volatile boolean canRead = false;
	ByteBuffer temp = null;
	
	public Session(SelectionKey key, SocketChannel channel) {
		this.key = key;
		this.channel = channel;
	}
	
	public String getAddr() throws IOException {
		return channel.getRemoteAddress().toString();
	}
	
	public void setReadable(boolean read) {
		canRead = read;
	}
	
	public Object read() throws IOException {
		while (!canRead)
			continue;
		ByteBuffer buffer = (ByteBuffer) key.attachment();
		channel.read(buffer);
		return ByteBufferToString(buffer);
	}
	
	public void write(Object result) throws IOException {
		temp = StringToByteBuffer((String) result);
	}
	
	public void setWritable(boolean write) {
		canWrite = write;
	}
	
	public String ByteBufferToString(ByteBuffer bb) throws CharacterCodingException {
		Charset cs = Charset.forName("utf-8");
		CharsetDecoder decoder = cs.newDecoder();
		ByteBuffer copy = bb.asReadOnlyBuffer();
		copy.flip();
		CharBuffer buffer = decoder.decode(copy);
		return buffer.toString();
	}
	
	public ByteBuffer StringToByteBuffer(String msg) {
		return ByteBuffer.wrap(msg.getBytes());
	}
	
	public ByteBuffer getByteBuffer() {
		return temp;
	}
}
