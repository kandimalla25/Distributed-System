import java.util.Scanner;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchService;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RMISocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class Client {

    public Client() {
    }

    public static void main(String[] args) {
        try {

            int registryPort = 1101;
            int rmiPort = 1100;

            RMIClientSocketFactory csf = RMISocketFactory.getDefaultSocketFactory();
            RMIServerSocketFactory ssf = new Server();
            LocateRegistry.createRegistry(registryPort, csf, ssf);
        
            AddOperationClientImpl addOperationClientImpl = new AddOperationClientImpl();
            SortOperationClientImpl sortOperationClientImpl = new SortOperationClientImpl();
            AddOperationClient addOperationClientStub = (AddOperationClient) UnicastRemoteObject.exportObject(addOperationClientImpl, rmiPort);
            SortOperationClient sortOperationClientStub = (SortOperationClient) UnicastRemoteObject.exportObject(sortOperationClientImpl, rmiPort);
            Registry registry = LocateRegistry.getRegistry();
            registry.bind("AddOperationClient", addOperationClientStub);
            registry.bind("SortOperationClient", sortOperationClientStub);

            System.out.println("Client starting...");

            OperationsSupported operationToPerform = takeInput();
            System.out.println("Operation selected: " + operationToPerform.name());
            addOperationHandler(operationToPerform);
        } catch (NumberFormatException numExc) {
            System.out.println("Operation is not valid! Please try again!");
        } catch (Exception e) {
            System.out.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    private static void addOperationHandler(OperationsSupported operation) throws Exception {
        Scanner scannerObj = new Scanner(System.in);
        Registry registry = LocateRegistry.getRegistry(null);
        AddOperationServer addOperationStub = (AddOperationServer) registry.lookup("AddOperationServer");

        switch (operation) {
            case ADD_NUMBERS:
                System.out.println("Enter the first number you want to add:");
                String firstNumber = scannerObj.nextLine();
                System.out.println("Enter the second number you want to add:");
                String secondNumber = scannerObj.nextLine();
                Integer firstNum = Integer.parseInt(firstNumber);
                Integer secondNum = Integer.parseInt(secondNumber);
                System.out.println("Do you want the operation to be synchronous? 0 - Sync or 1 - Async:");
                String syncTypeStr = scannerObj.nextLine();
                Integer syncType = Integer.parseInt(syncTypeStr);
                if (syncType == 0) {
                    int result = addOperationStub.add(firstNum, secondNum);
                    System.out.println("Client got result as : " + result);
                    break;
                } else if (syncType == 1) {
                    addOperationStub.addAsync(firstNum, secondNum);
                    break;
                } else {
                    System.out.println("The operation is not supported!");
                    break;
                }

            case SORT_ARRAY:
                List<Integer> numbersToSort = new ArrayList();
                System.out.println("Enter the number of elements in array:");
                String numElements = scannerObj.nextLine();
                Integer numElem = Integer.parseInt(numElements);
                for (int i = 0; i < numElem; i++) {
                    System.out.println("Enter the number you want to add:");
                    String currentNumber = scannerObj.nextLine();
                    Integer currentInt = Integer.parseInt(currentNumber);
                    numbersToSort.add(currentInt);
                }
                
                System.out.println("Do you want the operation to be synchronous? 0 - Sync or 1 - Async:");
                String sortSyncTypeStr = scannerObj.nextLine();
                Integer sortSyncType = Integer.parseInt(sortSyncTypeStr);
                if (sortSyncType == 0) {
                    List<Integer> sortedList = addOperationStub.sortList(numbersToSort);
                    System.out.println("Sorted list is : " + sortedList);
                    break;
                } else if (sortSyncType == 1) {
                    addOperationStub.sortListAsync(numbersToSort);
                    break;
                } else {
                    System.out.println("The operation is not supported!");
                    break;
                }
        }
    }

    private static OperationsSupported takeInput() throws Exception {
        Scanner scannerObj = new Scanner(System.in);
        System.out.println("Enter the operation you want to perform:");
        System.out.println("1. ADD NUMBERS");
        System.out.println("2. SORT ARRAY");
        System.out.println("\n");
        String operationToPerform = scannerObj.nextLine();
        Integer operationNum = Integer.parseInt(operationToPerform);
        switch (operationNum) {
            case 1:
                return OperationsSupported.ADD_NUMBERS;
            case 2:
                return OperationsSupported.SORT_ARRAY;
            default:
              throw new Exception("Operation is not valid!");
        }
    }

    public ServerSocket createServerSocket(int port) throws IOException {
        return new ServerSocket(port, 0, InetAddress.getLocalHost());
    }

}