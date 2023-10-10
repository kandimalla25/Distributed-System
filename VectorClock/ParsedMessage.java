import java.util.List;

public class ParsedMessage {
   
    private final Integer nodeId;

    private final String message;

    private final List<Integer> vectorClock;

    public Integer getNodeId() {
        return this.nodeId;
    }

    public String getMessage() {
        return this.message;
    }

    public List<Integer> getVectorClock() {
        return this.vectorClock;
    }

    public ParsedMessage(Integer nodeId, String message, List<Integer> vectorClock) {
        this.nodeId = nodeId;
        this.message = message;
        this.vectorClock = vectorClock;
    }
}
