package com.neu.css.perdict.clean

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import java.text.SimpleDateFormat
import com.neu.css.perdict.model.StoreSalesDataFrame
import com.neu.css.perdict.model.StoreDataFrame

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

  // Map the input RDD to the correct format
  def covertToStoreSalesFormat(inputRDD: RDD[String]): RDD[StoreSalesDataFrame] = {

    val simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy")
    inputRDD.map(line => {
      val values = line.split(",")
      val storeSalesValue = new StoreSalesDataFrame()
      val storeId = values { 0 }
      val dayOfTheWeek = values { 1 }
      val dateValue = simpleDateFormat.parse(values { 2 })
      val dateString = simpleDateFormat.format(dateValue)
      storeSalesValue.id = values { 0 }.toLong
      storeSalesValue.storeID = values { 1 }.toInt
      storeSalesValue.date = dateString
      storeSalesValue.sales = values { 3 }.toLong
      storeSalesValue.customer = values { 4 }.toLong
      storeSalesValue.open = values { 5 }.toInt
      storeSalesValue.promo = values { 6 }.toInt
      storeSalesValue.stateHoliday = values { 7 }.toInt
      storeSalesValue.schoolHoliday = values { 8 }.toInt
      storeSalesValue
    })

  }

  // Map the input RDD to the correct of Sales data format
  def covertToStoreFormat(inputRDD: RDD[String]): RDD[StoreDataFrame] = {

    val simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy")
    inputRDD.map(line => {
      val values = line.split(";")
      val storeDataValue = new StoreDataFrame()
      val storeId = values { 0 }
      val dayOfTheWeek = values { 1 }
      val dateValue = simpleDateFormat.parse(values { 2 })
      val dateString = simpleDateFormat.format(dateValue)
      storeDataValue.storeID = values { 0 }.toInt
      storeDataValue.storeType = values { 1 }
      storeDataValue.assortment = values { 2 }
      storeDataValue.competitionDistance = values { 3 }.toLong
      storeDataValue.promo = values { 4 }.toInt
      storeDataValue.competitionOpenSinceMonth = values { 5 }.toInt
      storeDataValue.competitionOpenSinceYear = values { 6 }.toInt
      storeDataValue.promo2 = values { 7 }.toInt
      storeDataValue.promo2SinceWeek = values { 8 }.toInt
      storeDataValue.promo2SinceYear = values { 9 }.toInt
      storeDataValue.promoInterval = values { 10 }
      storeDataValue
    })
  } 
   
}





