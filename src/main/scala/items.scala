package nielinjie.app.toBeCloud
package domain

import scala.util.control.Exception._
import java.io.File
import java.net.InetAddress
import nielinjie.util.io.FileUtil
import nielinjie.util.io.Logger
import config._
import java.net.URL

case object Ls

case class LsOk(items: List[RemoteItem])
case object Where
case class WhereOk(urls: List[URL])

case class Item(mount: Mount, file: File) {
  def remoteView: RemoteItem = RemoteItem(mount.name, relativePath)
  def relativePath = FileUtil.relativePath(mount.point, file)
}
case class RemoteItem(mountName: String, relativePath: String)
case class Transform(source: RemoteItem, dis: Item)

case class Mount(name: String, point: File) {
  def ls: List[Item] = {
    allCatch.opt {
      FileUtil.recursiveListFiles(point).map({
        file: File =>
          Item(this, file)
      })
    }.getOrElse(List())
  }
}

class Domain(config: Config) extends Logger {
  val mounts = new Mounts(config)
  val define = new Define(config)
  def ls(): List[Item] = {
    mounts.mounts.map(_.ls).flatten
  }

  val strategy = new NewStrategy
  def updated(incoming: List[RemoteItem]): List[Transform] = {
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
}

