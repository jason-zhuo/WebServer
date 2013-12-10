import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;

public class SecuritySever implements Runnable {
	static boolean islogging = true;
	Socket socket;

	public SecuritySever(Socket s) {
		socket = s;
	}

	String xml = "<?xml version=\"1.0\"?><cross-domain-policy><site-control permitted-cross-domain-policies=\"all\"/><allow-access-from domain=\"*\" to-ports=\"*\"/></cross-domain-policy>\0";

	@Override
	public void run() {
		// TODO Auto-generated method stub
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(
					socket.getInputStream(), "UTF-8"));
			PrintWriter pw = new PrintWriter(socket.getOutputStream());
			char[] by = new char[22];
			br.read(by, 0, 22);
			String s = new String(by);
			System.out.println("s=" + s);
			if (s.equals("<policy-file-request/>")) {
				System.out.println("recieved policy-file-request");
				pw.print(xml);
				pw.flush();
				br.close();
				pw.close();
				socket.close();
				return;
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
