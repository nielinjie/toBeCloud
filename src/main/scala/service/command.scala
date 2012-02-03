package nielinjie.app.toBeCloud
package web

import unfiltered.filter.Plan
import unfiltered.request._
import unfiltered.response._
import config._
import domain._
import nielinjie.util.io.XStreamSerializer
import unfiltered.filter.Planify

trait CommandPlan {
  this: Web =>
  val lsOkS = new XStreamSerializer[LsOk]()
  val itemS = new XStreamSerializer[RemoteItem]()
  val whereOkS = new XStreamSerializer[WhereOk]()
  def commandPlan = Planify {
    case Path(Seg("command" :: "ls" :: Nil)) =>
      ResponseString(lsOkS.serialize((LsOk(domain.ls().map(_.remoteView)))))
    case (Path(Seg("command" :: "where" :: Nil)) & Params(params)) => {
      val items = params.get("items")
      ResponseString(
        whereOkS.serialize(
          WhereOk(
            items.getOrElse(Seq[String]())
              .map {
                item: String => domain.urlForRemoteItem(itemS.unSerialize(item))
              }
              .toList)))
    }
  }
}