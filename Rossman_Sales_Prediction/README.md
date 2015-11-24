### Instructions:

Rossmann Sales Prediction

Run the folling command :

mvn clean

mvn install

maven test

C:/spark-1.5.1-bin-hadoop2.6/bin/spark-submit --packages com.databricks:spark-csv_2.11:1.1.0 --class "com.neu.css.perdict.RossmannSalesPerdiction" --master local[1] target/Rossmann-Sales-0.0.1-jar-with-dependencies.jar C:/Users/llumba/Desktop/Scala/Final_Project/Data/train.csv C:/Users/llumba/Desktop/Scala/Final_Project/Data/test.csv