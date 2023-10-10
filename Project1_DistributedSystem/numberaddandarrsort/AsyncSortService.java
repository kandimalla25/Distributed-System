import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.Path;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.stream.Collectors;
import java.util.List;

public class AsyncSortService implements Runnable {

    private List<Integer> numbersToSort;

    private SortOperationClient sortOperationClientStub;

    public AsyncSortService(List<Integer> numbersToSort) throws RemoteException, NotBoundException {
        this.numbersToSort = numbersToSort;
        
        Registry registry = LocateRegistry.getRegistry(null);
        this.sortOperationClientStub = (SortOperationClient) registry.lookup("SortOperationClient");
    }

    private List<Integer> getNumbersToSort() {
        return this.numbersToSort;
    }

    private SortOperationClient getSortOperationClientStub() {
        return this.sortOperationClientStub;
    }

    @Override
    // TODO: Add some delay here - to make it look async
    public void run() {
        System.out.println("Running sorting async with " + String.valueOf(getNumbersToSort()));
        List<Integer> result = getNumbersToSort().stream().sorted().collect(Collectors.toList());
        try {
            getSortOperationClientStub().sortCallback(result);
        } catch (Exception e) {
            System.out.println("Exception thrown: " + e.getMessage());
        }
    }

}