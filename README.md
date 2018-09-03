# Spark Exasol Connector

[![Build Status](https://travis-ci.org/morazow/spark-exasol-connector.svg?branch=master)](https://travis-ci.org/morazow/spark-exasol-connector)
[![codecov](https://codecov.io/gh/morazow/spark-exasol-connector/branch/master/graph/badge.svg)](https://codecov.io/gh/morazow/spark-exasol-connector)

This is a connector library that supports an integration between
[Exasol][exasol] and [Apache Spark][spark]. Using this connector, users can
read/write data from/to Exasol using Spark.

* [Quick Start](#quick-start)
* [Build and Testing](#building-and-testing)
* [Usage](#usage)
* [Configuration](#configuration)

## Quick Start

Here is short quick start on how to use the connector.

Reading data from Exasol,

```scala
// This is Exasol SQL Syntax
val exasolQueryString = "SELECT * FROM MY_SCHEMA.MY_TABLE"

val df = sparkSession
     .read
     .format("exasol")
     .options("host", "localhost")
     .options("port", "8888")
     .options("username", "sys")
     .options("password", "exasol")
     .options("query", exasolQueryString)
     .load()

df.show(10, false)
```

For more examples you can check [docs/examples](docs/examples.md).

## Building and Testing

Clone the repository,

```bash
git clone https://github.com/EXASOL/spark-exasol-connector

cd spark-exasol-connector/
```

Compile,

```bash
./sbtx compile
```

Run unit tests,

```bash
./sbtx test
```

To run integration tests, a separate docker network should be created first,

```bash
docker network create -d bridge --subnet 192.168.0.0/24 --gateway 192.168.0.1 dockernet
```

then run,

```bash
./sbtx it:test
```

The integration tests requires [docker][docker],
[exasol/docker-db][exa-docker-db] and [testcontainers][testcontainers].

In order to create a bundled jar,

```bash
./sbtx assembly
```

This create a jar file under `target/` folder. The jar file can be used with
`spark-submit`, `spark-shell` or `pyspark` commands. For example,

```shell
spark-shell --jars /path/to/spark-exasol-connector.jar
```

## Usage

*TODO*: Add short description on how to use jar files via maven and sbt once the
publishing setup is decided.

## Configuration

*TODO*: Add short description on how to use or provide exasol parameters and
what they mean.

[exasol]: https://www.exasol.com/en/
[spark]: https://spark.apache.org/
[docker]: https://www.docker.com/
[exa-docker-db]: https://hub.docker.com/r/exasol/docker-db/
[testcontainers]: https://www.testcontainers.org/
