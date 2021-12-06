# Release Notes

All notable changes to this project will be documented in this file.

## Development

- Split the project into multiple modules in order to increase flexibility and extensibility of the benchmark:
  - `wss-commons`: Commonly used utilities
  - `wss-data-gen`: Generic data generator for creating an initial set of data for initial database population
  - `wss-server-core`: Defines the core features of the WSS benchmark server, including API, security, services, and data transfer objects
  - `wss-server-jpa-pg`: Actual implementation of the server using JPA-based persistence and a PostgreSQL DBMS
  - `wss-server-ms-jacis`: Actual implementation of the server using MicroStream-based persistence and JACIS for concurrent data access management
- Add a second MicroStream-based implementation using Java locks and synchronization features for concurrent data access (`wss-server-ms-sync`)
- Various other minor changes and improvements
- Improve configurability of JMeter script

## 1.0.0 (02.10.2021)
- Initial release of the benchmark
