.PHONY=up down build run test

up:
	docker-compose up --remove-orphans --build -d

down:
	docker-compose down

build:
	mvn clean package

run:
	java -Xmx1G -jar -Dspring.profiles.active=default ./target/*.jar $(PORT) $(RECEIVERS) $(PERSISTENCE)

test:
	mvn verify -B

