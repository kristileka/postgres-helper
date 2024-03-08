package com.kristileka.postgres.helper.samples.customer

import com.kristileka.postgres.helper.store.Store
import com.kristileka.postgres.helper.store.postgres.PostgresStore
import com.kristileka.postgres.helper.store.postgres.format.PostgresFormat

import java.util.UUID
import scala.concurrent.{ ExecutionContext, Future }

/**
  * This class is a wrapper between the CustomerStore and the PostgresStore
  * which uses all the functions of the postgresStore generically
  * @param store The PostgresStore[Customer] that was given by the Previous class
  */
case class DeepCustomerStore(store: PostgresStore[Customer]) extends Store[Customer] {

  override def findById(id: UUID)(implicit
                                  postgresFormat: PostgresFormat[Customer],
                                  executionContext: ExecutionContext): Future[Either[String, Option[Customer]]] =
    store.findById(id)

  override def findAll()(implicit
                         postgresFormat: PostgresFormat[Customer],
                         executionContext: ExecutionContext): Future[Either[String, Seq[Customer]]] = store.findAll()

  override def removeById(id: UUID)(implicit
                                    postgresFormat: PostgresFormat[Customer],
                                    executionContext: ExecutionContext): Future[Either[String, Boolean]] =
    store.removeById(id)

  override def findWhere(parameters: Map[String, Any])(
      implicit postgresFormat: PostgresFormat[Customer],
      executionContext: ExecutionContext
  ): Future[Either[String, Seq[Customer]]] =
    store.findWhere(parameters)

  override def findOrWhere(parameters: Map[String, Any])(
      implicit
      postgresFormat: PostgresFormat[Customer],
      executionContext: ExecutionContext
  ): Future[Either[String, Seq[Customer]]] =
    store.findOrWhere(parameters)

  override def insert(product: Customer)(implicit
                                         postgresFormat: PostgresFormat[Customer],
                                         executionContext: ExecutionContext): Future[Either[String, Boolean]] =
    store.insert(product)

  override def update(id: UUID, product: Customer)(
      implicit
      postgresFormat: PostgresFormat[Customer],
      executionContext: ExecutionContext
  ): Future[Either[String, Boolean]] =
    store.update(id, product)
}
