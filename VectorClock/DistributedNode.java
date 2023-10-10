// A Java program for a Client
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.DataOutputStream;
import java.io.IOException;
 
public class DistributedNode
{
    // initialize socket and input output streams
    private Integer numberOfNodes = -1;
    private Integer nodeId = -1;
    // initialized to the max size of the distributed node system that we're assuming is allowed
    private static AtomicIntegerArray atomicIntegerArray = new AtomicIntegerArray(10);
 
    // constructor to put ip address and port
    public DistributedNode(Integer numberOfNodes, Integer nodeId) {
        this.numberOfNodes = numberOfNodes;
        this.nodeId = nodeId;
    }

    // Ask the user for the server they'd want to send a message to & send them a message
    public void start() throws InterruptedException {
        for (int i = 0; i < atomicIntegerArray.length(); i++) {
            atomicIntegerArray.set(i, 0);
        }
        DistributedNodeReceiver distributedNodeReceiver = new DistributedNodeReceiver(this.nodeId, this.numberOfNodes);
        DistributedNodeSender distributedNodeSender = new DistributedNodeSender(this.nodeId, this.numberOfNodes);
        Thread serverThread = new Thread(distributedNodeReceiver);
        Thread clientThread = new Thread(distributedNodeSender);
        serverThread.start();
        clientThread.start();
        serverThread.join();
        clientThread.join();
    }
 
    public static void main(String args[]) {
        System.out.println("Starting a new node...");
        System.out.println("Enter the total number of nodes in the distributed system:");
        Scanner scannerObj = new Scanner(System.in);
        String numberOfNodesStr = scannerObj.nextLine();
        Integer numberOfNodes = Integer.parseInt(numberOfNodesStr);
        System.out.println("Enter the nodeId for node being initialized:");
        String nodeIdStr = scannerObj.nextLine();
        Integer nodeId = Integer.parseInt(nodeIdStr);
        // avoid multithreading issues - as scanner is not thread safe
        // scannerObj.close();
        try {
            DistributedNode node = new DistributedNode(numberOfNodes, nodeId);
            node.start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            System.out.println("Exiting program on node : " + nodeId);
        }
    }

    static class DistributedNodeReceiver implements Runnable {
   
        private Integer nodeIdOfServer = -1;
        private Integer numberOfNodes = -1;  // assuming this is smaller than the atomicIntegerArray
        private Integer portPrefixServer = 8090;
        private String fieldSeparator = "\\p{javaSpaceChar}\\|\\|\\p{javaSpaceChar}";
    
        private Integer getLocalServerNodePort(Integer nodeId) {
            return this.portPrefixServer + nodeId;
        }
    
        public DistributedNodeReceiver(Integer nodeIdOfServer, Integer numberOfNodes) {
            this.nodeIdOfServer = nodeIdOfServer;
            this.numberOfNodes = numberOfNodes;
        }
    
        private String getMessagePrefix() {
            return "Server-" + this.nodeIdOfServer + " : ";
        }

        private String getNodeClockStr() {
            String vectorClockStr = "";
            for (int i = 0; i < numberOfNodes; i++) {
                vectorClockStr = vectorClockStr + atomicIntegerArray.get(i) + ",";
            }
            vectorClockStr = vectorClockStr.substring(0, vectorClockStr.length()-1);
            return vectorClockStr;
        }

        private void printNodeClock() {
            System.out.println(getNodeClockStr());
        }

        private void updateNodeClock(List<Integer> vectorClock) {
            for (int i = 0; i < numberOfNodes; i++) {
                if (vectorClock.get(i) > atomicIntegerArray.get(i)) {
                    atomicIntegerArray.set(i, vectorClock.get(i));
                }
            }
            atomicIntegerArray.incrementAndGet(this.nodeIdOfServer);
        }

        private ParsedMessage parseMessage(String message) {
            String[] parsedString = message.split(this.fieldSeparator);
            Integer clientNodeId = Integer.parseInt(parsedString[0]);
            String clientMessage = parsedString[1];
            List<Integer> clientVectorClock = new ArrayList();
            String[] parsedVectorNum = parsedString[2].split(",");
            for (String num: parsedVectorNum) {
                clientVectorClock.add(Integer.parseInt(num));
            }
            return new ParsedMessage(clientNodeId, clientMessage, clientVectorClock);
        }
    
        protected void listenForMessages() throws Exception {
            Integer localServerPort = getLocalServerNodePort(this.nodeIdOfServer);
            ServerSocket serverSocket = new ServerSocket(localServerPort);
            System.out.println(getMessagePrefix() + "Started local server node on port : " + localServerPort);
            try {
                while (true) {
                    Socket socket = serverSocket.accept();
                    System.out.println(getMessagePrefix() + "Got message from client");
                    System.out.println(getMessagePrefix() + "Node clock before parsing response from remote client...");
                    printNodeClock();

                    DataInputStream input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                    // takes input from the client socket
                    String inputLine = input.readUTF();
                    ParsedMessage parsedMessage = parseMessage(inputLine);
                    System.out.println(getMessagePrefix() + "Received message from node " + parsedMessage.getNodeId() + " : " + parsedMessage.getMessage());
                    updateNodeClock(parsedMessage.getVectorClock());
                    printNodeClock();
                    socket.close();
                    input.close();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                serverSocket.close();
            }
        }
    
        // TODO: cleaner break if exception between client & server
        @Override
        public void run() {
            try {
                listenForMessages();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    static class DistributedNodeSender implements Runnable {
    
        private String address = "127.0.0.1";
        private Integer nodeId = -1;
        private Integer portPrefixServer = 8090;
        private Integer numberOfNodes = -1;  // assuming this is smaller than the atomicIntegerArray
        private String fieldSeparator = " || ";
    
        public DistributedNodeSender(Integer nodeId, Integer numberOfNodes) {
            this.nodeId = nodeId;
            this.numberOfNodes = numberOfNodes;
        }
    
        private String getMessagePrefix() {
            return "Client-" + this.nodeId + " : ";
        }
    
        private Integer getRemoteServerNodePort(Integer nodeId) {
            return this.portPrefixServer + nodeId;
        }

        private String getNodeClockStr() {
            String vectorClockStr = "";
            for (int i = 0; i < numberOfNodes; i++) {
                vectorClockStr = vectorClockStr + atomicIntegerArray.get(i) + ",";
            }
            vectorClockStr = vectorClockStr.substring(0, vectorClockStr.length()-1);
            return vectorClockStr;
        }

        private void printNodeClock() {
            System.out.println(getNodeClockStr());
        }

        private void updateNodeClock() {
            atomicIntegerArray.incrementAndGet(this.nodeId);
        }

        private String addMessageHeaders(String message) {
            String vectorClockStr = getNodeClockStr();
            return this.nodeId + this.fieldSeparator + message + this.fieldSeparator + vectorClockStr;
        }
    
        protected void sendMessageToClient(String message, Integer nodeIdOfServer) {
            try {
                Socket socket = new Socket(this.address, getRemoteServerNodePort(nodeIdOfServer));
                System.out.println(getMessagePrefix() + "Connected to remote server: " + nodeIdOfServer);
                System.out.println(getMessagePrefix() + "Node clock before sending to remote server: " + nodeIdOfServer);
                printNodeClock();
                updateNodeClock();
                System.out.println(getMessagePrefix() + "Node clock sent to remote server: " + nodeIdOfServer);
                printNodeClock();
                // write a message to the remote server
                DataOutputStream socketOutputStream = new DataOutputStream(socket.getOutputStream());
                socketOutputStream.writeUTF(addMessageHeaders(message));
                socketOutputStream.close();
                socket.close();
                System.out.println(getMessagePrefix() + "Sent message to remote server: " + nodeIdOfServer);
            } catch(IOException exception) {
                System.out.println(getMessagePrefix() + exception);
            }
        }
    
        protected Message takeInput() {
            Scanner scannerObj = new Scanner(System.in);
            System.out.println(getMessagePrefix() + "Enter the nodeId you want to send a message to:");
            String nodeIdStr = scannerObj.nextLine();
            Integer nodeId = Integer.parseInt(nodeIdStr);
            System.out.println(getMessagePrefix() + "Enter the message you want to send:");
            String message = scannerObj.nextLine();
            // scannerObj.close();
            return new Message(nodeId, message);
        }
    
        @Override
        public void run() {
            // hack - but easy solution to keep synchronization between client & server
            // wait for the server thread to start before prompting
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.exit(1);
            }
    
            while (true) {
                try {
                    Message inputMessage = takeInput();
                    sendMessageToClient(inputMessage.getMessage(), inputMessage.getNodeId());
                } catch(Exception e) {
                    System.out.println(e.getMessage());
                    break;
                }
            }
        }
    }

}