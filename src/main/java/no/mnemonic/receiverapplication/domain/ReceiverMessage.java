package no.mnemonic.receiverapplication.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Setter
@Getter
@Entity
@Table(name = "messages")
public class ReceiverMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int receiverId;
    private long timestamp;
    private String senderId;
    private String content;

    public ReceiverMessage() {

    }

    public ReceiverMessage(String senderId, String content) {
        this.senderId = senderId;
        this.content = content;
    }

    public ReceiverMessage(String senderId, int receiverId, String content, long timestamp) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return String.format("%s receiver%s received: %s", Instant.ofEpochMilli(timestamp).toString(), receiverId, content);
    }
}
