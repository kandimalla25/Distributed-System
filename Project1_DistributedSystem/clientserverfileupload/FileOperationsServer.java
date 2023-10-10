import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FileOperationsServer extends Remote {
    
    void uploadFile(String fileName, byte[] fileBytes) throws RemoteException;
    
    void deleteFile(String fileName) throws RemoteException;
    
    byte[] downloadFile(String fileName) throws RemoteException;

    void renameFile(String fileName, String newName) throws RemoteException;

}
