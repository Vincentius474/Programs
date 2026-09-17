package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.StringTokenizer;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class HealthClientPane extends GridPane {
    private Socket clientSocket;
    private DataInputStream input;
    private DataOutputStream output;
    private String[] listData = new String[0];

    private Label lblUser;
    private Label lblPass;
    private TextField txtUserName;
    private PasswordField txtPass;
    private Button btnConnect;
    private Button btnADMIT;
    private Button btnPATIENTLIST;
    private TextField txtID;
    private Button btnGETSCAN;
    private Button btnDISCHARGE;
    private TextArea textListArea;
    private TextArea textResponseArea;
    private ImageView scanPreview;

    public HealthClientPane() {
        setUI();
        btnConnect.setOnAction(event -> connect());
        btnADMIT.setOnAction(event -> admit());
        btnPATIENTLIST.setOnAction(event -> requestPatientList());
        btnGETSCAN.setOnAction(event -> downloadScan());
        btnDISCHARGE.setOnAction(event -> discharge());
    }

    private void connect() {
        if (connected()) {
            showResponse("HEALTHY Already connected");
            return;
        }
        try {
            clientSocket = new Socket("localhost", 2026);
            input = new DataInputStream(clientSocket.getInputStream());
            output = new DataOutputStream(clientSocket.getOutputStream());
            showResponse("HEALTHY Connected");
        } catch (IOException e) {
            showResponse("CRITICAL Unable to connect: " + e.getMessage());
            closeConnection();
        }
    }

    private void admit() {
        if (!connected()) {
            showResponse("CRITICAL Not connected");
            return;
        }
        try {
            sendLine("ADMIT " + txtUserName.getText().trim() + " " + txtPass.getText());
            showResponse(readLine());
        } catch (IOException e) {
            handleConnectionError(e);
        }
    }

    private void requestPatientList() {
        if (!connected()) {
            showResponse("CRITICAL Not connected");
            return;
        }
        try {
            sendLine("PATIENTLIST");
            String response = readLine();
            if (response != null && response.startsWith("CRITICAL")) {
                showResponse(response);
                return;
            }
            listData = response == null || response.isEmpty() ? new String[0] : response.split("#");
            textListArea.clear();
            for (String entry : listData) {
                textListArea.appendText(entry + System.lineSeparator());
            }
        } catch (IOException e) {
            handleConnectionError(e);
        }
    }

    private void downloadScan() {
        if (!connected()) {
            showResponse("CRITICAL Not connected");
            return;
        }
        String requestedId = txtID.getText().trim();
        if (requestedId.isEmpty()) {
            showResponse("CRITICAL File ID is required");
            return;
        }
        try {
            sendLine("GETSCAN " + requestedId);
            String response = readLine();
            if (response == null || !response.startsWith("HEALTHY ")) {
                showResponse(response == null ? "CRITICAL Connection closed" : response);
                return;
            }

            long size;
            try {
                size = Long.parseLong(response.substring("HEALTHY ".length()).trim());
            } catch (NumberFormatException e) {
                showResponse("CRITICAL Invalid file size from server");
                return;
            }

            String fileName = fileNameForId(requestedId);
            if (fileName == null) {
                showResponse("CRITICAL File ID is not in the patient list");
                return;
            }

            File fileToGet = new File("data/client", fileName);
            try (FileOutputStream fileOutput = new FileOutputStream(fileToGet)) {
                byte[] buffer = new byte[8192];
                long totalBytes = 0;
                while (totalBytes < size) {
                    int bytesRead = input.read(buffer, 0, (int) Math.min(buffer.length, size - totalBytes));
                    if (bytesRead == -1) {
                        throw new IOException("Connection closed during file transfer");
                    }
                    fileOutput.write(buffer, 0, bytesRead);
                    totalBytes += bytesRead;
                }
            }
            displayScan(fileToGet);
            showResponse(response + " Downloaded " + fileName);
        } catch (IOException e) {
            handleConnectionError(e);
        }
    }

    private void discharge() {
        if (!connected()) {
            showResponse("CRITICAL Not connected");
            return;
        }
        try {
            sendLine("DISCHARGE");
            showResponse(readLine());
        } catch (IOException e) {
            handleConnectionError(e);
        } finally {
            closeConnection();
        }
    }

    private String fileNameForId(String requestedId) {
        for (String entry : listData) {
            StringTokenizer tokens = new StringTokenizer(entry);
            if (tokens.countTokens() >= 2 && tokens.nextToken().equals(requestedId)) {
                return tokens.nextToken();
            }
        }
        return null;
    }

    private void sendLine(String message) throws IOException {
        output.write((message + "\n").getBytes(StandardCharsets.UTF_8));
        output.flush();
    }

    private String readLine() throws IOException {
        StringBuilder line = new StringBuilder();
        int nextByte;
        while ((nextByte = input.read()) != -1) {
            if (nextByte == '\n') {
                return line.toString();
            }
            if (nextByte != '\r') {
                line.append((char) nextByte);
            }
        }
        return line.length() == 0 ? null : line.toString();
    }

    private boolean connected() {
        return clientSocket != null && clientSocket.isConnected() && !clientSocket.isClosed();
    }

    private void showResponse(String response) {
        textResponseArea.appendText((response == null ? "CRITICAL Empty response" : response)
                + System.lineSeparator());
    }

    private void handleConnectionError(IOException exception) {
        showResponse("CRITICAL " + exception.getMessage());
        closeConnection();
    }

    private void displayScan(File scanFile) {
        Image image = new Image(scanFile.toURI().toString(), true);
        if (image.isError()) {
            showResponse("CRITICAL Downloaded file is not a valid image");
            return;
        }
        scanPreview.setImage(image);
    }

    private void closeConnection() {
        if (clientSocket != null) {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        clientSocket = null;
        input = null;
        output = null;
    }

    private void setUI() {
        setHgap(10);
        setVgap(10);
        setAlignment(Pos.CENTER);

        lblUser = new Label("Username:");
        lblPass = new Label("Password:");
        txtUserName = new TextField();
        txtPass = new PasswordField();
        btnConnect = new Button("Connect");
        btnADMIT = new Button("Log In");
        btnPATIENTLIST = new Button("Show List");
        txtID = new TextField();
        txtID.setPromptText("File ID");
        btnGETSCAN = new Button("Download File");
        btnDISCHARGE = new Button("Log Out");
        textListArea = new TextArea("List from Server:" + System.lineSeparator());
        textListArea.setPrefHeight(200);
        textResponseArea = new TextArea("Response Messages from Server:" + System.lineSeparator());
        textResponseArea.setPrefHeight(200);
        scanPreview = new ImageView();
        scanPreview.setFitWidth(360);
        scanPreview.setFitHeight(300);
        scanPreview.setPreserveRatio(true);
        scanPreview.setSmooth(true);

        add(btnConnect, 0, 0);
        add(lblUser, 1, 0);
        add(txtUserName, 2, 0);
        add(lblPass, 3, 0);
        add(txtPass, 4, 0);
        add(btnADMIT, 5, 0);
        add(btnPATIENTLIST, 0, 1);
        add(txtID, 1, 1);
        add(btnGETSCAN, 2, 1);
        add(btnDISCHARGE, 3, 1);
        add(textListArea, 0, 2, 4, 1);
        add(textResponseArea, 0, 3, 4, 1);
        add(scanPreview, 4, 2, 2, 2);
    }
}