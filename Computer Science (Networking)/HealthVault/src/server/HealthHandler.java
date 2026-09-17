package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

public class HealthHandler implements Runnable {
    private final Socket connectionToClientSocket;
    private final DataOutputStream output;
    private final DataInputStream input;
    private boolean authenticated;

    public HealthHandler(Socket connection) throws IOException {
        connectionToClientSocket = connection;
        output = new DataOutputStream(connection.getOutputStream());
        input = new DataInputStream(connection.getInputStream());
    }

    @Override
    public void run() {
        System.out.println("Start processing commands");
        try {
            String request;
            while ((request = readLine()) != null) {
                StringTokenizer tokens = new StringTokenizer(request);
                if (!tokens.hasMoreTokens()) {
                    sendLine("CRITICAL Empty command");
                    continue;
                }
                String command = tokens.nextToken();
                switch (command) {
                case "ADMIT":
                    if (tokens.countTokens() != 2) {
                        sendLine("CRITICAL Usage: ADMIT <Name> <Password>");
                    } else if (matchLogin(tokens.nextToken(), tokens.nextToken())) {
                        authenticated = true;
                        sendLine("HEALTHY logged in");
                    } else {
                        authenticated = false;
                        sendLine("CRITICAL Invalid credentials");
                    }
                    break;
                case "PATIENTLIST":
                    if (!authenticated) {
                        sendLine("CRITICAL Not Authenticated");
                    } else {
                        sendLine(String.join("#", getFileList()));
                    }
                    break;
                case "GETSCAN":
                    handleGetScan(tokens);
                    break;
                case "DISCHARGE":
                    if (!authenticated) {
                        sendLine("CRITICAL Not Authenticated");
                    } else {
                        authenticated = false;
                        sendLine("HEALTHY Logged out");
                        return;
                    }
                    break;
                default:
                    sendLine("CRITICAL Unknown command");
                    break;
                }
            }
        } catch (EOFException e) {
            // The client disconnected without sending DISCHARGE.
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                connectionToClientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleGetScan(StringTokenizer tokens) throws IOException {
        if (!authenticated) {
            sendLine("CRITICAL Not Authenticated");
            return;
        }
        if (tokens.countTokens() != 1) {
            sendLine("CRITICAL Usage: GETSCAN <ID>");
            return;
        }
        String fileName = idToFileName(tokens.nextToken());
        if (fileName == null) {
            sendLine("CRITICAL File ID does not exist");
            return;
        }
        File fileToSend = new File("data/server", fileName);
        if (!fileToSend.isFile()) {
            sendLine("CRITICAL File does not exist");
            return;
        }
        sendLine("HEALTHY " + fileToSend.length());
        try (FileInputStream fileInput = new FileInputStream(fileToSend)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fileInput.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            output.flush();
        }
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

    private boolean matchLogin(String userName, String password) {
        try (Scanner scan = new Scanner(new File("data/server/Users.txt"))) {
            while (scan.hasNextLine()) {
                String[] credentials = scan.nextLine().trim().split("\\s+");
                if (credentials.length >= 2 && credentials[0].equals(userName)
                        && credentials[1].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private ArrayList<String> getFileList() {
        ArrayList<String> result = new ArrayList<>();
        try (Scanner scan = new Scanner(new File("data/server/Scans.txt"))) {
            while (scan.hasNextLine()) {
                String line = scan.nextLine().trim();
                if (!line.isEmpty()) {
                    result.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private String idToFileName(String requestedId) {
        try (Scanner scan = new Scanner(new File("data/server/Scans.txt"))) {
            while (scan.hasNextLine()) {
                StringTokenizer tokens = new StringTokenizer(scan.nextLine());
                if (tokens.countTokens() >= 2 && tokens.nextToken().equals(requestedId)) {
                    return tokens.nextToken();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}