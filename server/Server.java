package server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(1099);
            FileTransferServiceImpl service = new FileTransferServiceImpl();
            
            // Matches Naming.lookup("rmi://" + serverIp + "/FileService")
            registry.rebind("FileService", service);
            
            System.out.println("========================================");
            System.out.println("🚀 Java RMI Unified Server is Live!");
            System.out.println("Listening on Port 1099...");
            System.out.println("========================================");
        } catch (Exception e) {
            System.err.println("Server Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
