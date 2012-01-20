package nielinjie.app.toBeCloud
package domain

import scala.util.control.Exception._
import java.io.File
import java.net.InetAddress
import nielinjie.util.io.FileUtil
import nielinjie.util.io.Logger

case object Ls

case class LsOk(items: List[RemoteItem])
case object Where
case class WhereOk(address: InetAddress)

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

object Domain extends Logger {
  def ls(): List[Item] = {
    Mounts.mounts.map(_.ls).flatten
  }

}
object Mounts {
  def mounts: List[Mount] = Define.mounts
  def byName(name: String): Option[Mount] = mounts.find(_.name == name)
}

object Items {
  def updated(incoming: List[RemoteItem]): List[Transform] = {
    incoming.groupBy(_.mountName).map({
      case (name, remoteItems) =>
        Mounts.byName(name) match {
          case Some(mount) => updateInOneMount(remoteItems, mount)
          case None => List[Transform]()
        }
    }).flatten.toList
  }

  def updateInOneMount(remoteItems: List[RemoteItem], mount: Mount): List[Transform] = {
    val localItems = mount.ls
    remoteItems.filter({
      reItem =>
        !localItems.map(_.relativePath).contains(reItem.relativePath)
    }).map(reItem => Transform(reItem, Item(mount, new File(mount.point, reItem.relativePath))))
  }
}