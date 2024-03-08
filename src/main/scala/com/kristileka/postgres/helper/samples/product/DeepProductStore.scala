package com.kristileka.postgres.helper.samples.product

import com.kristileka.postgres.helper.store.Store
import com.kristileka.postgres.helper.store.postgres.PostgresStore
import com.kristileka.postgres.helper.store.postgres.format.PostgresFormat

import java.util.UUID
import scala.concurrent.{ ExecutionContext, Future }

/**
  * This class is a wrapper between the ProductStore and the PostgresStore
 * which uses all the functions of the postgresStore generically
 *
 * @param store The PostgresStore[Product] that was given by the Previous class
 */
case class DeepProductStore(store: PostgresStore[Product]) extends Store[Product] {
  override def findById(id: UUID)(implicit
                                  postgresFormat: PostgresFormat[Product],
                                  executionContext: ExecutionContext): Future[Either[String, Option[Product]]] =
    store.findById(id)

  override def findAll()(implicit
                         postgresFormat: PostgresFormat[Product],
                         executionContext: ExecutionContext): Future[Either[String, Seq[Product]]] = store.findAll()

  override def removeById(id: UUID)(implicit
                                    postgresFormat: PostgresFormat[Product],
                                    executionContext: ExecutionContext): Future[Either[String, Boolean]] =
    store.removeById(id)

  override def findWhere(parameters: Map[String, Any])(
      implicit
      postgresFormat: PostgresFormat[Product],
      executionContext: ExecutionContext
  ): Future[Either[String, Seq[Product]]] =
    store.findWhere(parameters)

  override def findOrWhere(parameters: Map[String, Any])(
      implicit
      postgresFormat: PostgresFormat[Product],
      executionContext: ExecutionContext
  ): Future[Either[String, Seq[Product]]] =
    store.findOrWhere(parameters)

  override def insert(product: Product)(implicit
                                        postgresFormat: PostgresFormat[Product],
                                        executionContext: ExecutionContext): Future[Either[String, Boolean]] =
    store.insert(product)

  override def update(id: UUID, product: Product)(implicit
                                                  postgresFormat: PostgresFormat[Product],
                                                  executionContext: ExecutionContext): Future[Either[String, Boolean]] =
    store.update(id, product)
}
