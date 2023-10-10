import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.Path;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class AsyncAddService implements Runnable {

    private Integer firstNum;

    private Integer secondNum;

    private AddOperationClient addOperationClientStub;

    public AsyncAddService(Integer firstNum, Integer secondNum) throws RemoteException, NotBoundException {
        this.firstNum = firstNum;
        this.secondNum = secondNum;
        
        Registry registry = LocateRegistry.getRegistry(null);
        this.addOperationClientStub = (AddOperationClient) registry.lookup("AddOperationClient");
    }

    private Integer getFirstNum() {
        return this.firstNum;
    }

    private Integer getSecondNum() {
        return this.secondNum;
    }

    private AddOperationClient getAddOperationClientStub() {
        return this.addOperationClientStub;
    }

    @Override
    // TODO: Add some delay here - to make it look async
    public void run() {
        System.out.println("Running addition async with " + String.valueOf(getFirstNum()) + " and " + String.valueOf(getSecondNum()));
        int result = getFirstNum() + getSecondNum();
        try {
            getAddOperationClientStub().addCallback(result);
        } catch (Exception e) {
            System.out.println("Exception thrown: " + e.getMessage());
        }
    }

}