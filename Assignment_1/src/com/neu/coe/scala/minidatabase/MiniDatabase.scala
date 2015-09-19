package com.neu.coe.scala.minidatabase

import scala.io.Source

/**
 * lumba.l
 */
object MiniDatabase {
  
  def load(filename: String) = {
    val src = Source.fromFile(filename)
    val database = src.getLines.toList.map(e => Entry(e.split(",")))
    val result = database.toSeq
    src.close
    result
  }
  
  def measure(height: Height) = height match {
    case Height(8,_) => "giant"
    case Height(7,_) => "very tall"
    case Height(6,_) => "tall"
    case Height(5,_) => "normal"
    case Height(_,_) => "short"
  }

  def main(args: Array[String]): Unit = {

    if (args.length > 0) {

      val db = load(args(0))
      print(db)

    } else {

      var filePath = "src/com/neu/coe/scala/minidatabase/minidatabase.csv"
      val db = load(filePath)
      print(db)
      
    }
  }
}



/**
 * case class to retrieve the customer details
 */

case class Entry(name: Name, social: Social, dob: Date, height: Height, weight: Int)

object Entry {
  
  def apply(name: String, social: String, dob: String, height: String, weight: String): Entry =
    Entry(Name(name),Social(social),Date(dob),Height(height),weight.toInt)
  def apply(entry: Seq[String]): Entry = apply(entry(0).trim(),entry(1).trim(),entry(2).trim(),entry(3).trim(),entry(4).trim())

//  def apply(height: String): Entry =
//    Entry(Height(height))
//  def apply(entry: Seq[String]): Entry = apply(entry(3))
  
//  def apply(weight: String): Entry =
//    Entry(Weight(weight))
//  def apply(entry: Seq[String]): Entry = apply(entry(4))
  
//  def apply(ssn: String): Entry =
//    Entry(Social(ssn))
//  def apply(entry: Seq[String]): Entry = apply(entry(1))
  
//   def apply(name: String): Entry =
//    Entry(Name(name))
//  def apply(entry: Seq[String]): Entry = apply(entry(0))
  
//   def apply(date: String): Entry =
//    Entry(Date(date))
//   def apply(entry: Seq[String]): Entry = apply(entry(2))
  
}

//case class Entry(date: Date)

case class Height(feet: Int, in: Int) {
  def inches = feet*12+in
}

/**
 * case class to retrieve the height of the person 
 */

object Height {
  val rHeightFtIn = """^\s*(\d+)\s*(?:ft|\')(\s*(\d+)\s*(?:in|\"))?\s*$""".r
  def apply(ft: String, in: String) = new Height(ft.toInt,in.toInt)
  def apply(ft: Int) = new Height(ft,0)
  def apply(height: String): Height = height match {
    case rHeightFtIn(ft,_,in) => Height(ft,in)
    case rHeightFtIn(ft) => Height(ft.toInt)
    case _ => throw new IllegalArgumentException(height)
  }
}

/**
 * case class to retrieve the name of the person 
 */

case class Name(first: String, middle: String, last: String)

object Name {
  
  val namePattern = """(?m)\s*(\w+)\s+(\D+)\s+(\w+)\s*+$""".r
  
  def apply(name: String): Name = name match {
    
    case namePattern(first,middle,last) => Name(first,middle,last)
    case _ => throw new IllegalArgumentException(name)
    
  }
}

/**
 * case class to retrieve the date pattern 
 */

case class Date(month: String, day: String, year: String)

object Date {
  
  val datePattern = """(?m)\s*(\S+)\s+(\S+)\s+(\S+)\s*+$""".r
  
  val datePattern2 =  """(?m)\s*(\d+)/(\d+)/(\d{2,4})\s*+$""".r
  
  def apply(date: String): Date = date match {
    case datePattern(month,day,year) => Date(month,day,year)
    case datePattern2(month,day,year) => Date(month,day,year)
    case _ => throw new IllegalArgumentException(date)
  }
}


/**
 * case class to retrieve the SSN of person 
 */

case class Social(are: Int, group: Int, serial: Int)

object Social {
  
  val ssnPattern = """(?m)\s*([0-9]{1,4})[ -]([0-9]{1,3})[ -]([0-9]{1,5})\s*+$""".r
  
  def apply(ssn: String): Social = ssn match {
    case ssnPattern(are,group,serial) => Social(are.toInt,group.toInt,serial.toInt)
    case _ => throw new IllegalArgumentException(ssn)
  } 
}


/**
 * case class to retrieve the weight of person 
 */

case class Weight(weight: Int)

object Weight {
  
  val rWeight = """(?m)\s*(\d+)\s*+$""".r
  
  def apply(weight: String): Weight = weight match {
    case rWeight(weight) => Weight(weight.toInt)
    case _ => throw new IllegalArgumentException(weight)
  }
}