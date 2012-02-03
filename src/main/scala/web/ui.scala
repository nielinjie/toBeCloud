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

trait UIPlan {
  this: Web =>
  object PeerE extends Params.Extract("peer", Params.first ~> Params.nonempty)
  def uiPlan = Planify {
    case Path(Seg("ui" :: "peers" :: Nil)) =>
      Ok ~> Json("peers" -> model.peers.map {
        peer =>
          ("peer" -> "%s:%s".format(peer.ip,peer.port))
      })
    case Path(Seg("ui" :: "diff" :: Nil)) & Params(PeerE(peer)) =>
      {
        Ok ~> Json("diff" -> status.diff(Peer.fromString(peer)).map {
          case Transform(remoteItem, item) =>
            ("mountName" -> remoteItem.mountName) ~ ("relativePath" -> remoteItem.relativePath)
        })
      }
  }
}

