# WebForge

**WebForge** is a lightweight, multi-threaded Java web server that implements core HTTP request handling using Java networking and socket programming. It allows web browsers to connect to a local server and retrieve HTML pages, images, and other supported resources.

## Overview

The application creates a server socket that listens on **port 4321** and accepts incoming browser connections. Each client connection is handled independently using a separate thread, allowing the server to process multiple requests concurrently.

The server follows the basic HTTP request/response model and returns appropriate HTTP status codes depending on whether a requested resource can be served.

## Features

- Multi-threaded client connection handling
- HTTP request processing
- Server socket listening on port `4321`
- Support for serving HTML pages
- Support for binary files such as images
- HTTP `200`, `404`, and `500` response handling
- Localhost browser testing
- Object-oriented Java structure
- JavaDoc-compatible documentation
- Optional HTML5/JavaScript video streaming functionality

## Supported Pages

| URL | Resource |
|---|---|
| `http://localhost:4321/Afrikaans` | `Afrikaans.html` |
| `http://localhost:4321/Zulu` | `Zulu.html` |
| `http://localhost:4321/ZuluWithImage` | `ZuluWithImage.html` |

## HTTP Response Codes

### 200 — OK

Returned when the requested resource exists and can be successfully served. Binary resources such as images must also be supported.

### 404 — Not Found

Returned when the requested page or resource cannot be found.

### 500 — Internal Server Error

Returned when an unexpected server-side error occurs while processing a request.

## Architecture

```text
Browser
   |
   | HTTP Request
   v
WebForge Server
   |
   | Accept connection
   v
Client Handler Thread
   |
   | Locate requested resource
   v
File / Resource
   |
   | HTTP Response
   v
Browser
```

Each incoming client connection is handled independently so multiple requests can be processed concurrently.

## Project Structure

```text
WebForge/
├── bin/
│   └── # Compiled Java classes
├── docs/
│   ├── compile.bat
│   └── # Additional documentation
├── src/
│   └── # Java source code and packages
├── data/
│   ├── Afrikaans.html
│   ├── Zulu.html
│   ├── ZuluWithImage.html
│   └── # Additional resources
├── lib/
│   └── # Optional libraries
└── README.md
```

## Requirements

- Java Development Kit (JDK)
- Java compiler (`javac`)
- Java Runtime Environment (`java`)
- A web browser such as Chrome, Firefox, Edge, or Opera
- Windows Command Prompt or another compatible terminal

## Running the Server

### 1. Compile

Use the provided batch file:

```text
docs/compile.bat
```

The compilation process should place compiled classes in the `bin` directory.

### 2. Start the Server

Run the server's main class after compilation. The server should listen on port `4321`.

### 3. Test Using a Browser

Open:

```text
http://localhost:4321/Afrikaans
```

Also test:

```text
http://localhost:4321/Zulu
```

and:

```text
http://localhost:4321/ZuluWithImage
```

To test a missing resource, use for example:

```text
http://localhost:4321/does-not-exist
```

The server should return `404 Not Found`.

## Multi-Threading

When a client connects:

1. The server accepts the client socket.
2. A new thread is created for the connection.
3. The thread reads the HTTP request.
4. The requested resource is identified.
5. An HTTP response is generated.
6. The resource is sent to the browser.
7. The connection is closed or managed according to the implementation.

This prevents one client request from unnecessarily blocking other clients.

## Binary File Support

Binary resources, including images, must be transmitted as raw bytes rather than processed as normal text. This prevents images and other binary resources from becoming corrupted during transmission.

## Bonus: Video Streaming

An optional extension can provide an HTML5 or JavaScript-based video streaming page with:

- Play and pause controls
- Video seeking
- An alternative video source URL
- A custom HTML video-player page

A separately supplied sample video should not be included in the final submission.

## Error Handling

The server should gracefully handle:

- Invalid HTTP requests
- Missing resources
- Invalid file paths
- File access errors
- Client connection failures
- Unexpected server exceptions
- Multiple simultaneous client requests

## Documentation

The source code should follow standard Java coding conventions and a clear object-oriented structure.

Classes and methods created or modified should include appropriate JavaDoc comments. Normal comments should be used where they improve readability and explain important implementation details.

Generated JavaDoc should not be included in the submission unless specifically required.

## Testing Checklist

- [ ] Server starts successfully
- [ ] Server listens on port `4321`
- [ ] Browser connects through `localhost`
- [ ] `/Afrikaans` serves the correct page
- [ ] `/Zulu` serves the correct page
- [ ] `/ZuluWithImage` serves the correct page
- [ ] Images are transmitted correctly
- [ ] Missing resources return `404`
- [ ] Server errors return `500` where appropriate
- [ ] Multiple clients can connect concurrently
- [ ] Project compiles without errors
- [ ] Required JavaDoc comments are present
- [ ] Batch file compiles and runs the application

## Purpose

WebForge demonstrates the fundamentals of building a web server from the ground up using Java, including TCP socket programming, HTTP request/response handling, multi-threaded networking, binary file transfer, HTTP status codes, browser-server communication, object-oriented design, and Java documentation.

## Author

Developed as a Java networking project.

---

**WebForge — A lightweight Java web server built from the ground up.**
