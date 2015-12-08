package com.neu.css.perdict.clean

import org.apache.log4j.{ Logger }
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.sql.DataFrame

/**
 * lakshl
 */
object DataAggregatorUtil {

  val DATABRICKS_SPARK_CSV_PROPERTY = "com.databricks.spark.csv"

  val HEADER = "header"

  val IS_HEADER = "true"

  val RAW_TRAINING_TABLE = "raw_training_table"

  val RAW_TEST_TABLE = "raw_test_table"

  val SQL_RETRIEVE_TRAINING_DATA_QUERY = """SELECT
      double(Sales) label, double(Store) Store, int(Open) Open, double(DayOfWeek) DayOfWeek, 
      StateHoliday, SchoolHoliday, (double(regexp_extract(Date, '\\d+-\\d+-(\\d+)', 1))) DayOfMonth
      FROM raw_training_table"""

  //  filtering out the null values manually
  val SQL_RETRIEVE_TEST_DATA_QUERY = """SELECT
      Id, double(Store) Store, int(Open) Open, double(DayOfWeek) DayOfWeek, StateHoliday, 
      SchoolHoliday, (double(regexp_extract(Date, '\\d+-\\d+-(\\d+)', 1))) DayOfMonth
      FROM raw_test_table
      WHERE !(ISNULL(Id) OR ISNULL(Store) OR ISNULL(Open) OR ISNULL(DayOfWeek) 
        OR ISNULL(StateHoliday) OR ISNULL(SchoolHoliday))"""

  // Getting the raw data using databricks csv formatter 
  def getRawdata(hiveContext: HiveContext, filePath: String) = {

    val rawdata = hiveContext
      .read.format(DATABRICKS_SPARK_CSV_PROPERTY)
      .option(HEADER, IS_HEADER)
      .load(filePath)
      .repartition(30)

    rawdata
  }

  //loading the training data csv using the databricks csv formatter and registering it as temp table 
  def loadTrainDataset(hiveContext: HiveContext, filePath: String): DataFrame = {

    val trainRawdata = getRawdata(hiveContext, filePath)

    trainRawdata.registerTempTable(RAW_TRAINING_TABLE)

    hiveContext.sql(SQL_RETRIEVE_TRAINING_DATA_QUERY).na.drop()
  }

  //loading the test data csv using the databricks csv formatter and registering it as temp table 
  def loadTestDataset(hiveContext: HiveContext, filePath: String) = {

    val testRawdata = getRawdata(hiveContext, filePath)

    testRawdata.registerTempTable(RAW_TEST_TABLE)

    val testData = hiveContext.sql(SQL_RETRIEVE_TEST_DATA_QUERY).na.drop()

    Array(testRawdata, testData)

  }

}





