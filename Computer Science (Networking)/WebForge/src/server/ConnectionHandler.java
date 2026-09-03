package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.StringTokenizer;

public class ConnectionHandler implements Runnable {
	private Socket clientConnection;

	public ConnectionHandler(Socket connection) {
		this.clientConnection = connection;
	}

	public void run() {
		
		BufferedReader in = null;
		DataOutputStream out = null;
		
		try {
			
			in = new BufferedReader(new InputStreamReader(clientConnection.getInputStream()));
			out = new DataOutputStream(new BufferedOutputStream(clientConnection.getOutputStream()));			
			String line = in.readLine();
			
			if (line!=null) {
				
				System.out.println("Client request:\t"+line);
				
				if(line.contains("GET"))
				{
					
					StringTokenizer httpGETTokenizer = new StringTokenizer(line);
					String getReq = httpGETTokenizer.nextToken();
					String fileName = httpGETTokenizer.nextToken();
					
					if(fileName.indexOf('/') == 0) {
						fileName = fileName.substring(1);
						System.out.println("fileName var: " + fileName);
					}
					
					File theFile = null;
					
					if(fileName.endsWith(".jpg")) {
						theFile = new File("data/" + fileName);
						System.out.println("theFile var: " + theFile.getPath());
					}
					
					if(fileName.equals("Sample.m4v")) {
						theFile = new File("data/" + fileName);
					}
					
					if(fileName.equals("Afrikaans") || fileName.equals("Zulu") || fileName.equals("ZuluWithImage")) {
						theFile = new File("data/" + fileName + ".html");
					}
					
					if (fileName.equals("TestVid")) {
						theFile = new File("data/" + fileName + ".html");
					}
					
					if(!theFile.exists()) {
						
						System.out.println(theFile + " does not exist.");
						sendErrorHeader(out, 404, "File not found!!");
						
					} else {
						
						out.writeBytes("HTTP/1.1 200 OK\r\n");
						out.writeBytes("Connection: close \r\n");
						
						if(fileName.equals("Afrikaans") || fileName.equals("Zulu") || fileName.equals("ZuluWithImage")) {
							out.writeBytes("Content-Type: text/html\r\n");
						} else if(fileName.endsWith("jpg")) {
							out.writeBytes("Content-Type: image/jpeg\r\n");
						} else if(fileName.endsWith("m4v")) {
							out.writeBytes("Content-Type: video/mp4\\r\\n");
						}
						
						out.writeBytes("Content-length: " + theFile.length() + "\r\n");
						out.writeBytes("\r\n");
						
						try {
							
							BufferedInputStream fin = new BufferedInputStream(new FileInputStream(theFile));
							byte[] buffer = new byte[1024];
							int n = 0;
							
							while ((n = fin.read(buffer)) > 0) {
								out.write(buffer, 0, n);
							}
							
							fin.close();
							
						} catch (IOException e) {
							System.err.println("Error reading the file to be sent to client");
						}
						
						out.writeBytes("\r\n");
						out.flush();
						
					}
					
				}
			}
			else 
			{
				sendErrorHeader(out, 500, "Command not supported/Invalid request");
			}
		}
		catch(IOException err)
		{
			err.printStackTrace();
		}		
		
	}
	
	public void sendErrorHeader(DataOutputStream out, int errorCode, String message)
	{
		String content = "<html><head><title>AN ERROR OCCURRED</title></head>";
		content +="<body>Message: "+message+"</body></html>";
		String errorString = "";
		switch(errorCode)
		{
		case 404:
			errorString = "404 Not Found";
			break;
		case 500:
			errorString = "500 Server Error";
			break;
		}
		
		try {
			out.writeBytes("HTTP/1.1 "+ errorCode + " " +errorString +"\r\n");
			out.writeBytes("Connection: close\r\n");
			out.writeBytes("Content-length: " + content.getBytes().length + "\r\n");
			out.writeBytes("\r\n");
			out.writeBytes(content);
			out.writeBytes("\r\n");
			out.flush();			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
