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
  case class salesDataFrame(date: String, month: Int, year: Long, activePower: Double, reactivePower: Double, voltage: Double, globalIntensity: Double,
    subMetering1: Double, subMetering2: Double, subMetering3: Double, totalCost: Double,
    totalPowerUsed: Double, powerMetered: Double)
  
  /**
   * data frame object for the stores attributes
   */  
  case class storeDataFrame(month: Int, year: Long, activePower: Double, reactivePower: Double, voltage: Double, globalIntensity: Double,
    subMetering1: Double, subMetering2: Double, subMetering3: Double, totalCost: Double,
    totalPowerUsed: Double, powerMetered: Double)
    
  /**
   * data frame object for the test dat set
   */
  case class testDataFrame(year: Long, activePower: Double, reactivePower: Double, voltage: Double, globalIntensity: Double,
    subMetering1: Double, subMetering2: Double, subMetering3: Double, totalCost: Double,
    totalPowerUsed: Double, powerMetered: Double)
    
  /**
   *   data frame object for the AverageRevenueLossForOneday
   */
  case class AverageRevenueLossForOneday(Days: Int, RevenueLoss: Double)
  
  /**
   * data frame object for the PeakTimeLoad
   */
  case class PeakTimeLoad(WeekdayPeakTimeLoad: Double, WeekendPeakTimeLoad: Double)
  
  /**
   * data frame object for the NextDayPowerConsumption
   */
  case class NextDayPowerConsumption(date: String, powerConsumption: Double)
  
  /**
   * data frame object for the NextYearPowerConsumption
   */
  case class NextYearPowerConsumption(week: String, day: String, power_consumption: String, date: String)
    
    private final val MYSQL_USERNAME = "laksh";
    private final val MYSQL_PWD = "laksh";
    private final val MYSQL_CONNECTION_URL = "jdbc:mysql://localhost:3306/energy_prediction?user=" + MYSQL_USERNAME + "&password=" + MYSQL_PWD;

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
