# Start With Me

This guide describes the exact way this project is started and torn down in this repository.

## Important note about script names
The repo uses `setup.sh` as the startup script and `teardown.sh` as the shutdown script. If you were expecting a file named `startup.sh`, use `setup.sh` instead.

## Folder layout assumed by this guide
After unzipping or cloning the repo, you should have this structure at the top level:

```text
developer-psd/
  setup.sh
  teardown.sh
  docker-compose.yml
  db/
    schema.sql
  expense-splitter/
  expense-splitter.standalone/
  documents/
    postman/
```

All commands below assume you are inside the project root:

```bash
cd developer-psd
```

## What this startup flow does
The repository startup flow is script-driven. Running `setup.sh` does all of the following:
1. Starts PostgreSQL through Docker Compose.
2. Waits for the database health check to pass.
3. Applies `db/schema.sql`.
4. Unit Tests and Builds the standalone module.
5. Unit Tests and Builds the Spring Boot webservice module.
6. Builds the webservice Docker image.
7. Starts the application container on port `8080`.

So you do **not** need to manually create the database, manually run the schema, or manually build the JARs if you are using this script.

## Prerequisites on a brand new machine
Install these first:
- Docker Desktop (or Docker Engine + Docker Compose support)
- Git or a way to copy the project onto the machine
- Node.js and npm (needed only for the automated Newman/Postman verification flow)
- Postman Desktop App (needed only if you want to import and inspect the collection visually)

## Step 1 — Start Docker
Before running the project scripts, make sure the Docker daemon is running.

On Docker Desktop, open Docker Desktop and wait until Docker shows as running.

## Step 2 — Start the application exactly the way this repo expects
From the project root:

```bash
cd developer-psd
chmod +x setup.sh teardown.sh
./setup.sh
```

Expected result:
- PostgreSQL container starts
- schema is applied
- Maven build runs in Docker
- application image is built
- application container starts
- API becomes available on:

```text
http://localhost:8080
```

## Step 3 — Verify containers are up
Run:

```bash
docker ps
```

You should see containers similar to:
- `expense_splitter_database`
- `expense_splitter_container`

## Step 4 — Quick manual API smoke check
You can quickly verify the application is reachable:

```bash
curl http://localhost:8080/trip/all
```

If the app is running properly, you should get an HTTP response from the service.

## Step 5 — Run the automated Postman/Newman API verification
The automated API verification must be run from:

```text
/developer-psd/documents/postman
```

So do this exactly:

```bash
cd developer-psd/documents/postman
npm install
BASE_URL=http://localhost:8080 bash run-newman.sh
```

What this does:
- uses the Postman collection inside `documents/postman/Postman/`
- creates a runtime environment
- runs the end-to-end API flow against your local backend
- produces reports in:

```text
documents/postman/reports/
```

### Expected report outputs
After a successful run, inspect:

```text
documents/postman/reports/junit/results.xml
documents/postman/reports/html/report.html
```

## Step 6 — Import the Postman files into Postman Desktop
This step is for users who want to browse the API flow and request documentation visually inside Postman.

Open Postman Desktop and do the following:

1. Click **Import**.
2. Import this collection file:
   ```text
   developer-psd/documents/postman/Postman/Expense Splitter - Automated E2E.postman_collection.json
   ```
3. Import this environment file:
   ```text
   developer-psd/documents/postman/Postman/Expense Splitter - Local.postman_environment.json
   ```
4. In Postman, select the imported environment.
5. Confirm `baseUrl` is set to:
   ```text
   http://localhost:8080
   ```
6. Open the collection and review the folders and requests.

## Step 7 — Run the collection from Postman UI
Once imported into Postman:

1. Select the **Expense Splitter - Local** environment.
2. Open the collection.
3. Start from the top of the collection.
4. Click **Run collection**.
5. Execute the collection against `http://localhost:8080`.

This lets you:
- see the request sequence
- inspect request/response payloads
- understand variable propagation
- use the collection as API documentation and verification

## How to use the imported Postman collection as API documentation
Inside Postman, the collection acts as executable documentation:
- each folder represents a functional flow
- each request shows the actual endpoint, method, body, and expected response shape
- the Docs section shows the request payloads and response payloads and their descriptions
- the request ordering shows how the system is expected to be used end to end
- the embedded tests show what the application considers a successful response

For this project, the most useful way to understand the API is:
1. read the request name
2. inspect the request body
3. send the request
4. inspect the response
5. look at the test tab / collection flow to see what is asserted next

## Standard startup sequence for a brand new machine
Here is the minimal exact flow to get productive fast:

```bash
cd developer-psd
./setup.sh
cd docs/postman
npm install
BASE_URL=http://localhost:8080 bash run-newman.sh
```

If that succeeds, the application is up and the API suite has verified the main flows.

## How to stop and clean up the application
From the project root:

```bash
cd developer-psd
./teardown.sh
```

This brings down the Docker Compose stack defined in `docker-compose.yml`.

## Troubleshooting

### `docker: command not found`
Install Docker first.

### Docker is installed but the scripts fail immediately
Make sure Docker daemon / Docker Desktop is running before invoking `./setup.sh`.

### `Permission denied` on scripts
Run:

```bash
chmod +x setup.sh teardown.sh
chmod +x docs/postman/run-newman.sh
```

### Postman/Newman run fails because `npm` is missing
Install Node.js and npm, then rerun:

```bash
cd developer-psd/docs/postman
npm install
BASE_URL=http://localhost:8080 bash run-newman.sh
```

### App starts but API verification fails
Check:
- `docker ps`
- app is listening on `http://localhost:8080`
- you ran Newman from `developer-psd/docs/postman`
- `BASE_URL` is set to `http://localhost:8080`

### Need to rerun from scratch
Use:

```bash
cd developer-psd
./teardown.sh
./setup.sh
cd docs/postman
BASE_URL=http://localhost:8080 bash run-newman.sh
```

## One-command mental model
- `./setup.sh` = bring up everything
- `BASE_URL=http://localhost:8080 bash run-newman.sh` from `/docs/postman` = verify everything
- `./teardown.sh` = bring it all down
