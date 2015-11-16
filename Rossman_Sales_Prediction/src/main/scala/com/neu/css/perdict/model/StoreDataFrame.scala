package com.neu.css.perdict.model

/**
 * lakshl
 */
class StoreDataFrame() extends Serializable with Equals {

  var storeID: Int = _
  var storeType: String = _
  var assortment: String = _
  var competitionDistance: Long = _
  var promo: Int = _
  var competitionOpenSinceMonth: Int = _
  var competitionOpenSinceYear: Int = _
  var promo2 :Int = _
  var promo2SinceWeek: Int = _
  var promo2SinceYear: Int = _
  var promoInterval: String = _

  
  
  override def toString: String = {
    storeID + ";" + storeType + ";" + assortment + ";" + competitionDistance +
      ";" + promo + ";" + competitionOpenSinceMonth + ";" + competitionOpenSinceYear + ";" +
      + promo2 + ";" + promo2SinceWeek + ";" + promo2SinceYear + ";" + promoInterval
  }
  
  override def equals(obj: scala.Any): Boolean = this.storeID == obj.asInstanceOf[StoreDataFrame].storeID
  
}
