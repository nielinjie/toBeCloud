package nielinjie.app.toBeCloud
package web

import comm._
import domain._
import java.net.URL
import dispatch._
import nielinjie.util.io.XStreamSerializer

class ServiceClient(val domain: Domain) {
  val lsOkS = new XStreamSerializer[LsOk]()
  def diff(peer: Peer): List[Transform] = {
    domain.updated(ls(peer))
  }
  def ls(peer: Peer): List[RemoteItem] = {
    val h = new Http
    h(url(peerUrlRoot(peer) + "/command/ls") >- {
      text =>
        lsOkS.unSerialize(text).items
    })
  }
  def peerUrlRoot(peer: Peer): String = {
    "http://%s".format(peer.asString)
  }
}