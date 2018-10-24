package com.exasol.spark

import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.sources.{
  BaseRelation,
  DataSourceRegister,
  RelationProvider,
  SchemaRelationProvider
}
import org.apache.spark.sql.types.StructType

import com.exasol.spark.util.{ExasolConfiguration, ExasolConnectionManager}

class DefaultSource extends RelationProvider with DataSourceRegister with SchemaRelationProvider {

  override def shortName(): String = "exasol"

  override def createRelation(
    sqlContext: SQLContext,
    parameters: Map[String, String]
  ): BaseRelation = {

    val (queryString, manager) = fromParameters(parameters, sqlContext)
    new ExasolRelation(sqlContext, queryString, None, manager)
  }

  override def createRelation(
    sqlContext: SQLContext,
    parameters: Map[String, String],
    schema: StructType
  ): BaseRelation = {

    val (queryString, manager) = fromParameters(parameters, sqlContext)
    new ExasolRelation(sqlContext, queryString, Option(schema), manager)
  }

  private[spark] def mergeConfiguration(
    parameters: Map[String, String],
    sparkConf: Map[String, String]
  ): Map[String, String] = {
    var result: Map[String, String] = parameters
    sparkConf.keySet
      .filter(_.contains("spark.exasol"))
      .foreach(key => {
        val keySuffix: String = key.substring(key.lastIndexOf(".") + 1)
        result += (keySuffix -> sparkConf.getOrElse(key, ""))
      })
    result
  }

  private def fromParameters(
    parameters: Map[String, String],
    sqlContext: SQLContext
  ): (String, ExasolConnectionManager) = {
    val queryString = parameters.get("query") match {
      case Some(sql) => sql
      case None =>
        throw new UnsupportedOperationException(
          "A sql query string should be specified when loading from Exasol"
        )
    }
    val config = ExasolConfiguration(mergeConfiguration(parameters, sqlContext.getAllConfs))
    val manager = ExasolConnectionManager(config)
    (queryString, manager)
  }
}
