package com.neu.test

import org.scalatest._
import com.neu.css.perdict.clean.DataAggregatorUtil
import com.neu.css.perdict.algo.SalesPredictionUsageUtil
/**
 * @author llumba
 */
class RossmannSpec extends TestBaseSpec with GivenWhenThen with Matchers {
  
  "Spark Context" should "not be null" in {
   assertResult(2)(sc.parallelize(Seq(1,2,3)).filter(_ <= 2).map(_ + 1).count)
  }
  
  "Load test data" should "not be null" in {
    val rawTestData = DataAggregatorUtil.getRawdata(hiveContext,"src/test/resources/testSales.csv")
    rawTestData.registerTempTable("TEST_SALES_DATA")
    val storeCount = hiveContext.sql("""SELECT Store FROM TEST_SALES_DATA""").collect()
    storeCount should have length(20)
  }
  
  "Load train data" should "not be null" in {
    val rawTrainData = DataAggregatorUtil.getRawdata(hiveContext,"src/test/resources/trainSales.csv")
    rawTrainData.registerTempTable("TRAIN_SALES_DATA")
    val storeCount = hiveContext.sql("""SELECT Store FROM TRAIN_SALES_DATA""").collect()
    storeCount should have length(20)
  }
  
  "Open stores " should "be 20" in {
    val rawTrainData = DataAggregatorUtil.getRawdata(hiveContext,"src/test/resources/trainSales.csv")
    rawTrainData.registerTempTable("TRAIN_SALES_DATA")
    val storeCount = hiveContext.sql("""SELECT * FROM TRAIN_SALES_DATA  WHERE Open = 1 """).collect()
    storeCount should have length(20)
  }
  
  "Current Promo on stores " should "be 20" in {
    val rawTrainData = DataAggregatorUtil.getRawdata(hiveContext,"src/test/resources/trainSales.csv")
    rawTrainData.registerTempTable("TRAIN_SALES_DATA")
    val storeCount = hiveContext.sql("""SELECT * FROM TRAIN_SALES_DATA  WHERE Promo = 1 """).collect()
    storeCount should have length(20)
  }
  
  "School holiday " should "be effecting only 2 days" in {
    val rawTrainData = DataAggregatorUtil.getRawdata(hiveContext,"src/test/resources/trainSales.csv")
    rawTrainData.registerTempTable("TRAIN_SALES_DATA")
    val schoolHolidayEffect = hiveContext.sql("""SELECT * FROM TRAIN_SALES_DATA  WHERE SchoolHoliday = 0 """).collect()
    schoolHolidayEffect should have length(2)
  }
  
  "School holiday " should "not be effecting 18 Stores" in {
    val rawTrainData = DataAggregatorUtil.getRawdata(hiveContext,"src/test/resources/trainSales.csv")
    rawTrainData.registerTempTable("TRAIN_SALES_DATA")
    val schoolHolidayNotEffect = hiveContext.sql("""SELECT * FROM TRAIN_SALES_DATA  WHERE SchoolHoliday = 1 """).collect()
    schoolHolidayNotEffect should have length(18)
  }
  
  
  "State holiday " should "have any effect on store" in {
    val rawTrainData = DataAggregatorUtil.getRawdata(hiveContext,"src/test/resources/trainSales.csv")
    rawTrainData.registerTempTable("TRAIN_SALES_DATA")
    val stateHolidayEffect = hiveContext.sql("""SELECT * FROM TRAIN_SALES_DATA  WHERE StateHoliday = 1 """).collect()
    stateHolidayEffect should have length(0)
  }
  
  "State holiday " should "not have any effect on store" in {
    val rawTrainData = DataAggregatorUtil.getRawdata(hiveContext,"src/test/resources/trainSales.csv")
    rawTrainData.registerTempTable("TRAIN_SALES_DATA")
    val stateHolidayNotEffect = hiveContext.sql("""SELECT * FROM TRAIN_SALES_DATA  WHERE StateHoliday = 0 """).collect()
    stateHolidayNotEffect should have length 20
  }
  
  "Store Id 7 " should "have maximum sales" in {
    val rawTrainData = DataAggregatorUtil.getRawdata(hiveContext,"src/test/resources/trainSales.csv")
    rawTrainData.registerTempTable("TRAIN_SALES_DATA")
    val maxSales = hiveContext.sql("""SELECT Store, Sales FROM TRAIN_SALES_DATA WHERE Sales > 14000 """).collect()
    maxSales should have length 1
    maxSales.apply(0).get(0) == 7
    maxSales.apply(0).get(1) == 15344
  }
  
  
  "Store Id 7 " should "have maximum customers" in {
    val rawTrainData = DataAggregatorUtil.getRawdata(hiveContext,"src/test/resources/trainSales.csv")
    rawTrainData.registerTempTable("TRAIN_SALES_DATA")
    val maxCustomers = hiveContext.sql("""SELECT Store, Customers FROM TRAIN_SALES_DATA WHERE Customers > 1450""").collect()
    maxCustomers should have length 1
    maxCustomers.apply(0).get(0) == 4
    maxCustomers.apply(0).get(1) == 1498
  }
  
  "Load test data frame" should "not be null" in {
    val Array(testRawdata, testDataset) = DataAggregatorUtil.loadTestDataset(hiveContext,"src/test/resources/testSales.csv")
    testDataset.collect() should have length(0)
    testRawdata.collect() should have length(20)
  }
  
  "Load train data frame" should "not be null" in {
    val trainDF = DataAggregatorUtil.loadTrainDataset(hiveContext,"src/test/resources/trainSales.csv")
    trainDF.collect() should have length(0)
  }
  
  
  "Random Forest Pipeline" should "have transValidationSplit" in {
    val trainValidationSplit = SalesPredictionUsageUtil.prepareRandomForestPipeline()
    trainValidationSplit.getTrainRatio == 0.55
  }
  
  "Linear Regression Pipeline" should "have transValidationSplit" in {
    val trainValidationSplit = SalesPredictionUsageUtil.prepareLinearRegressionPipeline()
    trainValidationSplit.getTrainRatio == 0.75
  }
  
}