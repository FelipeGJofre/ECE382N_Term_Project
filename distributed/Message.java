package distributed;
public class Message {

    private final int srcPort;

    private final int destPort;
    
    private final String data;
    
    private final MessageTag tag;
    
    public enum MessageTag {
        TAG_0,
        TAG_1,
        TAG_2,
        TAG_3,
        TAG_4,
        TAG_5,
        TAG_6,
        TAG_7,
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

    public MessageTag getTag() {
        return tag;
    }

    public String getData() {
        return data;
    }

    public int getSrcPort() {
        return srcPort;
    }

    public int getDestPort() {
        return destPort;
    }
}
