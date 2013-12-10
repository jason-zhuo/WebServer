import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import Thread.Threadpool;

public class Main extends Thread {
	final static int portNum = 10800;
	final static int WebSocketPort = 10086;
	public static void startProxy(final int port) {
		System.out.println(">> Watching Server Started<<"); 
		ServerSocket sSocket = null;
		Threadpool pool = Threadpool.getInstance();
		try {
			sSocket = new ServerSocket(port);
			
			while (true) {
				Socket tmp = sSocket.accept();
				pool.assign(new Server(tmp));
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		} finally {
			pool.complete();
			if (sSocket != null) {
				try {
					sSocket.close();
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}

		}

	}
	 public void run() {
		 startSecureSever(WebSocketPort);
	}
	public static void startSecureSever(final int port) {
		System.out.println(">> WebSocket Server Started<<"); 
		ServerSocket sSocket = null;
		Threadpool pool = Threadpool.getInstance();
		try {
			sSocket = new ServerSocket(port);
			
			while (true) {
				Socket tmp = sSocket.accept();
				pool.assign(new WebsocketSever(tmp));
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		} finally {
			pool.complete();
			if (sSocket != null) {
				try {
					sSocket.close();
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}

		}

	}
	public static void main(String args[]){
		Main tt = new Main();
		tt.start();
		startProxy(portNum);
		
	}
}
