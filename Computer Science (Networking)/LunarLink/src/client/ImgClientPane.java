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

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ImgClientPane extends BorderPane {
	
	private Socket socket;
	private InputStream is;
	private OutputStream os;
	private PrintWriter pw;
	private BufferedReader br;
	private DataInputStream dis;
	private DataOutputStream dos;
	private String[] listData;
	private boolean isConnected = false;
	
	//Graphical User Interface:
	private Button btnConnect;
	private Button btnDisconnect;
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
	private Label lblStatus;
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
				isConnected = true;
				lblStatus.setText("Status: Connected ✓");
				lblStatus.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
				btnConnect.setDisable(true);
				btnDisconnect.setDisable(false);
				responseArea.appendText("Successfully connected to server!\n");
				
			} catch (IOException e1) {
				isConnected = false;
				lblStatus.setText("Status: Connection Failed");
				lblStatus.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
				responseArea.appendText("Failed to connect to server!\n");
				e1.printStackTrace();
			}
		});
		
		// Disconnect button handler
		btnDisconnect.setOnAction((e)->{
			try {
				if(socket != null) {
					socket.close();
				}
				isConnected = false;
				lblStatus.setText("Status: Disconnected");
				lblStatus.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
				btnConnect.setDisable(false);
				btnDisconnect.setDisable(true);
				responseArea.appendText("Disconnected from server.\n");
				listArea.clear();
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
			pw.println("DOWN " + idToRetrieve);
			pw.flush();
			String responseString = "";
			
			try {
				
				responseString = br.readLine();
				int fileSize = Integer.parseInt(responseString);
				responseArea.appendText("File Received Size: " + responseString);
				
				// Find matching file name from list
				for(String s: listData) {
					StringTokenizer stringTokenizer = new StringTokenizer(s);
					String idString = stringTokenizer.nextToken();
					String nameString = stringTokenizer.nextToken();
					
					if(idString.equals(txtIDToRetrieve.getText())) {
						fileToGetName = nameString;
						break;
					}
				}
				
				// Create file with correct path
				File fileDownloaded = new File("data/client/" + fileToGetName);
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
				
				fos.close();
				System.out.println("File saved on client side");
				
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			
		});
		
		// Uploading an image file
		btnUpload.setOnAction((e)->{
			
			FileChooser fileChooser = new FileChooser();
			File selectedFile = fileChooser.showOpenDialog(stage);
			
			// Check if user cancelled the file chooser
			if(selectedFile == null) {
				responseArea.appendText("File selection cancelled.\n");
				return;
			}
			
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
			
			if(fileToGetName.isEmpty()) {
				responseArea.appendText("No image downloaded yet.\n");
				return;
			}
			
			File imageFile = new File("data/client/" + fileToGetName);
			try {
				Image image = new Image(imageFile.toURI().toString());
				ImageView imageView = new ImageView();
				imageView.setImage(image);
				imageView.setFitWidth(600);
				imageView.setFitHeight(450);
				imageView.setPreserveRatio(true);
				
				VBox imagePanel = new VBox(10);
				imagePanel.setAlignment(Pos.CENTER);
				imagePanel.setPadding(new Insets(15));
				imagePanel.setStyle("-fx-border-color: #bdc3c7; -fx-border-radius: 3; -fx-background-color: white;");
				
				Label imgTitle = new Label("Downloaded Image: " + fileToGetName);
				imgTitle.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");
				
				ScrollPane scrollPane = new ScrollPane(imageView);
				scrollPane.setStyle("-fx-control-inner-background: #ecf0f1;");
				VBox.setVgrow(scrollPane, Priority.ALWAYS);
				
				imagePanel.getChildren().addAll(imgTitle, scrollPane);
				setBottom(imagePanel);
				responseArea.appendText("Image displayed successfully!\n");
			} catch(Exception ex) {
				responseArea.appendText("Error loading image: " + ex.getMessage() + "\n");
				ex.printStackTrace();
			}

		});
	}
	
	//User interface setup
	private void setupUI()
	{
		setPadding(new Insets(15));
		setStyle("-fx-background-color: #f5f5f5;");
		
		// ===== TOP SECTION: Connection Controls =====
		HBox topPanel = new HBox(10);
		topPanel.setAlignment(Pos.CENTER_LEFT);
		topPanel.setPadding(new Insets(10));
		topPanel.setStyle("-fx-background-color: #2c3e50; -fx-border-radius: 5;");
		
		btnConnect = new Button("Connect");
		btnConnect.setPrefWidth(100);
		btnConnect.setStyle("-fx-font-size: 11; -fx-padding: 8;");
		
		btnDisconnect = new Button("Disconnect");
		btnDisconnect.setPrefWidth(100);
		btnDisconnect.setStyle("-fx-font-size: 11; -fx-padding: 8;");
		btnDisconnect.setDisable(true);
		
		lblStatus = new Label("Status: Disconnected");
		lblStatus.setStyle("-fx-text-fill: orange; -fx-font-size: 12; -fx-font-weight: bold;");
		
		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);
		
		topPanel.getChildren().addAll(btnConnect, btnDisconnect, spacer, lblStatus);
		
		// ===== MIDDLE SECTION: Download and List Controls =====
		VBox middlePanel = new VBox(10);
		middlePanel.setPadding(new Insets(10));
		middlePanel.setStyle("-fx-border-color: #bdc3c7; -fx-border-radius: 3; -fx-background-color: white;");
		
		// Pull button section
		HBox pullSection = new HBox(10);
		pullSection.setAlignment(Pos.CENTER_LEFT);
		btnPULL = new Button("Show Downloadable Images");
		btnPULL.setPrefWidth(200);
		btnPULL.setStyle("-fx-font-size: 11; -fx-padding: 8;");
		pullSection.getChildren().add(btnPULL);
		
		middlePanel.getChildren().add(pullSection);
		
		// List area section
		lblList = new Label("Available Images:");
		lblList.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");
		listArea = new TextArea();
		listArea.setPrefHeight(120);
		listArea.setStyle("-fx-control-inner-background: #ecf0f1; -fx-font-family: 'Courier New';");
		listArea.setWrapText(true);
		listArea.setEditable(false);
		
		middlePanel.getChildren().addAll(lblList, listArea);
		
		// Download controls section
		HBox downloadSection = new HBox(10);
		downloadSection.setAlignment(Pos.CENTER_LEFT);
		downloadSection.setPadding(new Insets(5, 0, 0, 0));
		
		lblID = new Label("File ID:");
		lblID.setStyle("-fx-font-size: 11; -fx-font-weight: bold;");
		txtIDToRetrieve = new TextField();
		txtIDToRetrieve.setPrefWidth(60);
		txtIDToRetrieve.setStyle("-fx-font-size: 11;");
		
		btnDownload = new Button("Download Image");
		btnDownload.setStyle("-fx-font-size: 11; -fx-padding: 6;");
		
		btnUpload = new Button("Upload Image");
		btnUpload.setStyle("-fx-font-size: 11; -fx-padding: 6;");
		
		downloadSection.getChildren().addAll(lblID, txtIDToRetrieve, btnDownload, btnUpload);
		middlePanel.getChildren().add(downloadSection);
		
		// ===== RESPONSE SECTION =====
		VBox responsePanel = new VBox(5);
		responsePanel.setPadding(new Insets(10));
		responsePanel.setStyle("-fx-border-color: #bdc3c7; -fx-border-radius: 3; -fx-background-color: white;");
		
		lblResponse = new Label("Server Response:");
		lblResponse.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");
		responseArea = new TextArea();
		responseArea.setPrefHeight(100);
		responseArea.setStyle("-fx-control-inner-background: #ecf0f1; -fx-font-family: 'Courier New';");
		responseArea.setWrapText(true);
		responseArea.setEditable(false);
		
		responsePanel.getChildren().addAll(lblResponse, responseArea);
		
		// ===== DISPLAY AND MAIN LAYOUT =====
		btnDisplay = new Button("Display Downloaded Image");
		btnDisplay.setPrefWidth(Double.MAX_VALUE);
		btnDisplay.setStyle("-fx-font-size: 12; -fx-padding: 10; -fx-font-weight: bold;");
		
		// Main content area
		VBox mainContent = new VBox(10);
		mainContent.setStyle("-fx-spacing: 10;");
		mainContent.getChildren().addAll(middlePanel, responsePanel, btnDisplay);
		
		// Set center
		setTop(topPanel);
		setCenter(mainContent);
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
