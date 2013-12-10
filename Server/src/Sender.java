import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;


public class Sender {
	static boolean islogging = true;
	 static Socket m_socket;

	public  Sender(Socket s) {
		m_socket = s;
	}
	public void send_msg(String Msg)
	{
		try {
			OutputStream outStream = m_socket.getOutputStream();
			outStream.write(Msg.getBytes());
			outStream.flush();
			
			return;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
