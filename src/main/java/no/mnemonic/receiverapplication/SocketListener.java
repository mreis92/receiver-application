package no.mnemonic.receiverapplication;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import lombok.RequiredArgsConstructor;
import no.mnemonic.receiverapplication.domain.ReceiverMessage;
import no.mnemonic.receiverapplication.domain.SenderMessage;
import no.mnemonic.receiverapplication.domain.StatusMessage;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class SocketListener {
    private final Gson gson;
    private final Connection connection;
    private final Worker worker;

    private void sendResponse(PrintWriter out, boolean status) {
        var serializedMessage = gson.toJson(new StatusMessage(status));
        out.println(serializedMessage);
    }

    public Runnable createRunnable(final Socket clientSocket, final int numberOfWorkers) {
        return () -> {
            try {
                initializeWorkers(numberOfWorkers);

                var out = new PrintWriter(clientSocket.getOutputStream(), true);
                var in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                var channel = connection.createChannel();

                channel.exchangeDeclare("socket-messages", "topic");

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    var senderMessage = gson.fromJson(inputLine, SenderMessage.class);
                    var id = senderMessage.getReceiverId();

                    if (id.equals("all")) {
                        publishMessage(channel, senderMessage, "all");
                    } else {
                        var receiverId = Integer.parseInt(id);

                        if (receiverId > numberOfWorkers) {
                            sendResponse(out, false);
                            continue;
                        }
                        publishMessage(channel, senderMessage, "receiver" + id);
                    }
                    sendResponse(out, true);
                }

                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

    private void publishMessage(Channel channel, SenderMessage message, String routingKey) throws IOException {
        var receiverMessage = new ReceiverMessage(message.getSenderId(), message.getContent());
        var serializedMessage = gson.toJson(receiverMessage).getBytes(StandardCharsets.UTF_8);
        channel.basicPublish("socket-messages", routingKey, null, serializedMessage);
    }

    private void initializeWorkers(int numberOfWorkers) throws IOException {
        for (var id = 1; id <= numberOfWorkers; id++) {
            worker.createRunnable(id).run();
        }
    }
}