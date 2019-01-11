package com.exasol.spark

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.Row
import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.SQLContext

import com.holdenkarau.spark.testing.DataFrameSuiteBase
import org.mockito.Mockito.when
import org.scalatest.{FunSuite, Matchers}
import org.scalatest.mockito.MockitoSugar

class DefaultSourceSuite extends FunSuite with Matchers with MockitoSugar {

  test("when reading should throw an Exception if no `query` parameter is provided") {
    val sqlContext = mock[SQLContext]

    val thrown = intercept[UnsupportedOperationException] {
      new DefaultSource().createRelation(sqlContext, Map[String, String]())
    }

    assert(
      thrown.getMessage === "A query parameter should be specified in order to run the operation"
    )
  }

  test("when saving should throw an Exception if no `table` parameter is provided") {
    val df = mock[DataFrame]
    val sqlContext = mock[SQLContext]

    val thrown = intercept[UnsupportedOperationException] {
      new DefaultSource().createRelation(sqlContext, SaveMode.Append, Map[String, String](), df)
    }

    assert(
      thrown.getMessage === "A table parameter should be specified in order to run the operation"
    )
  }

  test("`repartitionPerNode` should reduce dataframe partitions number") {
    val df = mock[DataFrame]
    val rdd = mock[RDD[Row]]

    when(df.rdd).thenReturn(rdd)
    when(rdd.getNumPartitions).thenReturn(2)

    val source = new DefaultSource()

    assert(source.repartitionPerNode(df, 2) === df)
    assert(source.repartitionPerNode(df, 2).rdd.getNumPartitions === 2)

    val reducedDF = mock[DataFrame]
    val reducedRdd = mock[RDD[Row]]
    when(reducedDF.rdd).thenReturn(reducedRdd)
    when(reducedRdd.getNumPartitions).thenReturn(1)

    when(df.coalesce(1)).thenReturn(reducedDF)
    assert(source.repartitionPerNode(df, 1) === reducedDF)
    assert(source.repartitionPerNode(df, 1).rdd.getNumPartitions === 1)
  }

  test("mergeConfigurations should merge runtime sparkConf into user provided parameters") {
    val sparkConf = Map[String, String](
      "spark.exasol.username" -> "newUsername",
      "spark.exasol.host" -> "hostName",
      "spark.other.options" -> "irrelevance"
    )
    val parameters = Map[String, String]("username" -> "oldUsername", "password" -> "oldPassword")

    val newConf = new DefaultSource().mergeConfigurations(parameters, sparkConf)
    // overwrite config if both are provided
    assert(newConf.getOrElse("username", "not available") === "newUsername")

    // use config from parameters if sparkConf doesn't provide
    assert(newConf.getOrElse("password", "some random password") === "oldPassword")

    // use config from sparkConf if parameters doesn't provide
    assert(newConf.getOrElse("host", "some random host") === "hostName")

    // should not contains irrelevant options for exasol
    assert(!newConf.contains("spark.other.options") && !newConf.contains("options"))
  }
}
