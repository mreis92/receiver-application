package no.mnemonic.receiverapplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ReceiverApplication {

    public static void main(String[] args) {
        var port = Integer.parseInt(args[0]);
        var NUMBER_OF_RECEIVERS = Integer.parseInt(args[1]);
        var persistence = args[2];

        initializeDatabase(persistence);
        var context = SpringApplication.run(ReceiverApplication.class, args);

        var receiverServer = context.getBean(ReceiverServer.class);
        receiverServer.start(port, NUMBER_OF_RECEIVERS);

        var inputListener = context.getBean(InputListener.class);
        inputListener.listenForUserInput(NUMBER_OF_RECEIVERS);
    }

    private static void initializeDatabase(String persistence) {
        var location = persistence.equals("memory") ? "jdbc:h2:mem:testdb" : "jdbc:h2:file:" + persistence;
        System.setProperty("spring.datasource.url", location + ";DATABASE_TO_UPPER=false");
    }
}
