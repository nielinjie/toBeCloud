package nielinjie.app.toBeCloud
package web

import domain._
import org.eclipse.jetty.servlet.ServletHolder
import org.eclipse.jetty.servlet.DefaultServlet
import nielinjie.util.io.Logger
import org.eclipse.jetty.util.resource.FileResource
import java.net.URL
import java.net.URLDecoder
import scala.collection.JavaConversions._
import nielinjie.util.data.Helper._
import unfiltered.jetty.ContextBuilder

trait FileService {

  self: { def domain: Domain } =>
  def newDefaultServletHolder(mount: Mount) = new ServletHolder(new DefaultServlet with Logger {
    override def getResource(pathInfo: String) = {
      logger.debug(pathInfo)
      new FileResource(new URL("file://" + mount.point.getAbsolutePath + URLDecoder.decode(pathInfo)))
    }
  }).doto {
    holder =>
      holder.setInitParameters(Map(
        "acceptRanges" -> "true",
        "dirAllowed" -> "true",
        "pathInfoOnly" -> "true"))
  }

  val fileContext = {
    context: ContextBuilder =>
      domain.mounts.mounts.foreach {
        mount =>
          context.current.addServlet(newDefaultServletHolder(mount), "/" + mount.name + "/*")
      }
  }
}