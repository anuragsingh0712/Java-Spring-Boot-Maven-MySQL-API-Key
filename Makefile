.PHONY: build run test package clean docker-build docker-up docker-down

build:
	mvn compile -q

package:
	mvn package -DskipTests -q

run: package
	SERVER_PORT=$${SERVER_PORT:-26872} bash start.sh

test:
	mvn test -q

clean:
	mvn clean -q

docker-build:
	docker build -t gym-management-backend .

docker-up:
	docker compose up --build

docker-down:
	docker compose down
