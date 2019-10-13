package com.polypay.platform.advice;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DSocketServer {

	private static ExecutorService es = Executors.newFixedThreadPool(10);

	public static void main(String[] args) throws IOException {

		ServerSocket ss = new ServerSocket(8080);
		System.out.println("服务端启动成功");
		Socket accept = ss.accept();
		es.submit(() -> {
			InputStream inputStream = null;
			try {
				System.out.println("处理客户端数据");
				inputStream = accept.getInputStream();

				byte[] buf = new byte[1024];
				int len;
				StringBuffer sb = new StringBuffer();
				while ((len = inputStream.read(buf)) > 0) {
					inputStream.read(buf, 0, len);
					sb.append(new String(buf));
				}

				System.out.println(Thread.currentThread().getName() + "-" + sb.toString());

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

	}

}
