package com.neu.css.perdict

import org.apache.log4j.{ Logger }
import org.apache.spark.{ SparkConf, SparkContext }
import org.apache.spark.sql.hive.HiveContext
import com.neu.css.perdict.clean.DataAggregatorUtil
import com.neu.css.perdict.algo.SalesPredictionUsageUtil

object RossmannSalesPerdiction extends Serializable {

  @transient lazy val logger = Logger.getLogger(getClass.getName)

  val ROSSMANN_SALES_PREDICTION_LINEAR_REG = "Rossmann Sales Prediction with Linear Regression"
  val SPARK_STORAGE_MEMORY_FRACTION = "spark.storage.memoryFraction"
  val SPARK_STORAGE_MEMORY_VALUE = "1"
  val SET_UP_MESSAGE_COMPLETION = "Spark Set Up Complete"
  val STARTING_RANDOM_FOREST_EVALUATION = "Evaluating Model with Linear Regression"
  val SALES_PREDICTION_RESULT_CSV = "sales_prediction_result.csv"
  
  // start the main program
  def main(args: Array[String]) = {

    val name = ROSSMANN_SALES_PREDICTION_LINEAR_REG

    logger.info(s"Starting up $name")

    val conf = new SparkConf().setAppName(name)
    conf.set(SPARK_STORAGE_MEMORY_FRACTION, SPARK_STORAGE_MEMORY_VALUE);
    val sc = new SparkContext(conf)
    val hiveContext = new HiveContext(sc)

    logger.info(SET_UP_MESSAGE_COMPLETION)

    // load the training data set
    val trainDataset = DataAggregatorUtil.loadTrainDataset(hiveContext, args(0))
    
    // load the test data set
    val Array(testRawdata, testDataset) = DataAggregatorUtil.loadTestDataset(hiveContext, args(1))

    // The Preparing the Spark Random Forest Pipeline for using ML library 
    val randomForestTvs = SalesPredictionUsageUtil.prepareRandomForestPipeline()
    logger.info(STARTING_RANDOM_FOREST_EVALUATION)

    //Preparing the Random Forest Model
    val randomForestModel = SalesPredictionUsageUtil.prepareAndFitModel(randomForestTvs, trainDataset)

    //Transform the test data set according to the model 
    val randomForestOutput = randomForestModel.transform(testDataset)
      .withColumnRenamed("prediction", "Sales")
      .withColumnRenamed("Id", "PredId")
      .select("PredId", "Sales")

    SalesPredictionUsageUtil.savePredictions(randomForestOutput, testRawdata, SALES_PREDICTION_RESULT_CSV)

  }

}
