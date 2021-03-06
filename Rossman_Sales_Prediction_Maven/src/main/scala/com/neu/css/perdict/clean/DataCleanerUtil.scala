package com.neu.css.perdict.clean

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import java.text.SimpleDateFormat

/**
 * lakshl
 */
class DataCleanerUtil {

  /**
   * remove the missing values from the input txt and the header of the files
   */
  def removeMissingValues(inputRDD: RDD[String]): RDD[String] = {

    inputRDD.filter(line => !(line.contains("?") || (line.contains("CompetitionOpenSinceMonth;CompetitionOpenSinceYear"))))
    // filter the values 
    // check only for stores which are open and sales 
    // sales and customers should be greater than 0
    inputRDD.filter(line => {
      val values = line.split(",")
      !(values { 5 }.toInt == 0 || values { 3 }.toLong == 0 || values { 4 }.toLong == 0)
    })
  }
   
}





