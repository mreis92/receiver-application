package no.mnemonic.receiverapplication.configuration;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Configuration
public class RabbitMqConfig {

    @Bean
    public Connection connection() throws IOException, TimeoutException {
        try {
            var factory = new ConnectionFactory();
            factory.setHost("localhost");
            return factory.newConnection();
        } catch (IOException | TimeoutException e) {
            System.err.printf("Could not connect to the queue: %s%n", e.getMessage());
            throw e;
        }
    }
}
