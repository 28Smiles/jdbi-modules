<!-- TODO logo -->
[![Travis CI](https://api.travis-ci.org/28Smiles/jdbi-modules.svg?branch=master)](https://travis-ci.org/28Smiles/jdbi-modules)
[![codecov](https://codecov.io/gh/28Smiles/jdbi-modules/branch/master/graph/badge.svg)](https://codecov.io/gh/28Smiles/jdbi-modules)
[![Licence](https://img.shields.io/badge/license-Apache-orange.svg)](LICENSE.txt)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.28Smiles.jdbi-modules/core/badge.svg)](https://search.maven.org/search?q=g:com.github.28Smiles.jdbi-modules)

Jdbi-Modules provides a well-tested facility of classes to implement modular SQL queries which allow for convenient access to the database.
It is built on top of [Jdbi](http://jdbi.org/).

## Getting started
The Libary is build with gradle.

    $ gradle initialize

The tests use the Postgres database. If not provided with an external Postgres database, it will use [Embedded Postgres](https://github.com/yandex-qatools/postgresql-embedded).

## Prerequisites
The Libary supports Java 10 and 11.

## License
This project is licensed under the [Apache 2.0 license](https://www.apache.org/licenses/LICENSE-2.0.html).

## Special Thanks
- [Jdbi](http://jdbi.org/), for providing such a good leightweight interface in Java to Sql databases
- [The Nabla e-Learning tool](https://nabla.algo.informatik.tu-darmstadt.de/)
- [dhardtke](https://github.com/dhardtke), for the idea of Sql-Modules
