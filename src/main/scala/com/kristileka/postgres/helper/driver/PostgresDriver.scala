package com.kristileka.postgres.helper.driver

import play.api.db.Database

/**
  * A trait that is used to encapsulate the db instance created
  * by your app that the postgres helper uses.
  */
trait PostgresDriver {
  val db: Database
}
