package server;

import streaming.FileTransferService;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileTransferServiceImpl extends UnicastRemoteObject implements FileTransferService {
    
    // Thread-safe storage maps for rooms and file data
    private final ConcurrentHashMap<String, List<String>> rooms = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, byte[]> roomFiles = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> roomFileNames = new ConcurrentHashMap<>();

    public FileTransferServiceImpl() throws RemoteException {
        super();
    }

    // --- ROOM MANAGEMENT METHODS ---

    @Override
    public synchronized String createRoom() throws RemoteException {
        String roomCode = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        rooms.put(roomCode, new ArrayList<>());
        System.out.println("RMI Server: Auto-generated Room [" + roomCode + "]");
        return roomCode;
    }

    @Override
    public synchronized void createRoom(String roomCode) throws RemoteException {
        rooms.put(roomCode, new ArrayList<>());
        System.out.println("RMI Server: Client-defined Room [" + roomCode + "] created.");
    }

    @Override
    public synchronized boolean joinRoom(String roomCode, String username) throws RemoteException {
        if (rooms.containsKey(roomCode)) {
            rooms.get(roomCode).add(username);
            System.out.println("RMI Server: User [" + username + "] joined room: " + roomCode);
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean joinRoom(String roomCode) throws RemoteException {
        if (rooms.containsKey(roomCode)) {
            rooms.get(roomCode).add("Anonymous");
            System.out.println("RMI Server: Anonymous client joined room: " + roomCode);
            return true;
        }
        return false;
    }

    // --- FILE TRANSFER METHODS ---

    @Override
    public void sendFile(String roomCode, String fileName, byte[] fileData) throws RemoteException {
        if (!rooms.containsKey(roomCode)) throw new RemoteException("Room does not exist.");
        roomFiles.put(roomCode, fileData);
        roomFileNames.put(roomCode, fileName);
        System.out.println("RMI Server: Stored file [" + fileName + "] in room " + roomCode);
    }

    @Override
    public void uploadFile(String roomCode, byte[] fileData, String fileName) throws RemoteException {
        // Alias method mapping directly to sendFile
        sendFile(roomCode, fileName, fileData);
    }

    @Override
    public byte[] receiveFile(String roomCode) throws RemoteException {
        if (!roomFiles.containsKey(roomCode)) throw new RemoteException("No file available in room: " + roomCode);
        return roomFiles.get(roomCode);
    }

    @Override
    public byte[] downloadFile(String roomCode) throws RemoteException {
        // Alias method mapping directly to receiveFile
        return receiveFile(roomCode);
    }

    @Override
    public String getLatestFileName(String roomCode) throws RemoteException {
        return roomFileNames.getOrDefault(roomCode, "unknown_file");
    }
}
