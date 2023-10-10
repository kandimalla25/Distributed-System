import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface SortOperationClient extends Remote {

    void sortCallback(List<Integer> result) throws RemoteException;

}
