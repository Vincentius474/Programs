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

The client provides a JavaFX graphical user interface that allows users to:

- Select an image from their computer.
- Upload an image to the server.
- Request and view a list of available images.
- Enter an image ID.
- Retrieve the corresponding image from the server.
- Display the retrieved image within the application.

## Project Structure

```text
LunarLink/
├── bin/
│   └── # Compiled application files
│
├── docs/
│   ├── compile.bat
│   └── # Additional documentation
│
├── src/
│   └── # Java source code
│
├── data/
│   └── # Test image data
│
├── lib/
│   └── # Required external libraries
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
- The required JavaFX/library files configured for the project

## Running the Application

### 1. Compile

Use the supplied batch file to compile the project:

```text
docs/compile.bat
```

The compilation process should place compiled classes in the `bin` directory.

### 2. Start the Server

Start the server application first so that it can listen for incoming client connections on port `5432`.

### 3. Start the Client

Once the server is running, launch the client application. The JavaFX interface can then be used to:

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
