import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;

public class Server implements Runnable {
	static boolean islogging = true;
	Socket listeningsocket;
	public Server(Socket s) {
		listeningsocket = s;
	}
	//String xml = "<?xml version=\"1.0\"?><cross-domain-policy><site-control permitted-cross-domain-policies=\"all\"/><allow-access-from domain=\"*\" to-ports=\"10800\"/></cross-domain-policy>\0";
	public void run() {
		// TODO Auto-generated method stub
		try {
			//listeningsocket.setSoTimeout(100);
			listeningsocket.setKeepAlive(false);
			String SrcAddress = listeningsocket.getInetAddress().toString().split("/")[1];
			int  imcomingport =listeningsocket.getPort();
			SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss,SSS");
			String Currenttime = sFormat.format(System.currentTimeMillis());
			System.out.print(Currenttime+"-->");
			System.out.println("Incoming Address:"+SrcAddress+":"+imcomingport);
//			String test = "hello\0";
//			PrintWriter pw = new PrintWriter(listeningsocket.getOutputStream());
//			pw.print(test);
//			pw.flush();
			
			
			InputStreamReader iread = new InputStreamReader(listeningsocket.getInputStream());
			char buf[] = new char[1024];
			iread.read(buf);
			String strt = new String (buf);
			
			iread.close();
			
			System.out.println(buf);
			listeningsocket.close();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			
		}

	}
}
