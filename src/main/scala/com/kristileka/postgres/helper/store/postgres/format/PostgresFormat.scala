package com.kristileka.postgres.helper.store.postgres.format

import com.kristileka.postgres.helper.store.postgres.Record

/**
  * A trait that encapsulates what a Object needs to be written/read from the database
  * using a Record Class and a Map[String,Any]
  * @tparam T The Generic Type of the object
  */
trait PostgresFormat[T] {
  def recordResultToModel(record: Record): T
  def recordToMap(entity: T): Map[String, Any]
}
