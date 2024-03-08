package com.kristileka.postgres.helper.samples.product

import com.kristileka.postgres.helper.store.postgres.Record
import com.kristileka.postgres.helper.store.postgres.format.PostgresFormat

import java.time.LocalDateTime
import java.util.UUID

case class Product(
    id: UUID,
    name: String,
    description: String,
    imageUrl: String,
    createdAt: LocalDateTime
)

object Product {
  implicit val productPostgresFormat: PostgresFormat[Product] =
    new PostgresFormat[Product] {
      override def recordResultToModel(record: Record): Product = Product(
        id = record.getAs("product.id"),
        name = record.getAs("product.name"),
        description = record.getAs("product.description"),
        imageUrl = record.getAs("product.image_url"),
        createdAt = record.getAs("product.createdAt")
      )

      override def recordToMap(entity: Product): Map[String, Any] = Map(
        "id"          -> entity.id,
        "name"        -> entity.name,
        "description" -> entity.description,
        "imageUrl"    -> entity.imageUrl,
        "createdAt"   -> entity.createdAt
      )
    }
}
