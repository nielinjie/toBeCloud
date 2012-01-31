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

class Web(val config: Config) extends CommandPlan{
  val domain=new Domain(config)
  val server = Http(config.webPort)
    .context("/files") {
      context =>
        domain.mounts.mounts.foreach {
          mount =>
            context.current.addServlet(newDefaultServletHolder(mount), "/" + mount.name + "/*")
        }
    }.filter(commandPlan)//.filter(pp)

  def start = {
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
  
  def urlForRemoteItem(remoteItem:RemoteItem)={
    new URL("http://%s:%s/files/%s/%s".format(Env.getRootIp,config.webPort,remoteItem.mountName,remoteItem.relativePath))
  }
}

object WebStart extends App {
  val web = new Web(Configs.defaultDeveloping2)//, new Domain(Configs.defaultDeveloping2))
  web.start
}