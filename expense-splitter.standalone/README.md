# Expense Splitter Standalone

## Context
- Trip: A trip is the business boundary within which participants, transactions, balances, and settlements are computed.
- Participant: An individual who is part of the trip and can either spend money or benefit from a transaction.
- Transaction: A record of money spent for a specific category, along with who paid and who benefited.
- Settlement: The computed output that tells each participant how much to pay another participant to clear balances.

## Actors
- Individuals involved in the trip.
- Integrating applications that want to reuse the expense-splitting engine without depending on Spring Boot, HTTP, persistence, or PostgreSQL.

## Available Features
- Create and work with the core domain model: `Trip`, `Participant`, and `Transaction`.
- Compute balances from transactions.
- Split expenses in a basic way so each debt is represented directly.
- Compute simplified settlements so the number of settlement payments is minimized.
- Select settlement behavior through settlement modes and the settler factory.

## What Belongs in the Standalone Module
The standalone module contains the pure business core of the system. It owns the domain model, settlement abstractions, settlement algorithms, and the logic required to transform transactions into balances and balances into debts. It does **not** know anything about REST APIs, controllers, request/response DTOs, Spring Boot, repositories, databases, or application deployment.

## Architectural Boundary
The standalone module is intentionally designed as the reusable computation engine for the entire project. The webservice depends on it, but the standalone module does not depend on the webservice. This keeps the settlement logic portable, testable, and stable. Any change to expense computation rules, balance calculation, settlement modes, or optimization logic should be made here first.

## Core Package Structure
- `com.split.trip`
  - Core domain entities such as `Trip` and `Participant`.
- `com.split.trip.accounts`
  - Transaction model, categories, share types, and balance-related concepts.
- `com.split.trip.accounts.settler`
  - Settlement contracts and implementations such as `BasicSettler` and `SimplifiedSettler`.
- `com.split.trip.accounts.settler.factory`
  - Factory used to choose the correct settler for a requested settlement mode.

## Design Highlights
- **Strategy Pattern**: `Settler` acts as the strategy interface, while `BasicSettler` and `SimplifiedSettler` provide alternate settlement behaviors.
- **Factory Pattern**: `SettlerFactory` centralizes selection of the correct settlement strategy.
- **Separation of Concerns**: The standalone module focuses only on business rules and leaves transport, validation DTOs, persistence, and orchestration to the webservice layer.
- **Extensibility**: New settlement modes can be introduced by adding a new `Settler` implementation and updating the factory, without changing callers.

## How the Webservice Uses It
The webservice collects input through REST APIs, validates and persists data, then delegates all core settlement computation to the standalone module. This means the standalone module remains the single source of truth for expense-sharing logic, while the webservice acts as an adapter around it.

## APIs
- The standalone module does not expose HTTP APIs by itself.
- It is consumed as a Java dependency by the webservice module.

## Design Documentations
- High-level architecture and service-level design documents are available in the project documentation set.
- This README describes the role of the standalone module specifically within that larger architecture.
