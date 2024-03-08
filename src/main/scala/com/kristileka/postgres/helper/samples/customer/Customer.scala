package com.kristileka.postgres.helper.samples.customer

import com.kristileka.postgres.helper.store.postgres.Record
import com.kristileka.postgres.helper.store.postgres.format.PostgresFormat

import java.time.{ LocalDate, LocalDateTime }
import java.util.UUID

/**
 * A case class and the companion that it uses, which has an implicit PostgresFormatter
 * to be used by the stores in order to store and read from the database the object data
 * that you want to store/read.
 */
case class Customer(
    id: UUID,
    email: String,
    firstName: String,
    lastName: String,
    birthDate: LocalDate,
    profileImage: String,
    countryCode: String,
    currencyCode: String,
    registeredAt: LocalDateTime
)

object Customer {
  implicit val customerPostgresFormat: PostgresFormat[Customer] =
    new PostgresFormat[Customer] {
      override def recordResultToModel(record: Record): Customer = Customer(
        id = record.getAs("customer.id"),
        email = record.getAs("customer.email"),
        firstName = record.getAs("customer.first_name"),
        lastName = record.getAs("customer.last_name"),
        birthDate = record.getAs("customer.birth_date"),
        profileImage = record.getAs("customer.profile_image"),
        countryCode = record.getAs("customer.country_code"),
        currencyCode = record.getAs("customer.currency_code"),
        registeredAt = record.getAs("customer.registered_at")
      )

      override def recordToMap(entity: Customer): Map[String, Any] = Map(
        "email"         -> entity.email,
        "first_name"    -> entity.firstName,
        "last_name"     -> entity.lastName,
        "birth_date"    -> entity.birthDate,
        "profile_image" -> entity.profileImage,
        "country_code"  -> entity.countryCode,
        "currency_code" -> entity.currencyCode,
        "registered_at" -> entity.registeredAt
      )
    }
}
