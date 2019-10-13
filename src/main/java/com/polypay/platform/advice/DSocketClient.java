package com.polypay.platform.advice;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class DSocketClient {
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		
		Socket s = new Socket("localhost",8080);
		
		OutputStream outputStream = s.getOutputStream();
		
		String line = "a";
		while(true)
		{
			outputStream.write(line.getBytes());
			outputStream.flush();
		}
		
	}

}
