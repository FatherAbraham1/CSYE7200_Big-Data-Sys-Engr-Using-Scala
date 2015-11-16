package com.neu.css.perdict.algo

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.rdd.RDD
import java.text.SimpleDateFormat
import java.util.{Calendar, Date}
import scala.util.Random
import com.neu.css.perdict.clean.DataAggregatorUtil
import org.apache.spark.rdd.RDD.rddToOrderedRDDFunctions
import org.apache.spark.rdd.RDD.rddToPairRDDFunctions
import scala.Range

/**
 * lakshl
 */
class SalesPredictionUsageUtil(sparkContext: SparkContext) {

//  
//  /**
//   * method to calculate the aggregate energy consumption for 1 day 
//   */
//  private def dailyAggregationMethod(inputRDD: RDD[((String, Long), RecordDataFrame)]): RDD[(Date, RecordDataFrame)] = {
//    inputRDD.map(value => {
//      val ((date, hour), recordValue) = value
//      val dateFormat = new SimpleDateFormat("dd/MM/yyyy")
//      val dateValue = dateFormat.parse(date)
//      (dateValue, recordValue)
//    }).reduceByKey((firstRecord, secondRecord) => {
//      val recordValue = new RecordDataFrame()
//      recordValue.hourofDay = firstRecord.hourofDay
//      recordValue.subMetering1 = firstRecord.subMetering1 + secondRecord.subMetering1
//      recordValue.subMetering2 = firstRecord.subMetering2 + secondRecord.subMetering2
//      recordValue.subMetering3 = firstRecord.subMetering3 + secondRecord.subMetering3
//
//      recordValue.activePower = firstRecord.activePower + secondRecord.activePower
//      recordValue.reactivePower = firstRecord.reactivePower + secondRecord.reactivePower
//
//      recordValue.voltage = (firstRecord.voltage + secondRecord.voltage) / 2
//      recordValue.globalIntensity = (firstRecord.globalIntensity + secondRecord.globalIntensity) / 2
//      recordValue
//    })
//
//  }
//
//  /**
//   * method to calculate the standard mean
//   */
//  private def calculateStandardMean(inputRDD: RDD[(Date, RecordDataFrame)]): (List[Double], Double) = {
//    val count = inputRDD.count()
//    var sum = 0.0
//    var riList = List[Double]()
//    for (i <- Range(1, count.toInt)) {
//      val firstRecord = inputRDD.toArray()(i)
//      val secondRecord = inputRDD.toArray()(i - 1)
//      val difference = (firstRecord._2.totalPowerUsed - secondRecord._2.totalPowerUsed) / firstRecord._2.totalPowerUsed
//      riList = riList ::: List(difference)
//      sum += difference
//    }
//
//    (riList, sum / count)
//
//  }
//
//  /**
//   * method to calculate the Standard Deviation
//   */
//  private def calculateStandardDeviation(inputRDD: RDD[Double], mean: Double): Double = {
//    val sum = inputRDD.map(value => {
//      (value - mean) * (value - mean)
//    }).reduce((firstValue, secondValue) => {
//      firstValue + secondValue
//    })
//    scala.math.sqrt(sum / inputRDD.count())
//  }
//
//  /**
//   * method to Calculate the Weekly consumption
//   */
//  def calculateWeeklyEnergyConsumption(inputRDD: RDD[RecordDataFrame]):List[String] = {
//
//    def weeklyAggregation(predictedValue: Double, mean: Double, stdDeviation: Double, count: Double, lastvalue: Double, time: Double): (Double, Double, Double, Double, Double, Double) = {
//      val r1 = (predictedValue - lastvalue) / lastvalue
//      val updatedMean: Double = (mean * count + r1) / (count + 1)
//      var stdSum: Double = stdDeviation * stdDeviation * count
//      stdSum = stdSum + (updatedMean - r1) * (updatedMean - r1)
//      val updatedStd: Double = scala.math.sqrt(stdSum / (count + 1))
//      val random = new Random()
//      val newPredicted = lastvalue * (1 + updatedMean * time + updatedStd * random.nextDouble() * scala.math.sqrt(time))
//      (newPredicted, updatedMean, updatedStd, count + 1, newPredicted, time + 1)
//    }
//
//
//    val simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy")
//    val dataAggregator = new DataAggregatorUtil()
//    val predictatedValue = calculateNextDayEnergyConsumption(inputRDD)
//    val hourlyAggregatedRDD = dataAggregator.hourlyDataAggregator(inputRDD)
//    val dailyAgreegatedRDD = dailyAggregationMethod(hourlyAggregatedRDD)
//    val sortedRDD = dailyAgreegatedRDD.sortByKey(false)
//    val (riList, mean) = calculateStandardMean(sortedRDD)
//    val stdDeviation = calculateStandardDeviation(sparkContext.makeRDD(riList), mean)
//    val calendar = Calendar.getInstance()
//    calendar.setTime(sortedRDD.toArray() {
//      0
//    }._1)
//    var finalArray = List[String]()
//    val totalCount = sortedRDD.count()
//    for (k <- Range(0, 52)) {
//
//      var array = Array.ofDim[Double](1000, 7)
//      for (i <- Range(0, 999)) {
//        var tempSet = weeklyAggregation(predictatedValue, mean, stdDeviation, totalCount, predictatedValue, 1)
//        for (j <- Range(0, 7)) {
//          tempSet = weeklyAggregation(tempSet._1, tempSet._2, tempSet._3, tempSet._4, tempSet._5, tempSet._6)
//          array(i)(j) = tempSet._1
//        }
//      }
//      for (i <- Range(0, 7)) {
//        calendar.add(Calendar.DATE, 1)
//        val predictedArray = new Array[Double](1000)
//        for (j <- Range(0, 999)) {
//          predictedArray(j) = array(j)(i)
//        }
//
//        val sortedArray = predictedArray.sorted
//        finalArray = finalArray ::: List(k + "," + i + "," + sortedArray {
//          10
//        } + "," + simpleDateFormat.format(calendar.getTime()))
//
//      }
//
//    }
//    finalArray
//  }
//
//
//  /**
//   * method to calculate the next day energy consumption 
//   */
//  def calculateNextDayEnergyConsumption(inputRDD: RDD[RecordDataFrame]) = {
//    /**
//     * Predicting using Geometric_Brownian_motion
//     */
//
//    val dataAggregator = new DataAggregatorUtil()
//    val hourlyAggregatedRDD = dataAggregator.hourlyDataAggregator(inputRDD)
//    val dailyAgreegatedRDD = dailyAggregationMethod(hourlyAggregatedRDD)
//    val sortedRDD = dailyAgreegatedRDD.sortByKey(false)
//    val (riList, mean) = calculateStandardMean(sortedRDD)
//    val stdDeviation = calculateStandardDeviation(sparkContext.makeRDD(riList), mean)
//    val lastValue = sortedRDD.toArray()(0)._2.totalPowerUsed
//    val date = sortedRDD.toArray()(0)._1
//    var predictedValues = List[Double]()
//    for (i <- Range(1, 1000)) {
//      val predictedValue = lastValue * (1 + mean * 1 + stdDeviation * scala.math.random * 1)
//      predictedValues ::= predictedValue
//    }
//    predictedValues = predictedValues.sorted
//    val predictatedValue = predictedValues(10)
//    val predictedValueKW = predictatedValue / 1000
//    predictedValueKW
//
//  }


}

