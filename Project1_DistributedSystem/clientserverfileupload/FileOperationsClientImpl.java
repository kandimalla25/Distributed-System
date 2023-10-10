import java.io.File;
import java.io.FileOutputStream;
import java.util.Scanner;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileOperationsClientImpl implements FileOperationsClient {

    private String basePath = "clientstore/";

    public String getBasePath() {
        return this.basePath;
    }
    
    private byte[] uploadFileHelper(String pathToClientFile) {

        File file = new File(pathToClientFile);
        FileInputStream fileInputStream = null;
        byte[] fileBytes = new byte[(int) file.length()];

        try {
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(fileBytes);
            fileInputStream.close();
        
        } catch (Exception e) {
           e.printStackTrace();
        }

        System.out.println("\nUploading file from client side...");
        return fileBytes;
    }
    
    public byte[] uploadFile() {
        Scanner scannerObj = new Scanner(System.in);

        System.out.println("Enter the path to the client file you want to upload? :");
        System.out.println("\n");
        String pathToClientFile = scannerObj.nextLine();

        return uploadFileHelper(pathToClientFile);
    }
    
    public byte[] uploadFile(String pathToClientFile) {
        return uploadFileHelper(pathToClientFile);
    }

    public void saveDownloadedFile(String fileName, byte[] fileBytes) {
        System.out.println("Downloading file " + fileName + " on client...");
        try (FileOutputStream fos = new FileOutputStream(getBasePath() + fileName)) {
            fos.write(fileBytes);
            System.out.println("Downloaded file " + fileName + " on server!");
        } catch(FileNotFoundException fe) {
            System.out.println("Error downloading file " + fileName + " to client. Ex: " + fe.getMessage());
        } catch(IOException ioe) {
            System.out.println("Error downloading file " + fileName + " to client. Ex: " + ioe.getMessage());
        }
    }
}
