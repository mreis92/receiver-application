package no.mnemonic.receiverapplication;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.ServerSocket;

@Service
@RequiredArgsConstructor
public class ReceiverServer {
    private final SocketListener socketListener;

    public void start(int port, int numberOfReceivers) {

        Runnable serverTask = () -> {
            try {
                var serverSocket = new ServerSocket(port);
                while (true) {
                    var clientSocket = serverSocket.accept();
                    socketListener.createRunnable(clientSocket, numberOfReceivers).run();
                }
            } catch (IOException e) {
                System.err.println("Unable to process client request");
                e.printStackTrace();
            }
        };

        var serverThread = new Thread(serverTask);
        serverThread.start();
    }
}
