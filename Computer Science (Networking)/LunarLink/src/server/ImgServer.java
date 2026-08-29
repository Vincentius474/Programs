package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ImgServer {

	private ServerSocket server;
	private boolean running;
	
	public ImgServer(int port) {
		
		try {
			
			server = new ServerSocket(port);
			running = true;
			startServer();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private void startServer() {
		
		System.out.println("Starting the server...");
		while (running) {
			
			try {
				
				Socket incomingSocketConn = server.accept();
				System.out.println("New Client Connected");
				ImgHandler imgHandler = new ImgHandler(incomingSocketConn);
				Thread thread = new Thread(imgHandler);
				thread.start();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	public static void main(String[] args) {
		ImgServer s = new ImgServer(5432);
	}
}
