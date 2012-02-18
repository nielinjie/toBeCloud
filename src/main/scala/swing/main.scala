package nielinjie.app.toBeCloud
package ui

import config._
import domain._
import comm._
import web._
import scala.swing.SimpleSwingApplication
import reactive.Observing
import scala.swing.MainFrame
import nielinjie.util.ui.MigPanel
import nielinjie.util.ui.event.EventSupport._
import nielinjie.util.ui.Mig._
import scala.swing.ListView
import scala.swing.Label
import scala.swing.event.WindowClosed
import nielinjie.util.ui.Bind
import scalaz._
import Scalaz._
import nielinjie.util.ui.Mig
import scala.swing.Button
import nielinjie.util.data.Helper._
import unfiltered.jetty.Http
import org.eclipse.jetty.servlet.ServletHolder
import nielinjie.util.io.XStreamSerializer
import unfiltered.filter.Planify
import org.eclipse.jetty.servlet.DefaultServlet
import unfiltered.request._
import unfiltered.response._
import org.eclipse.jetty.util.resource.FileResource
import java.net.URL
import java.net.URLDecoder

class Main(val config: Config) extends SimpleSwingApplication
  with Model
  with Service
  with DomainPanel
  with PeerList
  with DiffList
  with HistoryList {
  def top = new MainFrame {
    title = "First Swing App"
    contents = new MigPanel(fill + debug, fill(400)|, fill(50) | fill(200) | fill(200) | fill(200)) {
      add(domainPanel, wrap)
      add(peerPanel, wrap)
      add(diffPanel, wrap)
      add(historyPanel, "")
    }
  }

  reactions += {

    case WindowClosed(top) => {
      println("closing")
      System.exit(0)
    }
  }

  domainBind.push(domain.success)
  server.start
}
trait Model extends Observing {
  self: Main =>
  lazy val domain = new Domain(config)
  lazy val tower = new Tower(config)
  lazy val client = new ServiceClient(domain)

}
trait DomainPanel extends Observing {
  self: Main =>
  val peerNameLabel = new Label("Name")
  val domainPanel = new MigPanel(fill + debug, fill(300) | prefer(100), prefer(50)|) {
    add(peerNameLabel, "")
    add(new Button("Peers").doto {
      _.clicked.foreach {
        com =>
          peerBind.push(tower.peers.success)
          diffBind.push(tower.peers.flatMap {
            peer =>
              client.diff(peer)
          }.success)
      }
    }, "")
  }

  val domainBind: Bind[Domain] = Bind.readOnly({
    vd: Validation[String, Domain] =>
      peerNameLabel.text = vd.toOption.map {
        _.define.global.map {
          _.name
        }
      }.join.getOrElse("None")
  })
}

trait PeerList extends Observing {
  self: Main =>
  val peerList = new ListView[Peer]()
  val peerPanel = new MigPanel(fill, fill(400)|, fill(200)|) {
    add(peerList, Mig.none)
  }
  val peerBind: Bind[List[Peer]] = Bind.readOnly {
    vlp: Validation[String, List[Peer]] =>
      vlp.toOption.foreach {
        peerList.listData = _
      }
  }
}

trait DiffList extends Observing {
  self: Main =>
  val diffList = new ListView[Transform]()
  val diffPanel = new MigPanel(fill, fill(400)|, fill(200)|) {
    add(diffList, "")
  }
  val diffBind: Bind[List[Transform]] = Bind.readOnly {
    vlp: Validation[String, List[Transform]] =>
      vlp.toOption.foreach {
        diffList.listData = _
      }
  }
}

trait HistoryList extends Observing {
  self: Main =>
  val historyList = new ListView[DownloadHistory]
  val historyPanel = new MigPanel(fill + debug, fill(300) | prefer(100), prefer(200)) {
    add(historyList, grow)
    add(new Button("refresh").doto {
      _.clicked.foreach {
        com =>
          historyBind.push(domain.history.tail(10).success)
      }
    }, "")
  }
  val historyBind: Bind[List[DownloadHistory]] = Bind.readOnly {
    vlh: Validation[String, List[DownloadHistory]] =>
      vlh.toOption.foreach {
        historyList.listData = _
      }
  }
}



object SwingMain extends App {
  new Main(Configs.defaultDeveloping).main(Array.empty[String])
}
object SwingFack extends App {
  new Main(Configs.defaultDeveloping2).main(Array.empty[String])
}
object SwingBoth extends App {
  SwingMain.main(Array.empty[String])
  SwingFack.main(Array.empty[String])
}
