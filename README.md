# Wholesale Supplier Java Persistence Benchmark (JPB)

Benchmark for comparing the performance of a [JPA-based](https://www.oracle.com/java/technologies/persistence-jsp.html) and [MicroStream-based](https://microstream.one/platforms/microstream-for-java/) persistence implementation, loosely based on the [TPC-C](http://www.tpc.org/tpcc/) benchmark.

## Structure

The benchmark is based on the famous TPC-C benchmark. Like TPC-C, it models the activities of a wholesale supplier. This supplier has 100 000 products and a number of warehouses in which these products are stocked. Each warehouse has 10 districts, and an employee responsible for each district and its 3000 customers. The customers and employees of the supplier can execute certain tasks (*transactions*):

* Place a new order (read-write)
* Perform a payment (read-write)
* Request the delivery status of an order (read)
* Update the delivery status of an order (read-write)
* Check the stock levels of products at a warehouse (read)

In order to model this scenario, the benchmark has a server application (implemented using [Spring Boot](https://spring.io/projects/spring-boot)), which provides access to the data of the supplier and can execute the transactions described above. This application is implemented in multiple 'flavors'. The core of the application resides in the `wss-server-core` module, while the actual implementations for each persistence mechanism reside in their own dedicated subproject (e.g., `wss-server-jpa-pg` for JPA-based persistence backed by a PostgreSQL database).

Transactions can be simulated using the included [JMeter](https://jmeter.apache.org/) project, which uses employee accounts to perform the transactions at a rate and probability similar to the specifications of the TPC-C benchmark by calling the corresponding API endpoints of the server application.

## Configuration

### Wholesale Supplier Server

The main configuration properties of the server can be found in the `application.properties`, `application-prod.properties`, and `application-dev.properties` files. 

The server can be launched with one of the two following profiles (configurable via the `spring.profiles.active` property in the `application.properties` file):

* `prod`: Configures a [PostgreSQL](https://www.postgresql.org/) database as JPA data store.
* `dev`: Configures an in-memory H2 database as JPA data store.

The persistence layer of the server application is implemented both for JPA and MicroStream. Which implementation is to be utilized at runtime can be configured using the `application-dev.properties` and `application-prod.properties` files.

* `jpb.persistence.mode`: Set `jpa` to use JPA-based relational persistence or use `ms` to use MicroStream as persistence provider.
* `jpb.model.initialize`: Whether the server should generate the model data at startup. `True` to indicate that the model should be generated, `false` for not generating any data.
* `jpb.model.warehouse-count`: Primary scaling factor of the data model, defines how many warehouses the wholesale supplier has. Must be a value greater than zero.
* `jpb.model.full-scale`: Secondary scaling factor of the data model, for development purposes only. Setting this to `false` reduces the amount of entities generated per warehouse.
* `jpb.jpa.*`: Configuration values of the JPA persistence implementation.
* `jpb.ms.*`: Configuration values of the MicroStream persistence implementation.

### Wholesale Supplier Clients

The main configuration properties for the clients simulated are located in the *User Defined Variables* of the set-up thread group.

* `server.url`: The url of the targeted server, excluding port and protocol, e.g. `localhost` for a server running on the same machine.
* `server.port`: The port of the targeted server.
* `server.protocol`: The protocol of the targeted server, usually either `http` or `https`.
* `employee.count`: The number of employees to be simulated.
* `work.duration`: The duration for which employee work will be simulated in milliseconds.

## Setup & Usage

This section provides the information necessary for setting up a development or production environment for the benchmark.

### Development

For a local development setup, the following software is required:

* A Java IDE (e.g. [IntelliJ IDEA](https://www.jetbrains.com/idea/))
* JMeter (tested with version 5.4.1)

Make sure that the `spring.profiles.active` property in the `application.properties` file is set to `dev`.

Use the IDE to launch the Java application locally. By default, the server will become available at `localhost:8080`. The API documentation will be available at `localhost:8080/swagger-ui.html`.

Once the server has been launched, you may start the JMeter test plan defined in the `wss-terminals/terminals.jmx` file found in this project.

Note that if using MicroStream persistence, you must delete the MicroStream storage folder created during each server run before performing any further runs.

### Deployment

The server application is meant to be deployed and run as a Docker container. The appropriate container build instructions are defined in the Docker files found in the base directory of this repository. Depending on the persistence solution to be evaluated, one of the provided *docker-compose* files must be utilized:

* `docker-compose.jpa.pg.yml`: Creates a container for the server application (persistence mode will be set to JPA) and launches it after having started another container with a PostgreSQL database.
* `docker-compose.ms.jacis.yml`: Creates a container just for the server application (persistence mode will be set to MicroStream) and launches it.

Deploying either variation of the benchmark can be achieved by calling the command `docker-compose -f YML-FILE up` in the root directory of this project, while replacing `YML-FILE` with either of the two compose file names.

### Scaling

The test implemented by this benchmark can be scaled as hinted at in the [configuration](#configuration) section. While the data model maintained by the server can be scaled using the server properties (namely the `jpb.model.warehouse-count` property), the JMeter threads must be scaled accordingly.

As each JMeter thread represents the transactions performed by a single employee, and as each district has one employee, and each warehouse has ten districts, there must be ten JMeter threads per warehouse. This value can be configured in the JMeter project itself, by adjusting the `employee.count` variable. Note that the threads each use their own distinct employee account, defined in the `wss-terminals/employees.csv` file. If the number of threads exceeds the number of employees defined in this file, errors may occur; alternatively you may append new employee lines following the pattern exposed by the existing credentials.

Each employee thread executes an initial setup followed by running randomly selected (non-uniform) transactions until the duration defined in the `work.duration` variable has been exceeded. Adjusting this value affects the overall duration of the test and amount of data generated.

### Making and Processing Measurements

As mentioned in the [structure](#structure) section, measurements are meant to be taken with JMeter. For this, first ensure that the server is running on this or some other machine and that it already has generated the configured data model and written it to persistent storage.

Make sure that JMeter is installed on your machine. Then, navigate to the folder of this project containing the JMeter sub-project, called `wss-terminals`. If the server is running on a remote computer, adjust the `server.` properties found in the *User Defined Variables* accordingly.

Open a terminal and execute the command `jmeter -Jsample_variables=processingNs -n -t terminals.jmx -l results.jtl` to run the test in JMeter's CLI-mode. This will execute the testplan defined in `terminals.jmx`. The results will be written as CSV data to the `results.jtl` file.

Once the test has been completed, you can use the `jmeter -g results.jtl -o ./report` command to automatically create a report from the test results. The report will be placed in the `report` directory. Be aware that the `jmeter.reportgenerator.exporter.html.series_filter` property in the `user.properties` file defines which requests will be considered for the report.

Note that when executing the JMeter tests, no resource intensive features such as *Result Trees*, *Debug Samplers*, *Listeners*, or *Summary Reports* should be active in the script.

Furthermore, if you have configured a large number of threads as described in the section [scaling](#scaling), you may have to adjust the amount of heap memory available to the JVM executing the JMeter script. This can be facilitated by modifying the appropriate JMeter file. This file is found at `PATH-TO-JMETER-INSTALLATION/bin/jmeter`. Open the file and find the line `: "${HEAP:="-Xms1g -Xmx1g -XX:MaxMetaspaceSize=256m"}"` and adjust the values of `-Xms` and `-Xmx`. For example, if you have 50 threads, a size of 4GB to 6GB is appropriate.

## Extensibility

This benchmark is extensible, meaning it is possible to add custom implementations for a persistence solution of your choice.

For this, simply create a new module. By convention, the module name should start with `wss-server-`, followed by a very short acronym (or multiple) identifying the persistence solution. For example, if you were to implement a JPA-based solution backed by a MySQL database management system, an appropriate module name would be `wss-server-jpa-mssql`.

The `build.gradle` of the module should start out similar to the following build file:

```groovy
plugins {
    id 'org.springframework.boot'
    id 'io.spring.dependency-management'
    id 'java'
    id 'application'
}

application {
    mainClass = 'de.uniba.dsg.wss.Application'
}

repositories {
    mavenCentral()
}

dependencies {
    implementation project(':wss-server-core')
    implementation project(':wss-data-gen')
    // Dependencies for your persistence solution here
}
```

The module itself should implement the application components defined in the `wss-server-core` module. For more information on what components and structures are necessary, consult the core module, or the two implementations provided with the original benchmark version (`wss-server-jpa-pg` and `wss-server-ms-jacis`).

Aside from the application implementation, you must also provide a `Dockerfile` and `docker-compose` file for enabling the execution of your implementation in a container environment. These files must be defined in the root directory of this project. Check the existing implementations there for further information. 
