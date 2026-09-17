# HealthVault

**HealthVault** is a Java-based secure file-transfer client-server application that allows authenticated users to retrieve `.png` medical scan files from a central server repository.

The application implements the custom **HEALTH26** protocol and includes a multi-client server, authentication, command handling, file listing, scan retrieval, and a JavaFX graphical user interface.

## Overview

The system consists of:

- **Server** — Listens on port `2026` and handles multiple clients.
- **HealthHandler** — Processes commands, manages login, and transfers scan files.
- **Client** — Provides the starting point for the client application.
- **HealthClientPane** — Provides the JavaFX interface for the available commands.

Clients must authenticate before using other commands.

## Features

- Multi-threaded client handling
- User authentication
- HEALTH26 communication protocol
- Listing available `.png` files
- Retrieving scans by ID
- Returning file sizes before transfer
- Saving downloaded `.png` files
- JavaFX graphical user interface
- Error handling and connection cleanup

## Protocol Commands

### ADMIT

Authenticates a client:

```text
ADMIT <Name> <Password>
```

Example:

```text
ADMIT Mandla 999
```

Credentials are validated against users stored in `Users.txt`.

### PATIENTLIST

Returns the available `.png` files from `Scans.txt`:

```text
PATIENTLIST
```

### GETSCAN

Retrieves a scan using its ID:

```text
GETSCAN <ID>
```

The server validates the ID, returns the file size, and transfers the requested `.png` file.

Example successful response:

```text
HEALTHY <filesize>
```

### DISCHARGE

Logs the client out:

```text
DISCHARGE
```

## Server Responses

### HEALTHY

Indicates a successful command:

```text
HEALTHY <Message>
```

### CRITICAL

Indicates an unsuccessful command:

```text
CRITICAL <Message>
```

## Data Files

- `Users.txt` — Stores registered users and authentication details.
- `Scans.txt` — Stores available scan IDs and file names.
- `.png` files — Stored scan files available for retrieval.

## Project Structure

```text
HealthVault/
├── bin/
├── docs/
│   ├── cleanBin.bat
│   ├── buildServerMain.bat
│   └── buildClientMain.bat
├── src/
├── data/
│   ├── client/
│   └── server/
│       ├── Users.txt
│       ├── Scans.txt
│       └── PNG files
└── README.md
```

## Requirements

- Java Development Kit (JDK)
- Java compiler and runtime
- JavaFX library
- Compatible terminal or command prompt
- Required project data files

## Running the Application

### 1. Clean Compiled Files

```text
docs/cleanBin.bat
```

### 2. Build and Run the Server

```text
docs/buildServerMain.bat
```

The server should listen on port `2026`.

### 3. Build and Run the Client

```text
docs/buildClientMain.bat
```

The JavaFX client interface should open.

### 4. Authenticate

Use the login controls to submit an `ADMIT` command.

### 5. List Scans

Use the `PATIENTLIST` button to display available scan files.

### 6. Retrieve a Scan

Enter a valid scan ID and use `GETSCAN`. The received file should be saved in the client data directory.

### 7. Log Out

Use `DISCHARGE` to end the client session.

## Multi-Client Handling

The server must accept multiple clients. Each connection should be passed to a `HealthHandler` running independently, typically through a separate thread.

## Error Handling

The application should handle:

- Invalid login credentials
- Commands issued before authentication
- Invalid scan IDs
- Missing files
- Invalid commands
- Connection failures
- File-transfer errors
- Input/output exceptions
- Client disconnections

Errors from the server must be displayed to the client through the GUI.

## JavaFX Interface

`HealthClientPane` should provide controls for:

- `ADMIT`
- `PATIENTLIST`
- `GETSCAN`
- `DISCHARGE`

The interface should display server responses and errors clearly.

## Documentation and Coding Standards

Use standard Java coding conventions, logical packages, meaningful names, object-oriented design, normal comments, and JavaDoc comments for relevant classes and methods.

## Testing Checklist

- [ ] Server listens on port `2026`
- [ ] Multiple clients can connect
- [ ] Clients are passed to `HealthHandler`
- [ ] `ADMIT` validates credentials
- [ ] Unauthenticated clients are restricted
- [ ] `PATIENTLIST` returns available scans
- [ ] `GETSCAN` validates IDs
- [ ] File sizes are returned
- [ ] `.png` files transfer correctly
- [ ] Downloaded files are saved
- [ ] `DISCHARGE` logs the client out
- [ ] JavaFX client starts
- [ ] All command buttons work
- [ ] Errors are displayed
- [ ] Exceptions are handled
- [ ] Connections are cleaned up
- [ ] Batch files compile and run the application

## Purpose

HealthVault demonstrates socket programming, multi-threaded networking, authentication, custom protocols, binary file transfer, JavaFX GUI development, and structured error handling.

---

**HealthVault — Securely connecting clients to essential scan files.**
