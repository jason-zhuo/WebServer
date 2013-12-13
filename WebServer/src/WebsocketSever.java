
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.HashMap;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import sun.misc.BASE64Encoder;
import sun.misc.BASE64Decoder;


public class WebsocketSever implements Runnable {
	static boolean islogging = true;
	HashMap<String, String> reqHeader ;
	static final String magicalString="258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
	Socket socket;
	static MessageDigest md;   
	public WebsocketSever(Socket s) {
		socket = s;
	}

	public void getRequestHeader(byte [] data)
	{
		String requestHeader = new String(data);
		requestHeader=requestHeader.substring(0,requestHeader.indexOf("\r\n\r\n"));
		String []reqarr= requestHeader.split("\r\n");
		reqHeader = new HashMap<String, String>();
		for(int i=0;i<reqarr.length;i++)
		{
			String requestLine=reqarr[i];
			if(requestLine.toUpperCase().startsWith("GET")||requestLine.toUpperCase().startsWith("POST"))
			{
				String []first =requestLine.split(" ");
				if(first.length==3)//fisrt line
				{
					String method=first[0];
					String location= first[1].replaceAll("\\s", "");
					String protocal = first[2].split("/")[0];
					String protocalversion = first[2].split("/")[1];
					reqHeader.put("Method", method);
					reqHeader.put("location", location);
					reqHeader.put("protocal", protocal);
					reqHeader.put("protocalversion", protocalversion);
				}
			}else
			{
				String []reqline=requestLine.split(":");
				if(reqline.length==2)
				{
					String key =reqline[0];
					String value= reqline[1].replaceAll("\\s", "");
					reqHeader.put(key, value);
				}
			}
		}
		
	} 
	
	public static String byteArrayToHexString(byte[] b) {
		  String result = "";
		  for (int i=0; i < b.length; i++) {
		    result +=
		          Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
		  }
		  return result;
		}
	
	public static String getBASE64(byte[] s) {
		if (s == null) return null;
		return (new sun.misc.BASE64Encoder()).encode( s );
		} 
		 
		// 将 BASE64 编码的字符串 s 进行解码
	public static String getFromBASE64(String s) {
		if (s == null) 
			return null;
			
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			byte[] b = decoder.decodeBuffer(s);
			return new String(b);
		} catch (Exception e) {
			return null;
		}
	}
	
	public  byte[] toSHA1(byte[] convertme) {
	    this.md = null;
	    try {
	        md = MessageDigest.getInstance("SHA-1");
	    }
	    catch(NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    } 
	    return (md.digest(convertme));
	}
	
	static String AnalyticData(byte[] recBytes, int recByteLength)
	{
		String client_msg = new String("");
		if (recByteLength < 2) 
		{ 
			return client_msg; 
		}

        boolean fin = (recBytes[0] & 0x80) == 0x80; // 1bit，1表示最后一帧  
        if (!fin)
        {
            return client_msg;// 超过一帧暂不处理 
        }

        boolean mask_flag = (recBytes[1] & 0x80) == 0x80; // 是否包含掩码  
        if (!mask_flag)
        {
            return client_msg; // 不包含掩码的暂不处理
        }

        int payload_len = recBytes[1] & 0x7F; // 数据长度  

        byte[] masks = new byte[4];
        byte[] payload_data;
		
        if (payload_len == 126){
            System.arraycopy(recBytes, 4, masks, 0, 4);
            payload_len = (short)(recBytes[2] << 8 | recBytes[3]);
            payload_data = new byte[payload_len];
            System.arraycopy(recBytes, 8, payload_data, 0, payload_len);

        }else if (payload_len == 127){
        	System.arraycopy(recBytes, 10, masks, 0, 4);
            byte[] uInt64Bytes = new byte[8];
            for (int i = 0; i < 8; i++){
                uInt64Bytes[i] = recBytes[9 - i];
            }
            //long len = BitConverter.ToUInt64(uInt64Bytes, 0);
            long len = uInt64Bytes[7]<<56 + uInt64Bytes[6]<<48 +uInt64Bytes[5]<<40 
            		+uInt64Bytes[4]<<32 + uInt64Bytes[3]<<24 + uInt64Bytes[2]<<16 +
            		uInt64Bytes[1]<<8 +uInt64Bytes[0];
            payload_data = new byte[(int)len];
            for (int i = 0; i < len; i++){
                payload_data[i] = recBytes[i + 14];
            }
        }else{
        	System.arraycopy(recBytes, 2, masks, 0, 4);
            payload_data = new byte[payload_len];
            System.arraycopy(recBytes, 6, payload_data, 0, payload_len);

        }

        for (int i = 0; i < payload_len; i++){
            payload_data[i] = (byte)(payload_data[i] ^ masks[i % 4]);
        }
        
        try {
			client_msg = new String(payload_data,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		return client_msg;
	}
	
//	public void Get
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("Incoming Websocket:"+socket.getInetAddress()+" port:"+ socket.getPort());
		InputStream isR;
		byte [] buffer = new byte[2048];
		try {
				isR=  socket.getInputStream();
				socket.setKeepAlive(true);
				socket.setSoTimeout(10*1000);//10s 
				int available=isR.available();
				isR.read(buffer);
				getRequestHeader(buffer);
				String key_from_client = reqHeader.get("Sec-WebSocket-Key");
				key_from_client += magicalString;
					 
				byte [] key_from_client_byte = key_from_client.getBytes();
				byte sha1[] = new byte[20];
				 sha1 = toSHA1(key_from_client_byte);
				String base64_key = getBASE64(sha1);
				
				StringBuilder str_builder = new StringBuilder();
				str_builder.append("HTTP/1.1 101 Switching Protocols\r\n");
				str_builder.append("Upgrade: websocket\r\n");
				str_builder.append("Connection: Upgrade\r\n");
				str_builder.append("Sec-WebSocket-Accept: "+base64_key+"\r\n\r\n");
				Sender sender = new Sender(socket);
				sender.send_msg(str_builder.toString());
				System.out.println("Handshake protocals has been send !\nWaiting for client data\n");
				
				socket.setSoTimeout(0); //infinite timeout
				InputStream isR2=  socket.getInputStream();
				
				available = isR2.available();
				
				buffer = new byte[1024];
				available = isR2.read(buffer);			
				String client_msg = AnalyticData(buffer,available );
				System.out.println("Receive data from client:"+ client_msg+"\n");

				
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
