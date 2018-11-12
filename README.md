<!-- TODO logo -->
[![Travis CI](https://api.travis-ci.org/28Smiles/jdbi-modules.svg?branch=master)](https://travis-ci.org/28Smiles/jdbi-modules)
[![codecov](https://codecov.io/gh/28Smiles/jdbi-modules/branch/master/graph/badge.svg)](https://codecov.io/gh/28Smiles/jdbi-modules)
[![Licence](https://img.shields.io/badge/license-Apache-orange.svg)](LICENSE.txt)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.28Smiles/jdbi-modules/badge.svg)](https://mvnrepository.com/artifact/com.github.28Smiles/jdbi-modules)

Jdbi-Modules provides a well-tested facility of classes to implement modular SQL queries which allow for convenient access to the database.
It is built on top of [Jdbi](http://jdbi.org/).

## Getting started
### Prerequisites
This library supports Java 10 and 11.
The language level is still Java 10, but the Java 10 Worker was discontinued.

## Contributing
### Build
To build Jdbi-Modules, execute
```bash
$ gradlew assemble
```

### Tests
The tests utilize a Postgres database. If not provided with an external Postgres database, they will use [postgresql-embedded](https://github.com/yandex-qatools/postgresql-embedded).

## License
This project is licensed under the [Apache 2.0 license](https://www.apache.org/licenses/LICENSE-2.0.html).

## Special Thanks
- [Jdbi](http://jdbi.org/) for providing a well-written high-level JDBC wrapper
- [The Nabla e-Learning tool](https://nabla.algo.informatik.tu-darmstadt.de/)
- [dhardtke](https://github.com/dhardtke) for the idea of modular SQL
