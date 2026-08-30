# LunarLink

**LunarLink** is a Java-based client-server training network system designed for managing simulated lunar surface imagery. It allows users to upload training images to a central server, view the available image catalogue, and retrieve individual images for analysis.

The application consists of two main components:

- **Server** — Accepts client connections, manages image storage, maintains an image catalogue, and processes client requests.
- **Client** — Provides a user-friendly JavaFX graphical interface for uploading, listing, retrieving, and displaying images.

## Features

### Server

The server provides the following functionality:

- Runs on **port 5432**.
- Maintains a text-based image catalogue containing image IDs and file names.
- Supports multiple client connections.
- Handles the following requests:
  - `LIST` — Returns the available image IDs and file names.
  - `DOWN <ID>` — Retrieves an image associated with the specified ID.
  - `UP <ID> <Name> <Size> <Image>` — Uploads an image to the server and updates the image catalogue.
- Returns `SUCCESS` or `FAILURE` for upload operations.
- Supports simultaneous client requests.

### Client

The client provides a modern and intuitive JavaFX graphical user interface with the following features:

- **Connection Management** — Easily connect to or disconnect from the server with visual status indicators.
- **Status Display** — Real-time connection status (Disconnected/Connected/Failed) with color-coded feedback.
- **Image Upload** — Select and upload images from your computer to the server.
- **Image Browsing** — Request and view a list of available images on the server.
- **Image Retrieval** — Enter an image ID to download the corresponding image from the server.
- **Image Display** — View downloaded images directly within the application with smooth scrolling and scaling.
- **Server Feedback** — Dedicated response area showing real-time feedback from the server operations.

### User Interface Enhancements

The client features a professionally designed interface with:

- **Dark Header Bar** — Connection controls with connect/disconnect buttons and status indicator
- **Organized Panels** — Logically grouped sections for image listing, download controls, and server responses
- **Color-Coded Status** — Visual feedback (orange for disconnected, green for connected, red for failed)
- **Responsive Design** — Automatically adjusts layout to accommodate images and server responses
- **Error Handling** — Clear error messages and user guidance for failed operations

## Project Structure

```text
LunarLink/
├── bin/
│   ├── client/          # Compiled client classes
│   └── server/          # Compiled server classes
│
├── docs/
│   ├── 1-cleanBin.bat   # Clean build artifacts
│   ├── 2-buildServer.bat # Compile server
│   └── 3-buildClient.bat # Compile client
│
├── src/
│   ├── client/
│   │   ├── ImgClient.java       # Application entry point
│   │   └── ImgClientPane.java   # JavaFX UI and client logic
│   └── server/
│       ├── ImgServer.java       # Server entry point
│       └── ImgHandler.java      # Client request handler
│
├── data/
│   ├── client/  # Downloaded images stored here
│   └── server/
│       └── ImgList.txt # Image catalogue
│
└── README.md
```

> The `data` directory is intended for local testing. Test data supplied separately for assessment purposes should not be included when preparing the final submission if it is provided externally.

## Communication Protocol

LunarLink uses a simple text-based request protocol between the client and server.

### List Images

```text
LIST
```

The server responds with the available image IDs and file names.

### Download an Image

```text
DOWN <ID>
```

Example:

```text
DOWN 102
```

The server returns the image associated with ID `102`.

### Upload an Image

```text
UP <ID> <Name> <Size> <Image>
```

Example:

```text
UP 103 lunar_surface.png 24576 <image-data>
```

If the upload is successful:

```text
SUCCESS
```

Otherwise:

```text
FAILURE
```

## Requirements

To compile and run the application, you will need:

- Java Development Kit (JDK)
- JavaFX
- A Java compiler
- Windows Command Prompt or another compatible terminal
- The required JavaFX/libras to compile the project:

```text
docs/1-cleanBin.bat     # Clean previous build artifacts
docs/2-buildServer.bat  # Compile server components
docs/3-buildClient.bat  # Compile client components
```

The compilation process places compiled classes in the `bin` directory.

### 2. Start the Server

Start the server application first so that it can listen for incoming client connections on port `5432`:

```text
java -cp bin/server server.ImgServer
```

You should see output indicating the server is running and waiting for connections.

### 3. Start the Client

Once the server is running, launch the client application in a separate terminal:

```text
java -cp bin/client --module-path <path-to-javafx> --add-modules javafx.controls,javafx.fxml client.ImgClient
```

The JavaFX interface can then be used to:

1. Click **Connect** to establish a connection to the server.
2. Click **Show Downloadable Images** to view the available images.
3. Enter an image ID and click **Download Image** to retrieve it from the server.
4. Click **Display Downloaded Image** to view the image within the application.
5. Click **Upload Image** to add new images to the server.
6. Click **Disconnect** when finishednt application. The JavaFX interface can then be used to:

1. Connect to the server.
2. View the available images.
3. Select and upload an image.
4. Enter an image ID.
5. Retrieve and display the requested image.

## Image Catalogue

The server maintains a text file containing information about stored images.

A catalogue entry conceptually contains:

```text
<ID> <Image File Name>
```

For example:

```text
101 moon_surface.png
102 crater_training.jpg
103 terrain_map.png
```

The catalogue allows the server to associate each image with its unique ID.

## Concurrent Connections

The server is designed to handle simultaneous requests from multiple clients. Client connections should be processed independently so that one connected client does not unnecessarily prevent other clients from communicating with the server.

## Documentation

The project source code should contain appropriate JavaDoc comments for classes and methods, together with normal comments where they improve code readability and explain important implementation details.

Generated JavaDoc should not be included as part of the source submission unless specifically required.

## Error Handling

The application should gracefully handle situations such as:

- Invalid image IDs.
- Missing image files.
- Failed uploads.
- Invalid client requests.
- Incorrect image sizes.
- Connection failures.
- Multiple clients connecting at the same time.
- Server-side file access errors.

Where an upload cannot be completed successfully, the server should return:

```text
FAILURE
```

## Purpose

LunarLink demonstrates the implementation of a networked Java application in which a graphical client communicates with a server to transfer and manage binary image data.

## Recent Improvements

### Bug Fixes

- **Fixed File Path Issue** — Corrected path in `ImgHandler.loadImgList()` from `.data/` to `data/`
- **Fixed Download Command Parsing** — Added space between "DOWN" command and file ID in client
- **Fixed File Path Separator** — Corrected download file path from `data/client` to `data/client/`
- **Fixed Download Loop Logic** — Moved file operations outside the loop to prevent file overwriting
- **Added Null Checks** — Added validation for file chooser cancellation in upload handler

### UI/UX Enhancements

- **Disconnect Button** — Allows users to cleanly disconnect from the server
- **Status Indicator** — Real-time visual feedback of connection status (color-coded)
- **Modern Layout** — Reorganized with BorderPane for better structure and organization
- **Enhanced Image Display** — Images now display in a dedicated panel with scrolling and proper sizing
- **Better Error Messages** — User-friendly feedback for all operations
- **Professional Styling** — Improved colors, spacing, padding, and visual hierarchy
- **Larger Window** — Increased to 950x850 to accommodate all features comfortably

The project combines:

- Client-server networking
- Socket communication
- File handling
- Binary data transfer
- Concurrent request handling
- JavaFX GUI development
- JavaDoc and software documentation
- Error handling

## Author

Developed as a Java networking and GUI application project.

---

**LunarLink — Connecting training imagery for lunar exploration.**
