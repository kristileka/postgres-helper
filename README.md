# Anorm Postgres Helper for Scala

This is a Postgres example that shows how you can use a generic way to do crud operations in anorm using plain generic
logics. You provide the Case classes for your scala objects and the reads/writes for it using an implicit
PostgresFormat[T]. After you give the regular Database object that your scala application has, the Stores implement
the basic crud operations save/update/find/get/remove etc.. With you doing the changes meaning more control
over how you want to use it.

## Getting Started

Create your case classes for your object an implement such
as [Customer](/src/main/scala/com/kristileka/postgres/helper/samples/customer/Customer.scala) and define the
CustomerStore and DeepCustomerStore and implement the PostgresStore who has the default operations. This allows you
to have a generic way of handling the Database operations and you can add any specific logic that you like, while
keeping
the regular objects the same.

## Requirements

This is a simple example how to use that requires Java 11 and the libraries for scala that anorm/play-jdbc/postgres
which are:

```
    libraryDependencies ++= Seq(
      "com.typesafe.play"       %% "play-jdbc" % "2.8.19",
      "org.postgresql"          % "postgresql" % "42.5.4",
      "org.playframework.anorm" %% "anorm"     % "2.6.10",
    )
```