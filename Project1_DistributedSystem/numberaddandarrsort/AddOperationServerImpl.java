import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.stream.Collectors;
public class AddOperationServerImpl implements AddOperationServer {

    public int add(int i, int j) {
        int result = i + j;
        System.out.println("Result is : " + String.valueOf(result));
        return result;
    }

    public void addAsync(int i, int j) throws RemoteException, NotBoundException {
        AsyncAddService asyncAddService = new AsyncAddService(i, j);
        Thread asyncAddThread = new Thread(asyncAddService);
        asyncAddThread.start();
    }

    public List<Integer> sortList(List<Integer> numbersToSort) {
        return numbersToSort.stream().sorted().collect(Collectors.toList());
    }

    public void sortListAsync(List<Integer> numbersToSort) throws RemoteException, NotBoundException {
        AsyncSortService asyncSortService = new AsyncSortService(numbersToSort);
        Thread asyncSortThread = new Thread(asyncSortService);
        asyncSortThread.start();
    }

}

