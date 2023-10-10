import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.stream.Collectors;

public class SortOperationClientImpl implements SortOperationClient {

    public void sortCallback(List<Integer> result) {
        System.out.println("Result received at client end : " + String.valueOf(result));
    }

}

