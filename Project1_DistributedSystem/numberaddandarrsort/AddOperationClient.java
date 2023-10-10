import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface AddOperationClient extends Remote {

    void addCallback(int result) throws RemoteException;

}
