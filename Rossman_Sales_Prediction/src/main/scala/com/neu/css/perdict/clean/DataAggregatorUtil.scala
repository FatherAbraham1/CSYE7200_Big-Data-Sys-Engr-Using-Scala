package com.neu.css.perdict.clean

import org.apache.spark.SparkContext._
import org.apache.spark.rdd.RDD
import org.apache.spark.rdd.RDD.rddToPairRDDFunctions

/**
 * lakshl
 */
class DataAggregatorUtil {

  
//  /**
//   * method to calculate the hourly data Aggregator
//   */
//  def hourlyDataAggregator(inputRDD: RDD[RecordDataFrame]): RDD[((String, Long), RecordDataFrame)] = {
//    val groupRDD = inputRDD.map(recordValue => ((recordValue.date, recordValue.hourofDay), recordValue)).reduceByKey((firstRecord, secondRecord) => {
//      val recordValue = new RecordDataFrame()
//
//      recordValue.date = firstRecord.date
//      recordValue.day = firstRecord.day
//      recordValue.month = firstRecord.month
//      recordValue.year = firstRecord.year
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
//
//    })
//    groupRDD
//
//
//  }
//
//  /**
//   * method to calculate the daily data Aggregator
//   */
//  def dailyDataAggregator(inputRDD: RDD[RecordDataFrame]): RDD[(String, RecordDataFrame)] = {
//    val groupRDD = inputRDD.map(recordValue => (recordValue.date, recordValue)).reduceByKey((firstRecord, secondRecord) => {
//      val recordValue = new RecordDataFrame()
//
//      recordValue.date = firstRecord.date
//      recordValue.day = firstRecord.day
//      recordValue.month = firstRecord.month
//      recordValue.year = firstRecord.year
//      recordValue.subMetering1 = firstRecord.subMetering1 + secondRecord.subMetering1
//      recordValue.subMetering2 = firstRecord.subMetering2 + secondRecord.subMetering2
//      recordValue.subMetering3 = firstRecord.subMetering3 + secondRecord.subMetering3
//      recordValue.totalCost = firstRecord.totalCost + secondRecord.totalCost
//
//      recordValue.activePower = firstRecord.activePower + secondRecord.activePower
//      recordValue.reactivePower = firstRecord.reactivePower + secondRecord.reactivePower
//
//      recordValue.voltage = (firstRecord.voltage + secondRecord.voltage) / 2
//      recordValue.globalIntensity = (firstRecord.globalIntensity + secondRecord.globalIntensity) / 2
//      recordValue
//
//    })
//    groupRDD
//
//
//  }
//
//  /**
//   * method to calculate the monthly data Aggregator
//   */
//  def monthlyDataAggregator(inputRDD: RDD[RecordDataFrame]): RDD[((Int, Long), RecordDataFrame)] = {
//    val groupRDD = inputRDD.map(recordValue => ((recordValue.month, recordValue.year), recordValue)).reduceByKey((firstRecord, secondRecord) => {
//      val recordValue = new RecordDataFrame()
//
//      recordValue.date = firstRecord.date
//      recordValue.day = firstRecord.day
//      recordValue.month = firstRecord.month
//      recordValue.year = firstRecord.year
//      recordValue.subMetering1 = firstRecord.subMetering1 + secondRecord.subMetering1
//      recordValue.subMetering2 = firstRecord.subMetering2 + secondRecord.subMetering2
//      recordValue.subMetering3 = firstRecord.subMetering3 + secondRecord.subMetering3
//      recordValue.totalCost = firstRecord.totalCost + secondRecord.totalCost
//
//      recordValue.activePower = firstRecord.activePower + secondRecord.activePower
//      recordValue.reactivePower = firstRecord.reactivePower + secondRecord.reactivePower
//
//      recordValue.voltage = (firstRecord.voltage + secondRecord.voltage) / 2
//      recordValue.globalIntensity = (firstRecord.globalIntensity + secondRecord.globalIntensity) / 2
//      recordValue
//
//    })
//    groupRDD
//
//
//  }
//  
//  /**
//   * method to calculate the monthly data Aggregator
//   */
//  def yearlyDataAggregator(inputRDD: RDD[RecordDataFrame]): RDD[(Long, RecordDataFrame)] = {
//    val groupRDD = inputRDD.map(recordValue => (recordValue.year, recordValue)).reduceByKey((firstRecord, secondRecord) => {
//      val recordValue = new RecordDataFrame()
//
//      recordValue.date = firstRecord.date
//      recordValue.day = firstRecord.day
//      recordValue.month = firstRecord.month
//      recordValue.year = firstRecord.year
//      recordValue.subMetering1 = firstRecord.subMetering1 + secondRecord.subMetering1
//      recordValue.subMetering2 = firstRecord.subMetering2 + secondRecord.subMetering2
//      recordValue.subMetering3 = firstRecord.subMetering3 + secondRecord.subMetering3
//      recordValue.totalCost = firstRecord.totalCost + secondRecord.totalCost
//
//      recordValue.activePower = firstRecord.activePower + secondRecord.activePower
//      recordValue.reactivePower = firstRecord.reactivePower + secondRecord.reactivePower
//
//      recordValue.voltage = (firstRecord.voltage + secondRecord.voltage) / 2
//      recordValue.globalIntensity = (firstRecord.globalIntensity + secondRecord.globalIntensity) / 2
//      recordValue
//
//    })
//    groupRDD

  }
}





