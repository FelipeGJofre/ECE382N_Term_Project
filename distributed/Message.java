package distributed;
public class Message {

    public final int srcPort;

    public final int destPort;
    
    public final String data;
    
    public final MessageTag tag;
    
    public enum MessageTag {
        REQUEST,
        REPLY,
        RELEASE
    };

    public Message(int srcPort, int destPort, MessageTag tag, String data) {
        this.srcPort = srcPort;
        this.destPort = destPort;
        this.data = data;
        this.tag = tag;
    }

    public static Message fromString(String message) {
        String[] parts = message.split(",");
        int srcPort = Integer.parseInt(parts[0]);
        int destPort = Integer.parseInt(parts[1]);
        MessageTag tag = MessageTag.valueOf(parts[2]);
        String data = parts[3];
        return new Message(srcPort, destPort, tag, data);
    }

    @Override
    public String toString() {
        return srcPort + "," + destPort + "," + tag + "," + data + "\r\n";
    }
}
