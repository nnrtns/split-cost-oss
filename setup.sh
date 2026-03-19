## before running this file run the docker daemon
#docker run --name expense_splitter_database \
#  -e POSTGRES_PASSWORD=mysecretpassword \
#  -p 5432:5432 \
#  -d postgres:18.3
#
#docker exec -i expense_splitter_database psql -U postgres -c "CREATE DATABASE expense_splitter;"
#docker exec -i expense_splitter_database psql -U postgres -d expense_splitter < schema.sql
#docker compose down -v
#docker compose up -d

#!/bin/zsh
set -euo pipefail

echo "Starting database..."
docker compose up -d db

echo "Waiting for database healthcheck..."
until [ "$(docker inspect -f '{{if .State.Health}}{{.State.Health.Status}}{{else}}starting{{end}}' expense_splitter_database)" = "healthy" ]; do
  sleep 2
done

echo "Running schema.sql..."
docker compose run --rm db-init

echo "Building standalone..."
echo "Building webservice..."
docker compose run --rm builder

echo "Building service image..."
docker build -t expense-splitter-image ./expense-splitter

echo "Starting service container..."
docker compose up -d app

echo "Done."
docker ps