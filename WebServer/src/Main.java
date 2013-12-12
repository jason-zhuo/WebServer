import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.text.*;
import Thread.Threadpool;

public class Main extends Thread {
	final static int portNum = 10800;  // for proxy
	final static int WebSocketPort = 10086;  //for html 5
	final static int SecureSocketPort =10000; // for flash certificate bypassing
	String Serverinfo =null;
	int port;

	
	public Main(final String info, final int port )
	{
		this.Serverinfo =info;
		this.port=port;
	}
	public static void StartServices(String info, final int port )
	{
		String time  =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date (System.currentTimeMillis()));
		System.out.println(info+" on PORT: "+port+ " "+time);
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
		StartServices(Serverinfo, port);
	}
	 
	 
	public static void main(String args[]){
		
		Thread thread=null;
		ExecutorService pool = Executors.newFixedThreadPool(3);
		for(int i=0; i!=3;i++)
		{
			switch(i)
			{
			case 0: 
				thread = new Main(">> Watching Server Started<<",portNum);
				pool.execute(thread);
				break;
			case 1:
				thread = new Main(">> WebSocket Server Started<<",WebSocketPort);
				pool.execute(thread);
				break;
			case 2: 
				thread = new Main(">> Secure Server Started<<",SecureSocketPort);
				pool.execute(thread);
				break;
			}
			
			
		}
		
		System.out.println("Main thread exit!");
	}
}
