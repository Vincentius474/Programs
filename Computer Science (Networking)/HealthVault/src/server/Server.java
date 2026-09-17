package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	
	public Server(int port) {
		
		ServerSocket serverSocket;
		boolean ready;
		
		try {
			
			serverSocket = new ServerSocket(port);
			System.out.println("Server bound to port: " + port);
			ready = true;
			
			while(ready) {
				
				System.out.println("Waiting for clients to connect...");
				Socket clientSocket = serverSocket.accept();
				try {
					Thread clientThread = new Thread(new HealthHandler(clientSocket));
					clientThread.start();
				} catch (IOException e) {
					clientSocket.close();
					e.printStackTrace();
				}
				
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	// Main method
	public static void main(String[] args) {
		Server server = new Server(2026);
	}
}
