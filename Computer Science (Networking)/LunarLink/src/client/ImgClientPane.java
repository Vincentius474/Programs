package client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ImgClientPane extends GridPane {
	
	private Socket socket;
	private InputStream is;
	private OutputStream os;
	private PrintWriter pw;
	private BufferedReader br;
	private DataInputStream dis;
	private DataOutputStream dos;
	private String[] listData;
	
	//Graphical User Interface:
	private Button btnConnect;
	private Button btnPULL;
	private TextField txtIDToRetrieve;
	private Label lblID;
	private Button btnDownload;
	private Button btnUpload;
	TextArea listArea;
	private TextArea responseArea;
	private Label lblList;
	private Label lblResponse;
	private ImageView imgView;
	private Button btnDisplay;
	private String fileToGetName ="";
	public ImgClientPane(Stage stage) {
		setupUI();
		
		// Initialization
		btnConnect.setOnAction((e)->{
			try {
				
				socket = new Socket("localhost",5432);
				os = socket.getOutputStream();
				is = socket.getInputStream();
				br = new BufferedReader(new InputStreamReader(is));
				pw  =new PrintWriter(os);
				dis = new DataInputStream(is);
				dos = new DataOutputStream(os);
				
				
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
		
		// Requesting image list
		btnPULL.setOnAction((e)->{
			
			sendCommand(pw, "LIST");
			String responseString = "";
			responseString = readResponse(br);
			System.out.println(responseString);
			listData = responseString.split("#");
			
			for(int i=0; i<listData.length; i++) {
				listArea.appendText(listData[i] + "\n");
			}
			
		});
		
		// Retrieving image file
		btnDownload.setOnAction((e)->{
			
			int idToRetrieve = Integer.parseInt(txtIDToRetrieve.getText());
			pw.println("DOWN" + idToRetrieve);
			pw.flush();
			String responseString = "";
			
			try {
				
				responseString = br.readLine();
				int fileSize = Integer.parseInt(responseString);
				responseArea.appendText("File Received Size: " + responseString);
				
				for(String s: listData) {
					StringTokenizer stringTokenizer = new StringTokenizer(s);
					String idString = stringTokenizer.nextToken();
					String nameString = stringTokenizer.nextToken();
					
					if(idString.equals(txtIDToRetrieve.getText())) {
						fileToGetName = nameString;
					}
					
					File fileDownloaded = new File("data/client" + fileToGetName);
					FileOutputStream fos = new FileOutputStream(fileDownloaded);
					
					byte[] buffer = new byte[1024];
					int n = 0;
					int totalBytes = 0;
					
					while(totalBytes != fileSize) {
						n = dis.read(buffer, 0, buffer.length);
						fos.write(buffer, 0, n);
						fos.flush();
						totalBytes += n;
					}
					
					System.out.println("File saved on client side");
					
				}
				
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			
		});
		
		// Uploading an image file
		btnUpload.setOnAction((e)->{
			
			FileChooser fileChooser = new FileChooser();
			File selectedFile = fileChooser.showOpenDialog(stage);
			String fileName = selectedFile.getName();
			int fileID = listData.length + 1;
			pw.println("UP " + fileID + " " + fileName + " " + selectedFile.length());
			pw.flush();
			System.out.println("Upload command sent from client");
			
			FileInputStream fis;
			try {
				
				fis = new FileInputStream(selectedFile);
				byte[] buffer = new byte[1024];
				int n = 0;
				
				while ((n = fis.read(buffer)) > 0) {
					dos.write(buffer, 0, n);
					dos.flush();
				}
				
				fis.close();
				System.out.println("File sent for upload to server");
				String responseString = br.readLine();
				responseArea.appendText("Status of uploaded file: " + responseString);
				
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			
		});
		
		// Display of image file
		btnDisplay.setOnAction((e)->{
			
			Image image = new Image("file:data/client/" + fileToGetName);
			ImageView imageView = new ImageView();
			imageView.setImage(image);
			add(imageView, 0, 7, 4, 1);

		});
	}
	
	//User interface setup
	private void setupUI()
	{
		
		setHgap(10);
		setVgap(10);
		setAlignment(Pos.CENTER);
		btnConnect = new Button("Connect");
		btnPULL = new Button("Show Downloadable Images");
		txtIDToRetrieve = new TextField();
		lblID = new Label("File ID to retrieve: ");
		btnDownload = new Button("Download Image");
		btnUpload = new Button("Upload Image");
		listArea = new TextArea();
		listArea.setPrefHeight(50);
		responseArea = new TextArea();
		responseArea.setPrefHeight(50);
		lblList = new Label("List: ");
		lblResponse = new Label("Server Response: ");
		btnDisplay = new Button("Display Downloaded Image");
		
		add(btnConnect, 0, 0);
		add(btnPULL, 1, 0);
		add(lblID, 0, 1);
		add(txtIDToRetrieve, 1, 1);
		add(btnDownload, 2, 1);
		add(btnUpload, 3, 1);
		add(lblList, 0, 2);
		add(listArea, 0, 3, 4, 1);
		add(lblResponse, 0, 4);
		add(responseArea, 0, 5, 4, 1);
		add(btnDisplay, 0, 6, 4, 1);
		
	}
	
	// Read response
	private String readResponse(BufferedReader br)
	{
		String response = "";
		try {
			response= br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}
	
	// Send command
	private void sendCommand(PrintWriter pw, String msg)
	{
		pw.println(msg);
		pw.flush();
	}
	

}
