package com.neu.css.perdict.algo

import org.apache.spark.rdd.RDD
import org.apache.spark.SparkContext._
import org.apache.spark.SparkContext
import java.text.SimpleDateFormat
import com.neu.css.perdict.clean.DataAggregatorUtil
import org.apache.spark.rdd.RDD.rddToOrderedRDDFunctions
import org.apache.spark.rdd.RDD.rddToPairRDDFunctions
import scala.Range

/**
 * lakshl
 */
class SalesMetricsCalculatorUtil(sparkContext:SparkContext) {

//   /**
//    * method to get the per hour revenue 
//    */
//  def getPerHourRevenueEnergyConsumption(inputRDD:RDD[RecordDataFrame]):RDD[RecordDataFrame] = {
//    val dataAggregator = new DataAggregatorUtil()
//    val hourlyRDD = dataAggregator.hourlyDataAggregator(inputRDD)
//    hourlyRDD.map(value => {
//      var ((date, hour), recordValue) = value
//      val hourOfDay = recordValue.hourofDay
//      if ((hourOfDay >= 0 && hourOfDay <= 4) || (hourOfDay >= 10 && hourOfDay <= 15)) {
//        recordValue.totalCost = (recordValue.subMetering1 * 4 + recordValue.subMetering2 * 4 + recordValue.subMetering3 * 4) / 1000
//      } else if ((hourOfDay >= 5 && hourOfDay <= 6) || (hourOfDay >= 16 && hourOfDay <= 19) || (hourOfDay >= 22 && hourOfDay <= 23)) {
//        recordValue.totalCost = (recordValue.subMetering1 * 6 + recordValue.subMetering2 * 6 + recordValue.subMetering3 * 6) / 1000
//      } else if (hourOfDay >= 7 && hourOfDay <= 9) {
//        recordValue.totalCost = (recordValue.subMetering1 * 12 + recordValue.subMetering2 * 12 + recordValue.subMetering3 * 12) / 1000
//      } else {
//        recordValue.totalCost = (recordValue.subMetering1 * 10 + recordValue.subMetering2 * 10 + recordValue.subMetering3 * 10) / 1000
//      }
//
//      recordValue
//    })
//  }
//
//  /**
//   * get the Average Revenue Loss Per Day
//   */
//  def getAverageRevenueLossPerDay(inputRDD:RDD[RecordDataFrame]):Double = {
//    val dataAggregator = new DataAggregatorUtil()
//    val revenueRDD= getPerHourRevenueEnergyConsumption(inputRDD)
//    val dailyRDD = dataAggregator.dailyDataAggregator(revenueRDD)
//    val totalCostCalcRDD = dailyRDD.map(value => ("sum", value._2.totalCost)).reduceByKey((a, b) => a + b)
//    val revenueLossForDayOutage = totalCostCalcRDD.first()._2 / dailyRDD.count()
//    revenueLossForDayOutage
//
//  }
//
//  /**
//   * get the peak Load Value for Weekend and Week days
//   */
//  def getPeekLoadWeekly(inputRDD:RDD[RecordDataFrame]):(Double,Double)  = {
//    def findNextMonthPeakLoad(rdd:RDD[((Int,Long),RecordDataFrame)],sparkContext:SparkContext) : Double={
//      def standardMean(inputRDD: RDD[((Int,Long),RecordDataFrame)]): (List[Double], Double) = {
//        val count = inputRDD.count()
//        var sum = 0.0
//        var riList = List[Double]()
//        for (i <- Range(1, count.toInt)) {
//          val firstRecord = inputRDD.toArray()(i)
//          val secondRecord = inputRDD.toArray()(i - 1)
//          val difference = (firstRecord._2.totalPowerUsed - secondRecord._2.totalPowerUsed) / firstRecord._2.totalPowerUsed
//          riList = riList ::: List(difference)
//          sum += difference
//        }
//
//        (riList, sum / count)
//
//      }
//      /**
//       * get standard Deviation of input value  
//       */
//      def standardDeviation(inputRDD:RDD[Double],mean:Double): Double = {
//        val sum = inputRDD.map(value => {
//          (value - mean) * (value - mean)
//        }).reduce((firstValue, secondValue) => {
//          firstValue + secondValue
//        })
//        scala.math.sqrt(sum / inputRDD.count())
//      }
//
//
//      val (rList,mean) = standardMean(rdd)
//      val stdDeviation = standardDeviation(sparkContext.makeRDD(rList),mean)
//      val sortedRdd=rdd.sortByKey(false)
//      val lastValue = sortedRdd.first()._2.totalPowerUsed
//      var newValues = List[Double]()
//      for(i<- Range(0,1000)){
//        val predictedValue = lastValue * (1 + mean * 1 + stdDeviation * scala.math.random * 1)
//        newValues::=predictedValue
//      }
//      val sorted = newValues.sorted
//      val value = sorted(10)/1000
//      value
//    }
//
//    /**
//     * get the Peak time values for the given hours  
//     */
//    def peakTimeValue(inputRDD: RDD[RecordDataFrame]): RDD[RecordDataFrame] = {
//      inputRDD.filter(recordValue => (recordValue.hourofDay >= 7 && recordValue.hourofDay <= 10))
//    }
//
//    /**
//     * get the peak time load value for weekend
//     */
//    def peakTimeValueForWeekend(inputRDD: RDD[RecordDataFrame]): RDD[RecordDataFrame] = {
//      val dateFormat = new SimpleDateFormat("dd/MM/yyyy")
//      inputRDD.filter(recordValue => ((dateFormat.parse(recordValue.date).getDay == 0) || (dateFormat.parse(recordValue.date).getDay == 6)))
//    }
//
//    /**
//     * get the peek time load values for week day 
//     */
//    def peakTimeRecordForWeekday(inputRDD: RDD[RecordDataFrame]): RDD[RecordDataFrame] = {
//      val dateFormat = new SimpleDateFormat("dd/MM/yyyy")
//      inputRDD.filter(recordValue => !((dateFormat.parse(recordValue.date).getDay == 0) || (dateFormat.parse(recordValue.date).getDay == 6)))
//    }
//
//    val dataAggregator = new DataAggregatorUtil()
//    val revenueRDD = getPerHourRevenueEnergyConsumption(inputRDD)
//    var peakTimeRecordRDD = peakTimeValue(revenueRDD)
//    var weekendPeakTimeValue = peakTimeValueForWeekend(peakTimeRecordRDD)
//    var weekdayPeakTimeValue = peakTimeRecordForWeekday(peakTimeRecordRDD)
//    var weekendPeakTimeMonthlyRDD = dataAggregator.monthlyDataAggregator(weekendPeakTimeValue)
//    var weekdayPeakTimeMonthlyRDD = dataAggregator.monthlyDataAggregator(weekdayPeakTimeValue)
//    val peakTimeLoadWeekday = findNextMonthPeakLoad(weekdayPeakTimeMonthlyRDD, sparkContext)
//    val peakTimeLoadWeekend = findNextMonthPeakLoad(weekendPeakTimeMonthlyRDD, sparkContext)
//    (peakTimeLoadWeekday,peakTimeLoadWeekend)
//
//  }

}
