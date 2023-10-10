import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FileOperationsClient extends Remote {
    
    byte[] uploadFile();

    void saveDownloadedFile(String fileNameToSave, byte[] fileBytes);

}
