package com.kristileka.postgres.helper.store.postgres

import anorm.{ RowParser, SqlParser }

import java.time.{ LocalDateTime, ZoneId }
import java.util.Date

/**
  * A case class and Companion for the Record that reads the the data from the database and parses them to The generic type
  * @param data The Map[String,Any] of the data that came from the query
  */
case class Record(data: Map[String, Any]) {
  def getAs[T](key: String): T = {
    val singleData = data(key)
    singleData match {
      case sqlDate: java.sql.Date =>
        sqlDate.toLocalDate.asInstanceOf[T]
      case javaDate: Date =>
        LocalDateTime
          .ofInstant(javaDate.toInstant, ZoneId.systemDefault())
          .asInstanceOf[T]
      case _ =>
        singleData.asInstanceOf[T]
    }
  }
}

object Record {
  val parser: RowParser[Record] =
    SqlParser.folder(Record(Map.empty[String, Any])) { (map, value, meta) =>
      Right(Record(map.data + (meta.column.qualified -> value)))
    }
}
