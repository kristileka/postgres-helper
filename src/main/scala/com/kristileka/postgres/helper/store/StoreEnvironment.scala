package com.kristileka.postgres.helper.store

/**
 * The storeEnvironment Trait to initialize the Tye
 */
trait StoreEnvironment {
  def getPostgresStore[T](tableName: String): Store[T]
}
