package nielinjie.app.toBeCloud
package domain

import scala.util.control.Exception._
import java.io.File
import java.net.InetAddress
import nielinjie.util.io.FileUtil
import nielinjie.util.io.Logger
import config._
import comm._
import java.net.URL
import nielinjie.util.io.Serializer
import nielinjie.util.io.XStreamSerializer
import scalaz._
import Scalaz._
import java.net.URLEncoder

case object Ls

case class LsOk(items: List[RemoteItem])
case object Where
case class WhereOk(urls: List[URL])

case class Item(mount: Mount, file: File) {
  def remoteView: RemoteItem = RemoteItem(mount.name, relativePath)
  def relativePath = FileUtil.relativePath(mount.point, file)
}
case class RemoteItem(peer: Peer, mountName: String, relativePath: String)
case class Transform(source: RemoteItem, dis: Item) {

  //def save(input:Input)
}

case class Mount(name: String, point: File) {
  val historyFile = new File(point, ".toBeCloud/.history")
  val historyItemsS = new XStreamSerializer[List[DownloadHistory]]()
  def ls: List[Item] = {
    allCatch.opt {
      FileUtil.recursiveListFiles(point).map({
        file: File =>
          Item(this, file)
      })
    }.getOrElse(List())
  }
  def saveHistory(items: List[DownloadHistory]) = {
    FileUtil.needFile(historyFile).foreach(_ => FileUtil.toFile(historyItemsS.serialize(items), historyFile))
  }
  def loadHistory(): List[DownloadHistory] = {
    FileUtil.needFile(historyFile).toOption.join.map {
      file =>
        historyItemsS.unSerialize(FileUtil.fromFile(file))
    }.getOrElse(List())
  }
}

class Domain(val config: Config) extends Logger {
  val mounts = new Mounts(config)
  val define = new Define(config)
  val history = new History(this)
  val peer = de
  def ls(): List[Item] = {
    mounts.mounts.map(_.ls).flatten
  }

  val strategy = new NewStrategy
  def updated(incoming: List[RemoteItem]): List[Transform] = {
    //TODO think about downloading files
    incoming.groupBy(_.mountName).map({
      case (name, remoteItems) =>
        mounts.byName(name) match {
          case Some(mount) => strategy.need(remoteItems, mount)
          case None => List[Transform]()
        }
    }).flatten.toList
  }
  class Mounts(config: Config) {
    def mounts: List[Mount] = define.mounts
    def byName(name: String): Option[Mount] = mounts.find(_.name == name)

  }
  def urlForRemoteItem(remoteItem: RemoteItem) = {
    new URL("http://%s:%s/files/%s/%s".format(Env.getRootIp, config.webPort, URLEncoder.encode(remoteItem.mountName), URLEncoder.encode(remoteItem.relativePath)))
  }
}

