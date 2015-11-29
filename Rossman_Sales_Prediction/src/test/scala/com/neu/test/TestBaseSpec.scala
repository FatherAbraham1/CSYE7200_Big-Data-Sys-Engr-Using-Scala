package com.neu.test

import org.scalatest._
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.hive.HiveContext

abstract class TestBaseSpec extends FlatSpec with BeforeAndAfterAll with Matchers {
  
  private val master = "local"
  private val appName = "Rossmann Sales Testing"
  var sc: SparkContext = _
  var sqlContext: SQLContext = _
  var hiveContext: HiveContext = _

  val conf = new SparkConf()
    .setMaster(master)
    .setAppName(appName)
    .set("spark.driver.allowMultipleContexts","true")


  override protected def beforeAll(): Unit = {
    super.beforeAll()
    sc = new SparkContext(conf)    
    sqlContext = new SQLContext(sc)
    hiveContext = new HiveContext(sc)
  }

  override protected def afterAll(): Unit = {
    try {
      sc.stop()
      sc = null
      sqlContext = null
      hiveContext = null
    } finally {
      super.afterAll()
    }
  }
  
}

