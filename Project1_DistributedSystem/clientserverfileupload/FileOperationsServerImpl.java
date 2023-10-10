import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileInputStream;

public class FileOperationsServerImpl implements FileOperationsServer {

    private String basePath = "serverstore/";

    public String getBasePath() {
        return this.basePath;
    }
   
    public void uploadFile(String fileName, byte[] fileBytes) {
        System.out.println("Uploading file " + fileName + " on server...");
        try (FileOutputStream fos = new FileOutputStream(getBasePath() + fileName)) {
            fos.write(fileBytes);
            System.out.println("Uploaded file " + fileName + " on server!");
        } catch(FileNotFoundException fe) {
            System.out.println("Error uploading file " + fileName + " on server. Ex: " + fe.getMessage());
        } catch(IOException ioe) {
            System.out.println("Error uploading file " + fileName + " on server. Ex: " + ioe.getMessage());
        }
    }

    public void deleteFile(String fileName) {
        System.out.println("Deleting file " + fileName + " on server...");
        String relativePath = getBasePath() + fileName;
        File file = new File(relativePath);
        if (file.delete()) {
            System.out.println("Deleted file " + fileName + " from server!");
        } else {
            System.out.println("Failed to delete the file " + fileName + " from server!");
        }
    }

    public byte[] downloadFile(String fileName) {
        System.out.println("Fetching file " + fileName + " on server...");
        String relativePath = getBasePath() + fileName;

        File file = new File(relativePath);
        FileInputStream fileInputStream = null;
        byte[] fileBytes = new byte[(int) file.length()];

        try {
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(fileBytes);
            fileInputStream.close();
        
            // for (int i = 0; i < fileBytes.length; i++) {
            //    System.out.print((char) fileBytes[i]);
            // }
        } catch (Exception e) {
           e.printStackTrace();
        }

        System.out.println("\nFetched file from server side...");
        return fileBytes;
    }

    public void renameFile(String fileName, String newName) {
        System.out.println("Renaming file " + fileName + " on server to " + newName);
        String relativePath = getBasePath() + fileName;

        File file = new File(relativePath);
        File renamedFile = new File(getBasePath() + newName);

        if (renamedFile.exists()) {
            System.out.println("File " + newName +  " already exists!");
            return;
        }

        boolean fileRenameSucceeded = file.renameTo(renamedFile);
        if (!fileRenameSucceeded) {
            System.out.println("File rename failed for file " + fileName + " on server!");
        }

    }

}

