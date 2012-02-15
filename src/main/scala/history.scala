package nielinjie.app.toBeCloud
package domain

import java.util.Date
import scalaz._
import Scalaz._

class History(domain: Domain) {
  var items: List[DownloadHistory] = List()
  def byMountName() = {
    items.groupBy(_.mountName)
  }
  def tail(length: Int) = items.takeRight(length)
  def append(item: DownloadHistory) = {
    items = items :+ item
  }
  def load = items = domain.mounts.mounts.flatMap {
    mount =>
      mount.loadHistory
  }
  def save = byMountName.foreach {
    case (mountName, itms) =>
      domain.mounts.byName(mountName).foreach(mount => mount.saveHistory(itms.toList))
  }
}



class DownloadHistory(val startTime: Date, val transform: Transform) {
  var processed: Option[Int] = None
  var total: Option[Int] = None
  var done = false
  def mountName = transform.dis.mount.name
  def time = startTime
  def speed = processed.map(_ / ((new Date().getTime - startTime.getTime) / 60))
  def percent: Option[Int] = (processed |@| total) {
    case (p, t) =>
      (p / t) * 100
  }

  override def toString = {
    "Downloading from %s, %s/%s".format(transform.source.relativePath, processed, total)
  }
}