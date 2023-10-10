public class Message {
   
    private final Integer nodeId;

    private final String message;

    public Integer getNodeId() {
        return this.nodeId;
    }

    public String getMessage() {
        return this.message;
    }

    public Message(Integer nodeId, String message) {
        this.nodeId = nodeId;
        this.message = message;
    }
}
