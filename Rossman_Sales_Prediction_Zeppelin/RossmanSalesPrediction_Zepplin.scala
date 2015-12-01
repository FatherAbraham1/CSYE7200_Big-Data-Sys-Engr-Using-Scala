import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.regression.LinearRegressionWithSGD
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.optimization.{SimpleUpdater, SquaredL2Updater, L1Updater}
// this is used to implicitly convert an RDD to a DataFrame.
import sqlContext.implicits._

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


val sqlContext = new org.apache.spark.sql.SQLContext(sc)

val csv = sc.textFile("rossman_train.csv")
  // split / clean data
  val headerAndRows = csv.map(line => line.split(",").map(_.trim))
  // get header
  val header = headerAndRows.first
  // filter out header (eh. just check if the first val matches the first header name)
  val data = headerAndRows.filter(_(0) != header(0))
  // splits to map (header/value pairs)
  
  
val date_reg = """(\d\d\d\d)-(\d\d)-(\d\d)""".r
case class RossmannRecord(store: Double, dayofweek: Double, date: String, sales: Double, customers: Double, open: Double, promo: Double, stateholiday: String, schoolholiday: String, dayofmonth: Double)

// this is used to implicitly convert an RDD to a DataFrame.
import sqlContext.implicits._
val rossmann = data.map(r =>RossmannRecord(r(0).toDouble,r(1).toDouble,r(2).trim,r(3).toDouble,r(4).toDouble,r(5).toDouble,r(6).toDouble,r(7)replace("\"", ""),r(8).replace("\"", ""),r(2) match {case date_reg(y,m,d) => s"$d".toDouble})).toDF()
rossmann.registerTempTable("rossmann")
val trainDataset = sqlContext.sql("SELECT sales as label, store, open, dayofweek, stateholiday, schoolholiday, dayofmonth  FROM rossmann")


 val indexStateHoliday = new StringIndexer()
     .setInputCol("stateholiday")
     .setOutputCol("StateHolidayIndex")
  
  val indexSchoolHoliday = new StringIndexer()
    .setInputCol("schoolholiday")
    .setOutputCol("SchoolHolidayIndex")
  
  val encodeStateHoliday = new OneHotEncoder()
    .setInputCol("StateHolidayIndex")
    .setOutputCol("StateHolidayVector")
  
  val encodeSchoolHoliday = new OneHotEncoder()
    .setInputCol("SchoolHolidayIndex")
    .setOutputCol("SchoolHolidayVector")
  
  val encodeDayOfMonth = new OneHotEncoder()
    .setInputCol("dayofmonth")
    .setOutputCol("DayOfMonthVector")
  
  val encodeDayOfWeek = new OneHotEncoder()
    .setInputCol("dayofweek")
    .setOutputCol("DayOfWeekVector")
  
  val encodeStore = new OneHotEncoder()
    .setInputCol("store")
    .setOutputCol("StoreVector")

  val vectorAssembler = new VectorAssembler()
    .setInputCols(Array("StoreVector", "DayOfWeekVector", "open",
      "DayOfMonthVector", "StateHolidayVector", "SchoolHolidayVector"))
    .setOutputCol("features")
	
	
  // prepare the linear regression model for testing purpose only 
  def prepareLinearRegressionPipeline(): TrainValidationSplit = {
    
    val linearRegression = new LinearRegression()

    val paramGrid = new ParamGridBuilder()
      .addGrid(linearRegression.regParam, Array(0.1,0.01))
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
  
  val linearTvs = prepareLinearRegressionPipeline()
  
    def prepareAndFitModel(transValidationSplit : TrainValidationSplit, data: DataFrame) = {
    
    val Array(training, test) = data.randomSplit(Array(0.8, 0.2), seed = 12345)
    
    //logger.info("Fitting the transValidationSplit data")
    
    val model = transValidationSplit.fit(training)
    
    //logger.info("Now performing test on hold out set")
    
    val holdout = model.transform(test).select("prediction", "label")

    // have to do a type conversion for RegressionMetrics
    val regressionMatrix = new RegressionMetrics(holdout.rdd.map(x =>
      (x(0).asInstanceOf[Double], x(1).asInstanceOf[Double])))

    // print the resultant matrix 
    print("Result Metrics")
   
    print("Result Root Mean Squared Error : " + regressionMatrix.rootMeanSquaredError)
    
    model
  }
  
  
  val trainDataset = sqlContext.sql("SELECT sales as label, store, open, dayofweek, stateholiday, schoolholiday, dayofmonth, customers  FROM rossmann where open=1 AND sales>0  ")
  
  
   val linearModel = prepareAndFitModel(linearTvs, trainDataset)
   
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
  
  
  val testcsv = sc.textFile("test.csv")
  // split / clean data
  val testheaderAndRows = testcsv.map(line => line.split(",").map(_.trim))
  // get header
  val testheader = testheaderAndRows.first
  // filter out header (eh. just check if the first val matches the first header name)
  val testdata = testheaderAndRows.filter(_(0) != testheader(0))
  
  
  val date_reg = """(\d\d\d\d)-(\d\d)-(\d\d)""".r
case class TestRossmannRecord(id:Double ,store: Double, dayofweek: Double, date: String, open: Double, promo: Double, stateholiday: String, schoolholiday: String, dayofmonth: Double)

// this is used to implicitly convert an RDD to a DataFrame.
import sqlContext.implicits._
val test_rossmann = testdata.map(r =>TestRossmannRecord(r(0).toDouble,r(1).toDouble,r(2).toDouble,r(3).trim,r(4) match {case "" => 0 case _ => r(4).toDouble},r(5).toDouble,r(6).replace("\"", ""),r(7).replace("\"", ""),r(3) match {case date_reg(y,m,d) => s"$d".toDouble})).toDF()
test_rossmann.registerTempTable("test_rossmann")


val testDataset = sqlContext.sql("SELECT  id,store, open, dayofweek, stateholiday, schoolholiday, dayofmonth  FROM test_rossmann where open=1   ")


testDataset.show()

val linearRegOutput = linearModel.transform(testDataset)
      .withColumnRenamed("prediction", "Sales")
      .withColumnRenamed("id", "PredId")
      .select("PredId", "Sales")
	  
 def savePredictions(predictions: DataFrame, testRaw: DataFrame, filePath: String) = {
    
    val testdataOutput = testRaw
      .select("id")
      .distinct()
      .join(predictions, testRaw("id") === predictions("PredId"), "outer")
      .select("id", "Sales")
      .na.fill(0: Double)
    
    
    testdataOutput.show()
    
    testdataOutput.rdd.saveAsTextFile(filePath)
 
  }

  savePredictions(linearRegOutput, testDataset, "sales_prediction_result.csv")