# /usr/bin/python
# coding: utf-8

import socket, threading, time

def handler(message, no):
	s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

	s.connect(("127.0.0.1", 9090))

	s.send(message)

	print s.recv(64)

	s.close()

message = "lipeng"
no = 0

while no < 1:
	thread = threading.Thread(target=handler, args=(message, no))
	thread.start()
	no = no + 1
