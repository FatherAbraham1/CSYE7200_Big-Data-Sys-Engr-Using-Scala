package com.neu.css.perdict.model

/**
 * lakshl
 */
class StoreSalesDataFrame() extends Serializable {

  var id: Long = _
  var storeID: Int = _
  var dayOfTheWeek: Int = _
  var date: String = _
  var sales: Long = _
  var customer: Long = _
  var open: Int = _
  var promo: Int = _
  var stateHoliday: Int = _
  var schoolHoliday: Int = _

  override def toString: String = {
    id + ";" + storeID.toString + ";" + dayOfTheWeek.toString + ";" + date +
      ";" + sales + ";" + customer + ";" + open + ";" + promo + ";" + stateHoliday + ";" +
      schoolHoliday
  }

  override def equals(obj: scala.Any): Boolean = this.id == obj.asInstanceOf[StoreSalesDataFrame].id
}
