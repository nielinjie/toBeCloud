package nielinjie.app.toBeCloud
package web

import unfiltered.filter.Plan
import unfiltered.request._
import unfiltered.response._
import config._
import domain._
import comm._
import nielinjie.util.io.XStreamSerializer
import unfiltered.filter.Planify
import net.liftweb.json.JsonDSL._
import sjson.json._
import JsonSerialization._
import java.io.File
import dispatch.json.JsValue
import javax.servlet.http.HttpServletResponse

trait UIPlan {
  this: Web =>
  object PeerE extends Params.Extract("peer", Params.first ~> Params.nonempty)
  object HistoryLengthE extends Params.Extract("historyLength", Params.first ~> Params.nonempty)
  object MountNameE extends Params.Extract("source.mountName", Params.first ~> Params.nonempty)
  object RelativePathE extends Params.Extract("source.relativePath", Params.first ~> Params.nonempty)
  object DistMountNameE extends Params.Extract("dist.mountName", Params.first ~> Params.nonempty)
  object DistFileE extends Params.Extract("dist.file", Params.first ~> Params.nonempty)
  import JsonFormaters._
  object SJson {
    def apply[T](block: => T)(implicit sjson: Format[T]): ResponseFunction[HttpServletResponse] = {
      JsonContent ~> ResponseString(tojson(block)(sjson).toString)
    }
  }
  def uiPlan = Planify {
    case Path(Seg("ui" :: "peers" :: Nil)) =>
      Ok ~> SJson(model.peers)
    case Path(Seg("ui" :: "diff" :: Nil)) & Params(PeerE(peer)) => {
      Ok ~> Json("diff" -> status.diff(Peer.fromString(peer)).map {
        case Transform(remoteItem, item) =>
          ("peer" -> peer) ~ ("source" -> ("mountName" -> remoteItem.mountName) ~ ("relativePath" -> remoteItem.relativePath)) ~ ("dist" -> ("mountName" -> item.mount.name) ~ ("file" -> item.file.toString))
      })
    }
    case Path(Seg("ui" :: "download" :: Nil))
      & Params(PeerE(peer)
        & MountNameE(mountName)
        & RelativePathE(relativePath)
        & DistMountNameE(distMountName)
        & DistFileE(file)
      ) => {
      domain.mounts.byName(distMountName).foreach {
        mount =>
          status.download(Peer.fromString(peer), Transform(RemoteItem(mountName, relativePath), Item(mount, new File(file))))
      }
      Ok ~> ResponseString("OK")
    }
    case Path(Seg("ui" :: "history" :: Nil)) /*& Params(HistoryLengthE(historyLength))*/ => {
      Ok ~> Json(status.history(10).map {
        item =>
          item.toString
      })
    }
  }
}

object JsonFormaters extends DefaultProtocol {
  implicit val peerFormat: Format[Peer] = asProduct2("ip", "port")(Peer.apply)(Peer.unapply(_).get)
  //TODO is there any necessary make all response to pure json?
//  implicit val remoteItemFormat:Format[RemoteItem] = asProduct2("mountName","relativePath")(RemoteItem.apply)(RemoteItem.unapply(_).get)
//  implicit val itemFormat:Format[Item] = asProduct2("")
  
}

