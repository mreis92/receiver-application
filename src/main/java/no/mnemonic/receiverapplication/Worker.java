package no.mnemonic.receiverapplication;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import lombok.RequiredArgsConstructor;
import no.mnemonic.receiverapplication.domain.ReceiverMessage;
import no.mnemonic.receiverapplication.repository.MessageRepository;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class Worker {
    private final MessageRepository repository;
    private final Connection connection;
    private final Gson gson;

    public Runnable createRunnable(final int receiverId) {
        return () -> {
            try {
                var channel = connection.createChannel();
                var queueName = channel.queueDeclare().getQueue();
                channel.queueBind(queueName, "socket-messages", "all");
                channel.queueBind(queueName, "socket-messages", "receiver" + receiverId);

                var consumer = new DefaultConsumer(channel) {
                    @Override
                    public void handleDelivery(String consumerTag,
                                               Envelope envelope,
                                               AMQP.BasicProperties properties,
                                               byte[] body) {
                        var currentTime = Instant.now().toEpochMilli();
                        var message = gson.fromJson(new String(body, StandardCharsets.UTF_8), ReceiverMessage.class);

                        message.setReceiverId(receiverId);
                        message.setTimestamp(currentTime);
                        repository.save(message);
                        System.out.println(message);
                    }
                };

                channel.basicConsume(queueName, true, consumer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }
}
