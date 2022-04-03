package no.mnemonic.receiverapplication;

import no.mnemonic.receiverapplication.domain.ReceiverMessage;
import no.mnemonic.receiverapplication.repository.MessageRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Instant;

import static com.github.stefanbirkner.systemlambda.SystemLambda.catchSystemExit;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class InputListenerTest {
    @Autowired
    private InputListener inputListener;

    @Autowired
    private MessageRepository repository;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    void textExitMessage() throws Exception {
        int statusCode = catchSystemExit(() -> {
            inputListener.processUserInput("exit", 1);
            assertThat(outContent.toString()).isEqualTo("Receiver1: 0 message(s)\n");
        });

        assertThat(statusCode).isEqualTo(0);
    }

    @Test
    void testMessageToReceiverSuccessNoMessages() {
        inputListener.processUserInput("@receiver1#history", 1);
        assertThat(outContent.toString()).isEqualTo("No messages found for @receiver1\n");
    }

    @Test
    void testMessageToReceiverSuccessWithMessages() {
        var currentTime = Instant.now().toEpochMilli();
        var newMessage = new ReceiverMessage("test", 1, "testMessage", currentTime);

        repository.save(newMessage);
        inputListener.processUserInput("@receiver1#history", 1);
        assertThat(outContent.toString()).isEqualTo(newMessage + "\n");
    }

    @Test
    void testMessageToReceiverDoesNotExist() {
        inputListener.processUserInput("@receiver2#history", 1);

        assertThat(outContent.toString()).isEqualTo("No receiver found for @receiver2\n");
    }
}
