# Receiver Application

The receiver application is a Maven-based project using Spring Boot, that listens on a socket and receives messages,
delegating the messages to the correct receiver and finally, print and persist the messages.

The receivers can consume messages from a queue, implemented with RabbitMQ.

# Assumptions

- It was not clear what values the `persistence` parameter supported, so it is assumed that it can receive a file
  location (e.g. ./test-db, ~/test-db), or a string that indicates the messages are to be stored in memory (
  i.e. `memory`).

# Dependencies

You will need Maven installed in your local machine to compile the application and run the tests.

Alternatively, you can run the application with the provided .jar file, and the following instruction (parameters can be
modified):

```
java -Xmx1G -jar receiver-application.jar 6666 10 memory
```

It is also necessary to have Docker installed, in order to run the docker-compose file.

# Setup

A makefile has been created to simplify the commands to be executed.

To launch the queue container:

```
make up
```

To stop the queue container:

```
make down
```

To compile:

```
make build
```

To run the application:

```
make run PORT=6666 RECEIVERS=10 PERSISTENCE=memory
```

To run the tests:

```
make test
```