package no.mnemonic.receiverapplication.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SenderMessage {
    private String senderId;
    private String receiverId;
    private String content;
}
