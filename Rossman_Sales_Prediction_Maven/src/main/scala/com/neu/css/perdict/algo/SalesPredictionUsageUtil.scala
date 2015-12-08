package com.neu.css.perdict.algo

import org.apache.spark.rdd.RDD
import org.apache.spark.rdd.RDD.rddToPairRDDFunctions
import org.apache.log4j.{ Logger }
import org.apache.spark.sql.DataFrame
// ML Feature Creation, Tuning, Models, and Model Evaluation
import org.apache.spark.ml.feature.{ StringIndexer, VectorAssembler, OneHotEncoder }
import org.apache.spark.ml.tuning.{ ParamGridBuilder, TrainValidationSplit }
import org.apache.spark.ml.evaluation.{ RegressionEvaluator }
import org.apache.spark.ml.regression.{ RandomForestRegressor, LinearRegression }
import org.apache.spark.ml.Pipeline
import org.apache.spark.mllib.evaluation.RegressionMetrics

/**
 * lakshl
 */
object SalesPredictionUsageUtil {

  @transient lazy val logger = Logger.getLogger(getClass.getName)

  // Using indexer and oneHotEncoder to convert the label columns to dummy variables 
  
  val indexStateHoliday = new StringIndexer()
     .setInputCol("StateHoliday")
     .setOutputCol("StateHolidayIndex")
  
  val indexSchoolHoliday = new StringIndexer()
    .setInputCol("SchoolHoliday")
    .setOutputCol("SchoolHolidayIndex")
  
  val encodeStateHoliday = new OneHotEncoder()
    .setInputCol("StateHolidayIndex")
    .setOutputCol("StateHolidayVector")
  
  val encodeSchoolHoliday = new OneHotEncoder()
    .setInputCol("SchoolHolidayIndex")
    .setOutputCol("SchoolHolidayVector")
  
  val encodeDayOfMonth = new OneHotEncoder()
    .setInputCol("DayOfMonth")
    .setOutputCol("DayOfMonthVector")
  
  val encodeDayOfWeek = new OneHotEncoder()
    .setInputCol("DayOfWeek")
    .setOutputCol("DayOfWeekVector")
  
  val encodeStore = new OneHotEncoder()
    .setInputCol("Store")
    .setOutputCol("StoreVector")

  val vectorAssembler = new VectorAssembler()
    .setInputCols(Array("StoreVector", "DayOfWeekVector", "Open",
      "DayOfMonthVector", "StateHolidayVector", "SchoolHolidayVector"))
    .setOutputCol("features")

  // prepare the linear regression model for testing purpose only 
  def prepareLinearRegressionPipeline(): TrainValidationSplit = {
    
    val linearRegression = new LinearRegression()

    val paramGrid = new ParamGridBuilder()
      .addGrid(linearRegression.regParam, Array(0.1, 0.01))
      .addGrid(linearRegression.fitIntercept)
      .addGrid(linearRegression.elasticNetParam, Array(0.0, 0.25, 0.5, 0.75, 1.0))
      .build()

    val pipeline = new Pipeline()
      .setStages(Array(indexStateHoliday, indexSchoolHoliday,
        encodeStateHoliday, encodeSchoolHoliday, encodeStore,
        encodeDayOfWeek, encodeDayOfMonth,
        vectorAssembler, linearRegression))

    val trainValidationSplit = new TrainValidationSplit()
      .setEstimator(pipeline)
      .setEvaluator(new RegressionEvaluator)
      .setEstimatorParamMaps(paramGrid)
      .setTrainRatio(0.75)
      
    trainValidationSplit
  }

  // prepare the Random Forest Pipeline 
  def prepareRandomForestPipeline(): TrainValidationSplit = {
    
    val randomForest = new RandomForestRegressor()

    val paramGrid = new ParamGridBuilder()
      .addGrid(randomForest.minInstancesPerNode, Array(1, 5, 15))
      .addGrid(randomForest.maxDepth, Array(2, 4, 8))
      .addGrid(randomForest.numTrees, Array(20, 50, 100))
      .build()

    val pipeline = new Pipeline()
      .setStages(Array(indexStateHoliday, indexSchoolHoliday,
        encodeStateHoliday, encodeSchoolHoliday, encodeStore,
        encodeDayOfWeek, encodeDayOfMonth,
        vectorAssembler, randomForest))

    val trainValidationSplit = new TrainValidationSplit()
      .setEstimator(pipeline)
      .setEvaluator(new RegressionEvaluator)
      .setEstimatorParamMaps(paramGrid)
      .setTrainRatio(0.55)
      
    trainValidationSplit
  }

  // prepare the model from the train validation split 
  def prepareAndFitModel(transValidationSplit : TrainValidationSplit, data: DataFrame) = {
    
    val Array(training, test) = data.randomSplit(Array(0.8, 0.2), seed = 12345)
    
    logger.info("Fitting the transValidationSplit data")
    
    val model = transValidationSplit.fit(training)
    
    logger.info("Now performing test on hold out set")
    
    val holdout = model.transform(test).select("prediction", "label")

    // have to do a type conversion for RegressionMetrics
    val regressionMatrix = new RegressionMetrics(holdout.rdd.map(x =>
      (x(0).asInstanceOf[Double], x(1).asInstanceOf[Double])))

    // print the resultant matrix 
    logger.info("Result Metrics")
    logger.info("Result Explained Variance: " + regressionMatrix.explainedVariance)
    logger.info("Result R^2 Coeffieciant: " +  regressionMatrix.r2)
    logger.info("Result Mean Square Error : " + regressionMatrix.meanSquaredError)
    logger.info("Result Root Mean Squared Error : " + regressionMatrix.rootMeanSquaredError)
    
    model
  }

  // save the output in the csv
  def savePredictions(predictions: DataFrame, testRaw: DataFrame, filePath: String) = {
    
    val testdataOutput = testRaw
      .select("Id")
      .distinct()
      .join(predictions, testRaw("Id") === predictions("PredId"), "outer")
      .select("Id", "Sales")
      .na.fill(0: Double)
    
    // fill these with something
    testdataOutput
      .coalesce(1)
      .write.format("com.databricks.spark.csv")
      .option("header", "true")
      .save(filePath)
  }

}

