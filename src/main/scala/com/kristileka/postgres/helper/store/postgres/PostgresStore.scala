package com.kristileka.postgres.helper.store.postgres

import anorm._
import com.kristileka.postgres.helper.driver.PostgresDriver
import com.kristileka.postgres.helper.store.Store
import com.kristileka.postgres.helper.store.postgres.format.PostgresFormat
import org.joda.time.LocalDateTime

import java.sql.SQLException
import java.time.LocalDate
import java.util.UUID
import scala.concurrent.{ ExecutionContext, Future }

case class PostgresStore[T](
    driver: PostgresDriver,
    tableName: String
) extends Store[T] {

  private val TABLE_NAME: String = tableName
  private val AND                = "AND"
  private val OR                 = "OR"

  /** A PostgresQuery object to run queries using parameters
    * @param query The query to be prepared and run
    * @param parameters The parameters the query needs to run
    */
  case class PostgresQuery(
      query: String,
      parameters: Map[String, Any] = Map.empty[String, Any]
  ) {
    private val dbSession = driver.db

    /** Execute the query and return a List of Records for the query
      * @param preparedQuery The Prepared Query
      * @return The List of records
      */
    private def run(preparedQuery: String): Either[String, List[Record]] =
      dbSession.withConnection { implicit connection =>
        try { Right(SQL(preparedQuery).as(Record.parser.*)) } catch {
          case exception: SQLException => Left(exception.getMessage)
          case exception: Exception    => Left(exception.getMessage)
        }
      }

    /** Execute the query and return a boolean based on the changed rows
      *
      * @param preparedQuery The Prepared Query
      * @return The boolean representing if rows were affected
      */
    private def runWithResult(preparedQuery: String): Either[String, Boolean] =
      dbSession.withConnection { implicit connection =>
        try {
          val rowsAffected = SQL(preparedQuery).executeUpdate()
          Right(rowsAffected > 0)
        } catch {
          case ex: SQLException => Left(ex.getMessage)
          case ex: Exception    => Left(ex.getMessage)
        }
      }

    /** Run a query and excpect single Result
      * @param executionContext The ExecutionContexct where to run
      * @return The single Optional Record
      */
    def querySingle()(
        implicit
        executionContext: ExecutionContext
    ): Future[Either[String, Option[Record]]] = {
      val result = buildPreparedQuery()
      Future { run(result).map(_.headOption) }
    }

    /** Run a query and get a list of Records
      *
      * @param executionContext The ExecutionContext where to run
      * @return The Sequence of Records
      */
    def queryList()(
        implicit
        executionContext: ExecutionContext
    ): Future[Either[String, Seq[Record]]] = {
      val result = buildPreparedQuery()
      Future {
        run(result)
      }
    }

    /** Run a delete query that will return a Boolean
      * @param executionContext The ExecutionContext where to run
      * @return boolean that represents the value of changed lines
      */
    def queryResult()(
        implicit
        executionContext: ExecutionContext
    ): Future[Either[String, Boolean]] = {
      val result = buildPreparedQuery()
      Future {
        runWithResult(result)
      }
    }

    /** Build properties of the query with the parameters
      * @return the String version of the PreparedQuery
      */
    private val stringParametersClasses = List(
      classOf[UUID],
      classOf[String],
      classOf[LocalDate],
      classOf[LocalDateTime]
    )
    private def buildPreparedQuery(): String =
      parameters.foldLeft(query) {
        case (currentStr, (key, value)) => {
          if (stringParametersClasses.contains(value.getClass))
            currentStr.replace(s"$$$key", s"'$value'")
          else
            currentStr.replace(s"$$$key", s"$value")
        }
      }
  }

  /** Find a row from the database where Id matches
    *
    * @param id               UUID version of the ID
    * @param postgresFormat   The PostgresFormat for the object
    * @param executionContext The executionContext
    * @return A Future Monad of Either String or the Option[T]
    */
  override def findById(id: UUID)(implicit
                                  postgresFormat: PostgresFormat[T],
                                  executionContext: ExecutionContext): Future[Either[String, Option[T]]] = {
    val query =
      s"""
         | SELECT * from $TABLE_NAME
         | where id = $$id
         |""".stripMargin
    PostgresQuery(query, Map("id" -> id)).querySingle().map {
      case Left(error) => Left(error)
      case Right(record) =>
        Right(record.map(postgresFormat.recordResultToModel))
    }
  }

  /** Find all rows for the table in the database
    *
    * @param postgresFormat   The PostgresFormat of the object
    * @param executionContext The executionContext
    * @return A Future monad of Either String or the Sequence of Type T
    */
  override def findAll()(implicit
                         postgresFormat: PostgresFormat[T],
                         executionContext: ExecutionContext): Future[Either[String, Seq[T]]] = {
    val query =
      s"""
         | SELECT * from $TABLE_NAME
         |""".stripMargin
    PostgresQuery(query).queryList().map {
      case Left(error) => Left(error)
      case Right(records) =>
        Right(records.map(record => postgresFormat.recordResultToModel(record)))
    }
  }

  /** Remove a row from the database where Id matches
   *
   * @param id               UUID version of the ID
   * @param postgresFormat   The PostgresFormat for the object
   * @param executionContext The executionContext
   * @return A Future Monad of Either Error String or boolean with status of the query
   */
  override def removeById(id: UUID)(implicit
                                    postgresFormat: PostgresFormat[T],
                                    executionContext: ExecutionContext): Future[Either[String, Boolean]] = {
    val query =
      s"""
       | DELETE from $TABLE_NAME
       | WHERE id = $$id
       |""".stripMargin
    PostgresQuery(query, Map("id" -> id)).queryResult()
  }

  /** Filter from database requiring every property
    *
    * @param parameters       The list of parameters to search for
    * @param postgresFormat   The Postgres Format for the object
    * @param executionContext The ExecutionContext
    * @return A Future Monad of Either Error String or the Sequence of type T
    */
  override def findWhere(
      parameters: Map[String, Any]
  )(implicit
    postgresFormat: PostgresFormat[T],
    executionContext: ExecutionContext): Future[Either[String, Seq[T]]] = {

    val query = buildFindWhereQuery(parameters, AND)
    PostgresQuery(query, parameters).queryList().map {
      case Left(error) => Left(error)
      case Right(record) =>
        Right(record.map(postgresFormat.recordResultToModel))
    }
  }

  /** Filter from database using non required properties
    * @param parameters The list of parameters to search for
    * @param postgresFormat The Postgres Format for the object
    * @param executionContext The ExecutionContext
    * @return A Future Monad of Either Error String or the Sequence of type T
    */
  override def findOrWhere(
      parameters: Map[String, Any]
  )(implicit
    postgresFormat: PostgresFormat[T],
    executionContext: ExecutionContext): Future[Either[String, Seq[T]]] = {
    val query = buildFindWhereQuery(parameters, OR)
    PostgresQuery(query, parameters).queryList().map {
      case Left(error) => Left(error)
      case Right(record) =>
        Right(record.map(postgresFormat.recordResultToModel))
    }
  }

  /**
    * This function inserts a record on the database
    *
    * @param entity          The Object Generic
    * @param postgresFormat   The Formatter used to read/write the generic object
    * @param executionContext the implicit execution context
    * @return A Future monad of either String or Boolean if the insertion was successful
    */
  override def insert(entity: T)(implicit
                                 postgresFormat: PostgresFormat[T],
                                 executionContext: ExecutionContext): Future[Either[String, Boolean]] = {

    val parameters   = postgresFormat.recordToMap(entity)
    val keys         = parameters.keys.toList.sorted
    val placeholders = keys.map(key => s"$$$key").mkString(", ")

    val query =
      s"INSERT INTO $TABLE_NAME (${keys.mkString(", ")}) VALUES ($placeholders);"
    PostgresQuery(query, parameters)
      .queryResult()
  }

  /**
    * This function updates a record on the database
    * @param id The UUID of the record
    * @param entity The Object Generic
    * @param postgresFormat The Formatter used to read/write the generic object
    * @param executionContext the implicit execution context
    * @return A Future monad of either String or Boolean that determines whether the function was successful or not
    */
  override def update(id: UUID, entity: T)(implicit
                                           postgresFormat: PostgresFormat[T],
                                           executionContext: ExecutionContext): Future[Either[String, Boolean]] = {

    val parameters = postgresFormat.recordToMap(entity).removed("id")
    val setClause =
      parameters.keys.map(key => s"$key = $$${key}").mkString(", ")

    val query = s"UPDATE $TABLE_NAME SET $setClause WHERE id = '$id';"
    PostgresQuery(query, parameters)
      .queryResult()
  }

  /** PRIVATE FUNCTIONS */
  private def buildFindWhereQuery(
      parameters: Map[String, Any],
      operation: String
  ): String = {
    val result = parameters
      .map { case (key, _) => s"$key = $$$key" }
      .mkString(s" $operation ")

    s"""
       | SELECT * from $TABLE_NAME
       | where ($result)
       |""".stripMargin
  }
}
