package no.mnemonic.receiverapplication;

import lombok.RequiredArgsConstructor;
import no.mnemonic.receiverapplication.repository.MessageRepository;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class InputListener {
    private static final Pattern receiverPattern = Pattern.compile("^@receiver([1-9][0-9]*)#history$");

    private final MessageRepository repository;

    public void listenForUserInput(int numberOfReceivers) {

        var in = new Scanner(System.in);
        String inputLine;

        while ((inputLine = in.nextLine()) != null) {
            processUserInput(inputLine.trim(), numberOfReceivers);
        }
    }

    protected void processUserInput(String line, int numberOfReceivers) {
        if (line.equals("exit")) {
            printReceiversSummary(numberOfReceivers);
            System.exit(0);
            return;
        }

        var targetMatcher = receiverPattern.matcher(line);
        if (targetMatcher.matches()) {
            var receiverId = Integer.parseInt(targetMatcher.group(1));

            if (receiverId > numberOfReceivers) {
                System.out.println("No receiver found for @receiver" + receiverId);
                return;
            }

            printReceiverHistory(receiverId);
            return;
        }

        System.out.println("invalid message");
    }

    private void printReceiverHistory(int receiverId) {
        var result = repository.findAllByReceiverId(receiverId);

        if (result.size() == 0) {
            System.out.printf("No messages found for @receiver%s%n", receiverId);
        } else {
            result.forEach(System.out::println);
        }
    }

    private void printReceiversSummary(int numberOfReceivers) {
        var result = repository.countReceiverMessagesByReceiverId();
        var summaries = new HashMap<Integer, Long>();
        result.forEach((summary) -> summaries.put((Integer) summary[0], (Long) summary[1]));

        for (var i = 1; i <= numberOfReceivers; i++) {
            System.out.printf("Receiver%s: %s message(s)%n", i, Objects.requireNonNullElse(summaries.get(i), 0));
        }
    }
}
