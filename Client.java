import java.util.Scanner;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchService;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {

    public Client() {
    }

    public static void main(String[] args) {
        System.out.println("Client starting...");
        try {
            OperationsSupported operationToPerform = takeInput();
            System.out.println("Operation selected: " + operationToPerform.name());
            fileOperationHandler(operationToPerform);
        } catch (NumberFormatException numExc) {
            System.out.println("Operation is not valid! Please try again!");
        } catch (Exception e) {
            System.out.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    private static void fileOperationHandler(OperationsSupported operation) throws Exception {
        Scanner scannerObj = new Scanner(System.in);
        Registry registry = LocateRegistry.getRegistry(null);
        FileOperationsClientImpl fileOperationsClientImpl = new FileOperationsClientImpl();
        FileOperationsServer fileOperationsStub = (FileOperationsServer) registry.lookup("FileOperationsServer");

        switch (operation) {
            case UPLOAD:
                byte[] fileBytes = fileOperationsClientImpl.uploadFile();
                System.out.println("Enter the name you want to save the file on the server with? :");
                String nameFileToUpload = scannerObj.nextLine();
                fileOperationsStub.uploadFile(nameFileToUpload, fileBytes);
                break;

            case DOWNLOAD:
                System.out.println("Enter the name of the file you want to download from the server? :");
                String nameFileToDownload = scannerObj.nextLine();
                byte[] downloadedFileBytes = fileOperationsStub.downloadFile(nameFileToDownload);
                fileOperationsClientImpl.saveDownloadedFile(nameFileToDownload, downloadedFileBytes);
                break;

            case DELETE:
                System.out.println("Enter the name you of the file on the server you want to delete? :");
                String nameFileToDelete = scannerObj.nextLine();
                fileOperationsStub.deleteFile(nameFileToDelete);
                break;

            case RENAME:
                System.out.println("Enter the name you of the file on the server you want to rename? :");
                String nameFileToRename = scannerObj.nextLine();
                System.out.println("Enter the new name for the file? :");
                String newName = scannerObj.nextLine();
                fileOperationsStub.renameFile(nameFileToRename, newName);
                break;

            case SYNC_SERVER:
                System.out.println("Enter the name of the path on the client that you want to synchronize? :");
                String folderName = scannerObj.nextLine();
                Path pathToUpdate = Paths.get(folderName);
                if (pathToUpdate != null) {
                    WatchService watchService = pathToUpdate.getFileSystem().newWatchService();
                    FileWatcherService fileWatcherService = new FileWatcherService(watchService);
                    Thread updateWatcherThread = new Thread(fileWatcherService);
                    updateWatcherThread.start();
                    pathToUpdate.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
                    updateWatcherThread.join();
                }
                break;
        }
    }

    private static OperationsSupported takeInput() throws Exception {
        Scanner scannerObj = new Scanner(System.in);
        System.out.println("Enter the operation you want to perform:");
        System.out.println("1. UPLOAD");
        System.out.println("2. DOWNLOAD");
        System.out.println("3. DELETE");
        System.out.println("4. RENAME");
        System.out.println("5. RUN FILE CHANGE SYNC SERVER");
        System.out.println("\n");
        String operationToPerform = scannerObj.nextLine();
        Integer operationNum = Integer.parseInt(operationToPerform);
        switch (operationNum) {
            case 1:
                return OperationsSupported.UPLOAD;
            case 2:
                return OperationsSupported.DOWNLOAD;
            case 3:
                return OperationsSupported.DELETE;
            case 4:
                return OperationsSupported.RENAME;
            case 5:
                return OperationsSupported.SYNC_SERVER;
            default:
              throw new Exception("Operation is not valid!");
        }
    }

}