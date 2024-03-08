package com.kristileka.postgres.helper.store

import com.kristileka.postgres.helper.store.postgres.format.PostgresFormat

import java.util.UUID
import scala.concurrent.{ ExecutionContext, Future }

trait Store[T] {

  /** Find a row from the database where Id matches
    *
    * @param id               UUID version of the ID
    * @param postgresFormat   The PostgresFormat for the object
    * @param executionContext The executionContext
    * @return A Future Monad of Either String or the Option[T]
    */
  def findById(id: UUID)(implicit
                         postgresFormat: PostgresFormat[T],
                         executionContext: ExecutionContext): Future[Either[String, Option[T]]]

  /**
    * This function inserts a record on the database
    *
    * @param product          The Object Generic
    * @param postgresFormat   The Formatter used to read/write the generic object
    * @param executionContext the implicit execution context
    * @return A Future monad of either String or Boolean if the insertion was successful
    */
  def insert(product: T)(implicit
                         postgresFormat: PostgresFormat[T],
                         executionContext: ExecutionContext): Future[Either[String, Boolean]]

  /**
    * This function updates a record on the database
    *
    * @param id               The UUID of the record
    * @param product          The Object Generic
    * @param postgresFormat   The Formatter used to read/write the generic object
    * @param executionContext the implicit execution context
    * @return A Future monad of either String or Boolean that determines whether the function was successful or not
    */
  def update(id: UUID, product: T)(implicit
                                   postgresFormat: PostgresFormat[T],
                                   executionContext: ExecutionContext): Future[Either[String, Boolean]]

  /** Find all rows for the table in the database
    *
    * @param postgresFormat   The PostgresFormat of the object
    * @param executionContext The executionContext
    * @return A Future monad of Either String or the Sequence of Type T
    */
  def findAll()(implicit
                postgresFormat: PostgresFormat[T],
                executionContext: ExecutionContext): Future[Either[String, Seq[T]]]

  /** Filter from database requiring every property
    *
    * @param parameters       The list of parameters to search for
    * @param postgresFormat   The Postgres Format for the object
    * @param executionContext The ExecutionContext
    * @return A Future Monad of Either Error String or the Sequence of type T
    */
  def findWhere(parameters: Map[String, Any])(implicit
                                              postgresFormat: PostgresFormat[T],
                                              executionContext: ExecutionContext): Future[Either[String, Seq[T]]]

  /** Filter from database using non required properties
    *
    * @param parameters       The list of parameters to search for
    * @param postgresFormat   The Postgres Format for the object
    * @param executionContext The ExecutionContext
    * @return A Future Monad of Either Erorr String or the Sequence of type T
    */
  def findOrWhere(parameters: Map[String, Any])(implicit
                                                postgresFormat: PostgresFormat[T],
                                                executionContext: ExecutionContext): Future[Either[String, Seq[T]]]

  /** Remove a row from the database where Id matches
    *
    * @param id               UUID version of the ID
    * @param postgresFormat   The PostgresFormat for the object
    * @param executionContext The executionContext
    * @return A Future Monad of Either Error String or boolean with status of the query
    */
  def removeById(id: UUID)(implicit
                           postgresFormat: PostgresFormat[T],
                           executionContext: ExecutionContext): Future[Either[String, Boolean]]
}
