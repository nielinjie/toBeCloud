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
import nielinjie.util.io.Logger
import reactive.Observing

import scalaz._
import Scalaz._

class ServiceClient(val domain: Domain) extends Observing with Logger {
  System.getProperties.put("HTTPClient.disableKeepAlives ", "true")
  System.getProperties.put("HTTPClient.dontChunkRequests", "true")
  System.getProperties.put("HTTPClient.forceHTTP_1.0", "true")

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
    logger.info(sourceUrl.toString)
    h(url(sourceUrl.toString) >:+ { (headers, req) =>
      logger.debug(headers.toString)
      headers.get("content-length").getOrElse(Seq()).foreach {
        len: String =>
          historyItem.total = len.toInt.some
      }
      req >> {
        stream: InputStream =>
          val input = new CountingInput(stream.asInput).doto {
            ci =>
              ci.count.foreach {
                case (count) => historyItem.processed = count.some
              }
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
    "http://%s".format(peer.asAddress)
  }
}