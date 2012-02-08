package nielinjie.app.toBeCloud
package domain

import java.util.Date

class History(domain: Domain) {
  var items: List[HistoryItem] = List()
  def byMountName() = {
    items.groupBy(_.mountName)
  }
  def tail(length: Int) = items.takeRight(length)
  def append(item: HistoryItem) = {
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

trait HistoryItem {
  def time: Date
  def mountName: String
}

class DownloadHistory(var time: Date, val transform: Transform) extends HistoryItem {
  var procssed: Int = _
  var total: Int = _
  var done = false
  def mountName = transform.dis.mount.name
  override def toString = {
    "Downloading from %s, %d/%d".format(transform.source.relativePath, procssed, total)
  }
}