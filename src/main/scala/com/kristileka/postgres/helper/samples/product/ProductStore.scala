package com.kristileka.postgres.helper.samples.product

import com.kristileka.postgres.helper.samples.ERROR_CASES.DEFAULT_ERROR
import com.kristileka.postgres.helper.store.StoreEnvironment
import com.kristileka.postgres.helper.store.postgres.PostgresStore

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ ExecutionContext, Future }

case class ProductStore @Inject()(
    storeEnvironment: StoreEnvironment,
) {
  private lazy val store = DeepProductStore(
    storeEnvironment
      .getPostgresStore[Product]("product")
      .asInstanceOf[PostgresStore[Product]]
  )

  def create(product: Product)(implicit
                               executionContext: ExecutionContext): Future[Either[String, UUID]] =
    store.insert(product).map {
      case Left(value) => Left(value)
      case Right(value) =>
        if (value) Right(product.id) else Left(DEFAULT_ERROR)
    }

  def update(id: UUID, product: Product)(implicit
                                         executionContext: ExecutionContext,
  ): Future[Either[String, Boolean]] =
    store.update(id, product)

  def findById(uuid: UUID)(implicit
                           executionContext: ExecutionContext): Future[Either[String, Option[Product]]] =
    store.findById(uuid)

  def findAll()(
      implicit
      executionContext: ExecutionContext
  ): Future[Either[String, Seq[Product]]] = store.findAll()

}
