package nielinjie.app.toBeCloud
package web

import config._
import domain._
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

class Web(config: Config, domain: Domain) {
  val server = new Server(config.webPort)
  val context = new ServletContextHandler(ServletContextHandler.SESSIONS)
  context.setContextPath("/")
  server.setHandler(context)
  domain.mounts.mounts.foreach {
    mount =>
      context.addServlet(newDefaultServletHolder(mount), "/" + mount.name + "/*")
  }
  def start = {
    server.start
    server.join
  }
  def newDefaultServletHolder(mount: Mount) = new ServletHolder(new DefaultServlet with Logger {
    override def getResource(pathInfo: String) = {
      logger.debug(pathInfo)
      new FileResource(new URL("file://"+mount.point.getAbsolutePath+"/"+new File(pathInfo).getName))
    }
  }).doto {
    holder =>
      holder.setInitParameters(Map(
        "acceptRanges" -> "true",
        "dirAllowed" -> "true",
        "pathInfoOnly" -> "true"))
  }
}

object WebStart extends App {
  val web = new Web(Configs.defaultDeveloping, new Domain(Configs.defaultDeveloping))
  web.start
}