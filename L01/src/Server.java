import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Hashtable;


public class Server {
	
  private static Hashtable<String, String> data_base;
  public static int srvc_port;
  public static int mcast_port;
  private static InetAddress mcast_addr;
  private static String srvc_addr= "localhost";

  public static void main(String[] args) throws IOException{
	  
	  if(args.length!=3) {
		  System.out.println("ERROR - Invalid arguments");
		  System.out.println("Usage - Server <srvc_port> <mcast_addr> <mcast_port>");
		  return;
	  }
	  
	  srvc_port = Integer.parseInt(args[0]);
	  mcast_addr = InetAddress.getByName(args[1]);
	  mcast_port = Integer.parseInt(args[2]);
	  
	  data_base = new Hashtable<String, String>();
	  
	  DatagramSocket socket = new DatagramSocket(srvc_port);
	  
	  multicastSocket();
	 

      while (true) {
    	  
    	byte[] r_buffer = new byte[1024];
        byte[] s_buffer = new byte[1024];
    	  
  
    	// receive request 
		DatagramPacket receivePacket = new DatagramPacket(r_buffer, r_buffer.length);
		socket.receive(receivePacket);
		String request = new String(receivePacket.getData());
		System.out.println("RECEIVED: "+ request);
		
		// handle request
		InetAddress s_address = receivePacket.getAddress();
		int s_port = receivePacket.getPort();
		String response = handleRequest(request);
		System.out.println("RESPONSE: " + response);
		
		// send response
		s_buffer = response.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(s_buffer, s_buffer.length, s_address, s_port);
		socket.send(sendPacket);
		}
      
      /*
      System.out.println("Ending Connection");
      socket.close();
      System.out.println("Server Terminated");
      */

  }

private static void multicastSocket() throws IOException {
	// create MulticastSocket
	MulticastSocket mcast_socket = new MulticastSocket(mcast_port);
	mcast_socket.setTimeToLive(1); 
	
	// create multicast datagram packet
	String msg = srvc_port + " " + srvc_addr;
	byte[] s_buffer = new byte[1024];
	DatagramPacket mcast_packet = new DatagramPacket(msg.getBytes(), msg.getBytes().length,
	mcast_addr, mcast_port);
	
	while(true) {
		mcast_socket.send(mcast_packet);
		
		
	}

	
	
	
	
	
}

private static String handleRequest(String request) {
	String[] array = request.split(" ");
	
	String request_type = array[0];
	String plate = array[1];
	
	if(request_type.equals("REGISTER")) {
		if(!data_base.containsKey(plate)) {
			String name = "";
			
			for(int i =2; i < array.length; i++) 
				name += array[i] + " ";
			
			name = name.substring(0, name.length() - 1);
			
			data_base.put(plate, name);
			return Integer.toString(data_base.size());
			
		}else return "-1";
			
	}else if(request_type.equals("LOOKUP")) {
		
		
		
		if(data_base.containsKey(plate)) {
			return data_base.get(plate);
		}else return "NOT_FOUND";
	}
	
	return "-1";
}


}
