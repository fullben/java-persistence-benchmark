# Wholesale Supplier Java Persistence Benchmark (JPB)

Benchmark for comparing the performance of a [JPA](https://www.oracle.com/java/technologies/persistence-jsp.html) implementation with that of [MicroStream](https://microstream.one/platforms/microstream-for-java/), loosely based on the [TPC-C](http://www.tpc.org/tpcc/) benchmark.

## Structure

The JPB is based on the famous TPC-C benchmark. Like TPC-C, it models the activities of a(n old) wholesale supplier. This supplier has 100 000 products and a number of warehouses in which these products are stocked. Each warehouse has 10 districts, and an employee responsible for each district and its 3000 customers. The customers and employees of the supplier can execute certain tasks (*transactions*):

* Place a new order
* Perform a payment
* Request the delivery status of an order
* Update the delivery status of an order
* Check the stock levels of products at a warehouse

In order to model this scenario, the JPB has a server application (implemented using [Spring Boot](https://spring.io/projects/spring-boot)), which provides access to the data of the supplier and can execute the transactions described above.

Transactions can be simulated using the included JMeter project, which uses employee accounts to perform the transactions at a rate and probability similar to the specifications of the TPC-C benchmark by calling the corresponding API endpoints of the server application.

## Configuration

The main configuration properties of the server can be found in the `application.properties`, `application-prod.properties`, and `application-dev.properties` files. 

The server can be launched with one of the two following profiles (configurable via the `spring.profiles.active` property in the `application.properties` file):

* `prod`: Configures a [PostgreSQL](https://www.postgresql.org/) database as JPA data store.
* `dev`: Configures an in-memory H2 database as JPA data store.

The persistence layer of the server application is implemented both for JPA and MicroStream. Which implementation is to be utilized at runtime can be configured using the `application-dev.properties` and `application-prod.properties` files.

* `jpb.persistence.mode`: Set `jpa` to use JPA-based relational persistence or use `ms` to use MicroStream as persistence provider
* `jpb.model.warehouse-count`: Primary scaling factor of the data model, defines how many warehouses the wholesale supplier has. Must be a value greater than zero.
* `jpb.model.full-scale`: Secondary scaling factor of the data model, for development purposes only. Setting this to `false` reduces the amount of entities generated per warehouse.
* `jpb.jpa.*`: Configuration values of the JPA persistence implementation
* `jpb.ms.*`: Configuration values of the MicroStream persistence implementation

## Setup

This section provides the information necessary for setting up a development or production environment for the JPB.

### Development

For a local development setup, the following software is required:

* A Java IDE (e.g. [IntelliJ IDEA](https://www.jetbrains.com/idea/))
* [JMeter](https://jmeter.apache.org/) (tested with version 5.4.1)

Make sure that the `spring.profiles.active` property in the `application.yml` file is set to `dev`.

Use the IDE to launch the Java application locally. By default, the server will become available at `localhost:8080`. The API documentation will be available at `localhost:8080/swagger-ui.html`.

Once the server has been launched, you may start the JMeter test plan defined in the `clients/*.jmx` file found in this project. For actual performance evaluation, JMeter should be used in its CLI-mode. For this, one may use a command such as `jmeter -n -t jpb-terminals.jmx -l results.jtl`. This will execute the test defined in `jpb-terminals.jmx` and write the results to the `results.jtl` file.

### Deployment

The server application is meant to be deployed and run as a Docker container. The appropriate container build instructions are defined in the `Dockerfile` file found in the base directory of this repository. Depending on the persistence solution to be evaluated, one of the two provided *docker-compose* files must be utilized:

* `docker-compose.jpa.yml`: Creates a container for the server application (persistence mode will be set to JPA) and launches it after having started another container with a PostgreSQL database.
* `docker-compose.ms.yml`: Just creates a container for the server application (persistence mode will be set to MicroStream) and launches it, without creating any additional containers.

Deploying either variation of the benchmark can be achieved by calling the command `docker-compose -f YML-FILE up` in the root directory of this project, while replacing `YML-FILE` with either of the two compose file names.