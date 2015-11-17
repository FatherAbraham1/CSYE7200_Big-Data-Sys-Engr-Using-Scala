package com.neu.css.perdict

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import com.neu.css.perdict.clean.DataCleanerUtil
import com.neu.css.perdict.clean.DataAggregatorUtil
import org.apache.spark.SparkConf
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.DataFrameReader
import org.apache.spark.sql.Column
import org.apache.spark.sql.DataFrame
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.regression.LinearRegressionModel
import org.apache.spark.mllib.regression.LinearRegressionWithSGD
import org.apache.spark.mllib.linalg.Vectors


/**
 * lakshl
 */
object MainSalesPerdiction {
  
  
  /**
   * data frame object for the daily sales
   */
  case class salesDataFrame(storeID: String, storeType: String, assortment: String, competitionDistance: Long, promo: String, competitionOpenSinceMonth: Int, competitionOpenSinceYear: Int,
    promo2: String, promo2SinceWeek: Int, promo2SinceYear: Int, promoInterval: String)
  
  /**
   * data frame object for the stores attributes
   */  
  case class storeDataFrame(id: Long, storeID: Long, dayOfTheWeek: Long, date: String, sales: Long, customer: Long,
    open: Int, promo: String, stateHoliday: String, schoolHoliday: String)
    
  /**
   * data frame object for the test dat set
   */
  case class testDataFrame(id: Long, storeID: Long, dayOfTheWeek: Long, date: String, open: Int, promo: String, stateHoliday: String, schoolHoliday: String)
    
  
  /**
   * data frame object for the NextSixWeekSales
   */
  case class NextSixWeekSales(id: String, sales: String, date: String)
    
    private final val MYSQL_USERNAME = "laksh";
    private final val MYSQL_PWD = "laksh";
    private final val MYSQL_CONNECTION_URL = "jdbc:mysql://localhost:3306/sales_prediction?user=" + MYSQL_USERNAME + "&password=" + MYSQL_PWD;

  /**
   * main method
   */
  def main(args: Array[String]) {
    
    // read the input file store and sales
    val inputSalesFile = "src/main/resources/store.csv"
    val inputStoreFile = "src/main/resources/train.csv"
    val results = "results"
    
    // use the spark context to read the file and Sql context to store data frames into the db
    val sparkContext = new SparkContext("local", "DataCleansing")
    val sqlContext = new org.apache.spark.sql.SQLContext(sparkContext)
    import sqlContext.implicits._
    
    val inputSalesRawRDD = sparkContext.textFile(inputSalesFile)
    
    val inputStoresRawRDD = sparkContext.textFile(inputStoreFile)
    
    /**Clean the missing data */
    val dataCleaner = new DataCleanerUtil()
    val withoutMissingValuesRDD = dataCleaner.removeMissingValues(inputSalesRawRDD)
    
    // convert the into the particular format for spark algorithms
    val inputFormatSalesRDD = dataCleaner.covertToStoreSalesFormat(withoutMissingValuesRDD)
    inputFormatSalesRDD.cache()

    //val metricsCalculator = new SalesMetricsCalculatorUtil(sparkContext)
    //val energyConsumptionPrediction = new SalesPredictionUsageUtil(sparkContext)
    val dataAggregator = new DataAggregatorUtil()

  }
}
