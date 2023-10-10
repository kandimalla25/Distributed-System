import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.stream.Collectors;

public class AddOperationClientImpl implements AddOperationClient {

    public void addCallback(int result) {
        System.out.println("Result received at client end : " + String.valueOf(result));
    }

}

