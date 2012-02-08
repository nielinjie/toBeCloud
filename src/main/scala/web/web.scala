package nielinjie.app.toBeCloud
package web

import config._
import domain._
import comm._
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.eclipse.jetty.servlet.DefaultServlet
import nielinjie.util.data.Helper._
import scala.collection.JavaConversions._
import nielinjie.util.io.Logger
import org.eclipse.jetty.util.resource.FileResource
import java.net.URL
import java.io.File
import unfiltered.jetty.Http
import unfiltered.filter.Planify
import unfiltered.filter.request._
import unfiltered.request._
import unfiltered.response._
import unfiltered.filter.Plan
import scalaz._
import Scalaz._
import java.util.Date

class Web(val config: Config) extends CommandPlan with UIPlan {
  //TODO so many depent objects
  val domain = new Domain(config)
  val tower = new Tower(config)
  val client = new ServiceClient(domain)
  val history = new History(domain)
  val status = new Status(config, tower, client, history)
  val model = new Model(domain, status, tower)
  val server = Http(config.webPort)
    .context("/web") {
      context =>
        context.current.setBaseResource(new FileResource(new File("src/main/www").toURI.toURL))
    }
    .filter(uiPlan)
    .context("/files") {
      context =>
        domain.mounts.mounts.foreach {
          mount =>
            context.current.addServlet(newDefaultServletHolder(mount), "/" + mount.name + "/*")
        }
    }
    .filter(commandPlan)

  def start = {
    tower.start
    server.start
    server.join
  }
  def newDefaultServletHolder(mount: Mount) = new ServletHolder(new DefaultServlet with Logger {
    override def getResource(pathInfo: String) = {
      logger.debug(pathInfo)
      new FileResource(new URL("file://" + mount.point.getAbsolutePath + pathInfo))
    }
  }).doto {
    holder =>
      holder.setInitParameters(Map(
        "acceptRanges" -> "true",
        "dirAllowed" -> "true",
        "pathInfoOnly" -> "true"))
  }

}
class Model(domain: Domain, status: Status, tower: Tower) {
  def peers = tower.peers
}
class Status(config: Config, tower: Tower, client: ServiceClient, history: History) extends Logger {
  def diff(peer: Peer): List[Transform] = {
    tower.peers.find(_ == peer).map {
      peer =>
        logger.warn(peer.asString)
        client.diff(peer)
    }.getOrElse(List())
  }
  def download(peer: Peer, transform: Transform) = {
    //    val hi=H
    val hi = new DownloadHistory(new Date(), transform)
    history.append(hi)
    client.download(peer, transform, hi)
  }
  def history(length: Int): List[HistoryItem] = {
    history.tail(length)
  }
}

object WebStart extends App {
  val web = new Web(Configs.defaultDeveloping2) //, new Domain(Configs.defaultDeveloping2))
  web.start
}

object FakePeer extends App {
  val web = new Web(Configs.defaultDeveloping)
  web.start
}