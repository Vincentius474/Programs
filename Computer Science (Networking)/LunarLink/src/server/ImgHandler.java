package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;

public class ImgHandler implements Runnable {
	private Socket incomingConnection;
	private OutputStream os;
	private InputStream is;
	private PrintWriter pw;
	private BufferedReader br;
	private DataOutputStream dos;
	private DataInputStream dis;
	
	public ImgHandler(Socket s) {
		
		this.incomingConnection = s;
		
		try {
			
			os = incomingConnection.getOutputStream();
			is = incomingConnection.getInputStream();
			pw = new PrintWriter(os);
			br = new BufferedReader(new InputStreamReader(is));
			dos = new DataOutputStream(os);
			dis = new DataInputStream(is);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void run() {
		
		System.out.println("Handling Client Requests");
		boolean processing = true;	
		
		try {
			
			while(processing) {
				
				String messageString = br.readLine();
				System.out.println("Message: " + messageString);
				StringTokenizer stringTokenizer = new StringTokenizer(messageString);
				String commandString = stringTokenizer.nextToken().toUpperCase();
				
				switch (commandString) {
				case "LIST": {
					
					pw.println(loadImgList());
					pw.flush();
					break;
					
				}
				case "DOWN": {
					
					String fileID = stringTokenizer.nextToken();
					System.out.println("ID requested: " + fileID);
					String fileName = "";
					
					File fileList = new File("data/server/ImgList.txt");
					Scanner scanner = new Scanner(fileList);
					String lineString = "";
					
					while (scanner.hasNext()) {
						lineString = scanner.nextLine();
						StringTokenizer stringTokenizer2 = new StringTokenizer(lineString);
						String idString = stringTokenizer2.nextToken();
						String fileNameString = stringTokenizer2.nextToken();
						
						if(idString.equals(fileID)) {
							fileName = fileNameString;
						}
						
					}
					
					scanner.close();
					System.out.println("Name of the requested file: " + fileName);
					File fileToReturn = new File("data/server/" + fileName);
					
					if(fileToReturn.exists()) {
						
						pw.println(fileToReturn.length());
						pw.flush();
						
						FileInputStream fis = new FileInputStream(fileToReturn);
						byte[] buffer = new byte[1024];
						int n = 0;
						
						while((n = fis.read(buffer)) > 0) {
							dos.write(buffer, 0, n);
							dos.flush();
						}
						
						fis.close();
						System.out.println("File sent to client.");
						
					}
					break;
					
				}
				case "UP": {
					
					// Command structure received: UPLOAD <ID> <NAME> <SIZE> <IMAGE>
					
					String fileRecID = stringTokenizer.nextToken();
					String fileRecName = stringTokenizer.nextToken();
					int size = Integer.parseInt(stringTokenizer.nextToken());
					
					PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter("./data/server/ImgList.txt", true)));
					printWriter.println(fileRecID + " " + fileRecName);
					printWriter.flush();
					printWriter.close();
					System.out.println("File appended to list");
					
					File fileToRec = new File("data/server/" + fileRecName);
					FileOutputStream fos = null;
					System.out.println("Still Receiving bytes from client...");
					
					try {
						
						fos = new FileOutputStream(fileToRec);
						byte[] buffer = new byte[1024];
						int n = 0;
						int totalBytes = 0;
						
						while((totalBytes != size)) {
							n = dis.read(buffer, 0, buffer.length);
							fos.write(buffer, 0, n);
							fos.flush();
							totalBytes += n;
						}
						
						pw.println("SUCCESS");
						pw.flush();
						System.out.println("DONE!! File uploaded to server.");
						 
					} catch (IOException e) {
						pw.println("FAILURE");
						pw.flush();
						e.printStackTrace();
					} finally {
						
						if(fos != null) {
							try {
								fos.close();
							} catch (IOException e2) {
								e2.printStackTrace();
							}
						}
						
					}
					break;
					
				}
				default:
					throw new IllegalArgumentException("Unexpected value: " + commandString);
				}
				
			}
			
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
	}
	
	// Retrieve file name from a given ID
	private String getFileNameFromID(String searchID)
	{
		String ret = "";
		File imglist = new File("data/server/ImgList.txt");
		try {
			
			Scanner scanner = new Scanner(imglist);
			String lineString = "";
			
			while(scanner.hasNext()) {
				lineString = scanner.nextLine();
				StringTokenizer stringTokenizer = new StringTokenizer(lineString);
				String idString = stringTokenizer.nextToken();
				String fileNameString = stringTokenizer.nextToken();
				
				if(idString.equals(searchID)) {
					ret = fileNameString;
				}
				
			}
			scanner.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
			
		return ret;
	}
	
	// Returning of image files list
	private String loadImgList() {
		String ret = "";
		try {
			
			Scanner scanner = new Scanner(new File(".data/server/ImgList.txt"));
			while (scanner.hasNext()) {
				String imgString = scanner.nextLine();
				ret += imgString + " # ";
			}
			
			System.out.println("Image list loaded");
			scanner.close();
			
		}catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		return ret; 
	}

}
