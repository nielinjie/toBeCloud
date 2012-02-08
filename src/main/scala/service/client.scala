package nielinjie.app.toBeCloud
package web

import comm._
import domain._
import java.net.URL
import dispatch._
import nielinjie.util.io.XStreamSerializer
import java.io.InputStream
import scalax.io._
import JavaConverters.asInputConverter
import JavaConverters.asOutputConverter
import nielinjie.util.io.CountingInput
import nielinjie.util.data.Helper._
import reactive.Observing

import scalaz._
import Scalaz._

class ServiceClient(val domain: Domain) extends Observing {
  val lsOkS = new XStreamSerializer[LsOk]()
  val whereOkS = new XStreamSerializer[WhereOk]()
  val itemS = new XStreamSerializer[RemoteItem]()
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
  def download(peer: Peer, transform: Transform, historyItem: DownloadHistory) = {
    val sourceUrl = where(peer, transform)
    val h = new thread.Http

    h(url(sourceUrl.toString) >:+ { (headers, req) =>
      println(headers)
      headers.get("content-length").getOrElse(Seq()).foreach {
        len: String =>
          println(len)
          historyItem.total = len.toInt
      }
      req >> {
        stream: InputStream =>
          val input = new CountingInput(stream.asInput).doto {
            ci =>
              ci.count.foreach(historyItem.procssed = _)
          }.asInput

          input.copyDataTo(transform.dis.file.asOutput)
      }
    })
  }
  def where(peer: Peer, transform: Transform): URL = {
    val h = new Http
    h(url(peerUrlRoot(peer) + "/command/where") <<? Map("items" -> itemS.serialize(transform.source)) >- {
      text =>
        whereOkS.unSerialize(text).urls.head
    })
  }
  def peerUrlRoot(peer: Peer): String = {
    "http://%s".format(peer.asString)
  }
}