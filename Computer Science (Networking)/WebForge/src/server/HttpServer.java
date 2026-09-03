package server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Calendar;

public class HttpServer {

	private ServerSocket server;
	private boolean ready  = false;
	private FileWriter logFile;
	
	public HttpServer(int port)
	{
		
		try {
			
			ready = true;
			server = new ServerSocket(port);
			ready = true;
			
			try 
			{
				File log = new File("./webserver.log");
				logFile = new FileWriter(log);
			}
			catch(IOException e)
			{
				System.err.println("Error opening log file...");
			}
			
		} catch (IOException e) {
			
			System.err.println("Error starting server...");
		}	
		
	}
	
	// Listen to connections via the server
	public void start()
	{
		
		log("Server started on port:"+server.getLocalPort());
		
		while(ready)
		{
			
			try {
				log("Client connected");
				Thread thread = new Thread(new ConnectionHandler(server.accept()));
				thread.start();
			
			}catch(IOException e) {
				e.printStackTrace();
			}
			
		}
		
		try 
		{
			logFile.close();
		} catch(IOException ex) {
			ex.printStackTrace();
		}
		
		
	}
	
	synchronized void log(String message) 
	{
		
		try {
			logFile.write(Calendar.getInstance().getTime().toString());
			logFile.write("\t");
			logFile.write(message);
			logFile.write("\r\n");
			logFile.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
