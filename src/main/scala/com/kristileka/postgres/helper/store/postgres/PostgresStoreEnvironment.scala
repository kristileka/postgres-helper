package com.kristileka.postgres.helper.store.postgres

import com.kristileka.postgres.helper.driver.PostgresDriver
import com.kristileka.postgres.helper.store.{ Store, StoreEnvironment }

import javax.inject.Inject

/**
 * Postgres Store Enviornment Provider
 * @param postgresDriver
 */
case class PostgresStoreEnvironment @Inject()(postgresDriver: PostgresDriver) extends StoreEnvironment {
  override def getPostgresStore[T](tableName: String): Store[T] =
    PostgresStore[T](
      postgresDriver,
      tableName
    )
}
