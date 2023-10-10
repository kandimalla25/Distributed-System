import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface AddOperationServer extends Remote {

    int add(int i, int j) throws RemoteException;

    void addAsync(int i, int j) throws RemoteException, NotBoundException;

    List<Integer> sortList(List<Integer> listToSort) throws RemoteException;

    void sortListAsync(List<Integer> listToSort) throws RemoteException, NotBoundException;

}
