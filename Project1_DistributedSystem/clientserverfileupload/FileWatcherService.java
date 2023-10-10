import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.Path;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class FileWatcherService implements Runnable {

    private WatchService watchService;

    private FileOperationsClientImpl fileOperationsClient;

    private FileOperationsServer fileOperationsStub;

    private String basePath = "clientstore/";

    public FileWatcherService(WatchService watchService) throws RemoteException, NotBoundException {
        this.watchService = watchService;
        this.fileOperationsClient = new FileOperationsClientImpl();
        
        Registry registry = LocateRegistry.getRegistry(null);
        this.fileOperationsStub = (FileOperationsServer) registry.lookup("FileOperationsServer");
    }

    private FileOperationsClientImpl getFileOperationsClient() {
        return this.fileOperationsClient;
    }

    private FileOperationsServer getFileOperationsStub() {
        return this.fileOperationsStub;
    }

    private String getBasePath() {
        return this.basePath;
    }

    @Override
    public void run() {
        while (true) {
            try {
                WatchKey watchKey = watchService.take();
                for (WatchEvent event: watchKey.pollEvents()) {
                    eventHandler(event);
                }
                boolean reset = watchKey.reset();
                if (!reset) {
                    break;
                }
            } catch (Exception e) {
                System.out.println("Filewatcher service threw exception: " + e.getMessage());
            }
        }
    }

    private void eventHandler(WatchEvent event) throws RemoteException {
        String eventName = event.kind().name();
        Path fileNameInEvent = ((WatchEvent<Path>) event).context();
        if (eventName == StandardWatchEventKinds.ENTRY_CREATE.name()) {
            System.out.println("Detected a create event for file : " + fileNameInEvent);
            byte[] fileBytes = getFileOperationsClient().uploadFile(getBasePath() + fileNameInEvent.toString());
            getFileOperationsStub().uploadFile(fileNameInEvent.toString(), fileBytes);
        } else if (eventName == StandardWatchEventKinds.ENTRY_MODIFY.name()) {
            System.out.println("Detected an update event for file : " + fileNameInEvent);
            getFileOperationsStub().deleteFile(fileNameInEvent.toString());
            byte[] fileBytes = getFileOperationsClient().uploadFile(getBasePath() + fileNameInEvent.toString());
            getFileOperationsStub().uploadFile(fileNameInEvent.toString(), fileBytes);
        } else if (eventName == StandardWatchEventKinds.ENTRY_DELETE.name()) {
            System.out.println("Detected a delete event for file : " + fileNameInEvent);
            getFileOperationsStub().deleteFile(fileNameInEvent.toString());
        } else {
            System.out.println("Detected invalid event for file : " + fileNameInEvent);
        }
    }

}