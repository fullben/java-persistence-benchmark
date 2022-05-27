# Release Notes

All notable changes to this project will be documented in this file.

## 2.2.0 (27.05.2022)

- Fix a major performance bug of the delivery transaction in the JPA-based (PostgreSQL) implementation
- Upgrade Gradle to 7.4.2, update some dependencies and improve main build script

## 2.1.0 (02.01.2022)

- Rework security implementation
  - Define actual user roles, privileges, and mapping between these two concepts
  - Add the concept of a role to the employee model construct; every employee now has a single role
  - Improve the the `UserDetailsService` base class
  - Increase security granularity by securing the transaction API endpoints with a different privilege than the resource API endpoints
- Rework the transaction API
  - Use resource-based paths instead of transaction names
  - Improve request structure (combination of path parameters, request parameters, and request body content instead of sending everything as part of the body)
  - Add proper Swagger-based API documentation
  - Remove requirement for actual server implementations to add a copy+paste transaction controller implementation

## 2.0.1 (09.12.2021)

- Fix invalid environment property keys in Docker Compose files

## 2.0.0 (07.12.2021)

- Split the project into multiple modules in order to increase flexibility and extensibility of the benchmark:
  - `wss-commons`: Commonly used utilities
  - `wss-data-gen`: Generic data generator for creating a set of data for initial database population
  - `wss-server-core`: Defines the core features of the WSS benchmark server, including API, security, services, and data transfer objects
  - `wss-server-jpa-pg`: Actual implementation of the server using JPA-based persistence and a PostgreSQL DBMS
  - `wss-server-ms-jacis`: Actual implementation of the server using MicroStream-based persistence and JACIS for concurrent data access management
- Add a second MicroStream-based implementation using Java locks and synchronization features for concurrent data access (`wss-server-ms-sync`)
- Various other minor changes and improvements
- Improve configurability of JMeter script
- Improve documentation

## 1.0.0 (02.10.2021)
- Initial release of the benchmark
