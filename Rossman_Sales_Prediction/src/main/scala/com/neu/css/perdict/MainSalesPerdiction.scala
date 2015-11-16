package com.neu.css.perdict

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import com.neu.css.perdict.clean.DataCleanerUtil
import com.neu.css.perdict.algo.EnergyPredictionUsageUtil
import com.neu.css.perdict.algo.EnergyMetricsCalculatorUtil
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

    val metricsCalculator = new EnergyMetricsCalculatorUtil(sparkContext)
    val energyConsumptionPrediction = new EnergyPredictionUsageUtil(sparkContext)
    val dataAggregator = new DataAggregatorUtil()

    /*
    
    
    // Get the daily data consumption record
    val dailyRDD = dataAggregator.dailyDataAggregator(inputRDD)
    
    val dailyDataFrame = dailyRDD.map(p => DailyDataFrame(p._2.date, p._2.month, p._2.year, p._2.activePower, p._2.reactivePower,
                                                    p._2.voltage,p._2.globalIntensity, p._2.subMetering1,
                                                    p._2.subMetering2, p._2.subMetering3, p._2.totalCost, p._2.totalPowerUsed, p._2.powerMetered)).toDF()
                                                   
    dailyDataFrame.createJDBCTable(MYSQL_CONNECTION_URL, "Daily_Consumption", true)
  
    // Get the monthly data consumption record
    val monthlyRDD = dataAggregator.monthlyDataAggregator(inputRDD)
    
    val monthlyDataFrame = monthlyRDD.map(p => MonthlyDataFrame(p._2.month, p._2.year, p._2.activePower, p._2.reactivePower,
                                                    p._2.voltage,p._2.globalIntensity, p._2.subMetering1,
                                                    p._2.subMetering2, p._2.subMetering3, p._2.totalCost, p._2.totalPowerUsed, p._2.powerMetered)).toDF()
    
    monthlyDataFrame.createJDBCTable(MYSQL_CONNECTION_URL, "Monthly_Consumption", true)
    
    
    // linear Regression with SGD
    val parsedData = monthlyRDD.map { line =>
      
      LabeledPoint(line._2.month.toDouble, Vectors.dense(line._2.totalPowerUsed))
    }.cache()
    
    // Building the model
    val numIterations = 1000
    val model = LinearRegressionWithSGD.train(parsedData, numIterations)
    
    // Evaluate model on training examples and compute training error
    val valuesAndPreds = parsedData.map { point =>
      val prediction = model.predict(point.features)
      (point.label, prediction)
    }
    val MSE = valuesAndPreds.map{case(v, p) => math.pow((v - p), 2)}.mean()
    println("training Mean Squared Error = " + MSE)
   
    
    // Get the Yearly data consumption record
    val yearlyRDD = dataAggregator.yearlyDataAggregator(inputRDD)
    
    val yearlyDataFrame = yearlyRDD.map(p => YearlyDataFrame(p._2.year, p._2.activePower, p._2.reactivePower,
                                                    p._2.voltage,p._2.globalIntensity, p._2.subMetering1,
                                                    p._2.subMetering2, p._2.subMetering3, p._2.totalCost, p._2.totalPowerUsed, p._2.powerMetered)).toDF()
    
    yearlyDataFrame.createJDBCTable(MYSQL_CONNECTION_URL, "Yearly_Consumption", true)
    
    
    /** Calculate revenue loss whenever there is power outage */
    val revenueLossForOneDay = metricsCalculator.getAverageRevenueLossPerDay(inputRDD)
    
    val revenueLossRDD = sparkContext.makeRDD(List(revenueLossForOneDay))
    
    val averageRevenueLossForOneday = revenueLossRDD.map(p => AverageRevenueLossForOneday(1, p)).toDF()
    
    averageRevenueLossForOneday.createJDBCTable(MYSQL_CONNECTION_URL, "Average_Revenue_Loss", true)
   

    
    /** Predict peak time load for weekend and weekday for next one week */
    val(peakTimeLoadWeekday,peakTimeLoadWeekend) = metricsCalculator.getPeekLoadWeekly(inputRDD)
    
    val peakTimeLoadRDD = sparkContext.makeRDD(Array(peakTimeLoadWeekday,peakTimeLoadWeekend))
    
    val peakTimeLoadDataframe = peakTimeLoadRDD.map(p => PeakTimeLoad(peakTimeLoadWeekday, peakTimeLoadWeekend)).toDF()
    
    peakTimeLoadDataframe.createJDBCTable(MYSQL_CONNECTION_URL, "Peak_Time_Load", true)
    
    
    
    /** predict next day use */
    val  predictedValueKW = energyConsumptionPrediction.calculateNextDayEnergyConsumption(inputRDD)
    
    val nextDayPowerPerdictionRDD = sparkContext.makeRDD(List(predictedValueKW))
    
    val nextDate = "27/11/2012";
    
    val nextDayPowerPerdictionDataFrame = nextDayPowerPerdictionRDD.map(p => NextDayPowerConsumption(nextDate.toString(), p)).toDF()
    
    nextDayPowerPerdictionDataFrame.createJDBCTable(MYSQL_CONNECTION_URL, "Next_Day_Power_Perdiction", true)
    
 
    /** predict the weekly usage*/
    val predictionArray = energyConsumptionPrediction.calculateWeeklyEnergyConsumption(inputRDD)
    
    val weeklyPerdictRDD =  sparkContext.makeRDD(predictionArray)
    
    val weeklyPerdictDF  = weeklyPerdictRDD.map(p => NextYearPowerConsumption(p.split(',')(0), p.split(',')(1), p.split(',')(2), p.split(',')(3))).toDF()
    
    weeklyPerdictDF.createJDBCTable(MYSQL_CONNECTION_URL, "Next_Year_Power_Perdiction", true)
    * */
  }
}
