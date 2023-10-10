import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.io.IOException;
import java.net.*;
import java.rmi.server.*;
import java.rmi.registry.*;

public class Server implements RMIServerSocketFactory {

    public Server() {
    }
   
    public static void main(String[] args) {
        try {
            int registryPort = 1099;
            int rmiPort = 1098;

            RMIClientSocketFactory csf = RMISocketFactory.getDefaultSocketFactory();
            RMIServerSocketFactory ssf = new Server();
            LocateRegistry.createRegistry(registryPort, csf, ssf);
            
            AddOperationServerImpl addOperationServerImpl = new AddOperationServerImpl();
            AddOperationServer addOperationServerStub = (AddOperationServer) UnicastRemoteObject.exportObject(addOperationServerImpl, rmiPort);
            Registry registry = LocateRegistry.getRegistry();
            registry.bind("AddOperationServer", addOperationServerStub);

            System.out.println("Server ready!");
        } catch (Exception e) {
            System.out.println("Exception: " + e.toString());
            e.printStackTrace();
        }
    }

    public ServerSocket createServerSocket(int port) throws IOException {
        return new ServerSocket(port, 0, InetAddress.getLocalHost());
    }

}
