package nielinjie.app.toBeCloud
package comm

import scala.actors.Actor
import scala.actors.Actor._
import scala.actors.remote.RemoteActor._
import scala.actors.Exit
import scala.actors.remote.Node
import nielinjie.util.io.Logger
import config._
import domain._
import java.net.InetAddress
import nielinjie.util.io.LocalAddress

class Connect(val config: Config) extends Logger {
  def startPing(toPeer: Peer): Unit = {
    val port = config.connetPingPort
    val peer = Node(toPeer.address.getHostAddress(), config.connetPongPort)
    val ping = new RemotePing(port, peer, 16)
    ping.start()
    logger.debug("ping start")
  }
  def startPong(): Unit = {
    val port = config.connetPongPort
    val pong = new RemotePong(port)
    pong.start()
    logger.debug("pong start")
  }
  class RemotePing(port: Int, peer: Node, count: Int) extends Actor {
    trapExit = true
    def act() {
      alive(port)
      register('Ping, self)
      val pong = select(peer, 'Pong)
      link(pong)
      logger.debug("linked")
      pong ! Ping
      while (true) {
        receive {
          case Pong => {
            logger.debug("Ping recieved: pong")
            reply(Ls)
          }
          case LsOk(lsResult) => {
            logger.debug("Ping revieved: LsOk")
            logger.debug(lsResult.toString)
          }
          case Exit(pong, 'normal) => {
            logger.debug("Ping recieved: exit")
            exit()
          }
        }
      }
    }
  }
  class RemotePong(port: Int) extends Actor {
    def act() {
      alive(port)
      register('Pong, self)
      while (true) {
        receive {
          case Ping => {
            logger.debug("Pong recieved: ping")
            sender ! Pong
          }
          case Ls => {
            logger.debug("Pong recived: ls")
            logger.debug(Domain.ls().toString)
            logger.debug("what?")
            reply(LsOk(Domain.ls().map(_.remoteView)))
          }
          case Quit => {
            logger.debug("Pong recieved: stop")
            exit()
          }
        }
      }
    }
  }

}
case object Ping
case object Pong
case object Quit


object PingPong extends App {
  val connet = new Connect(Configs.defaultDeveloping)
  connet.startPong()
  Thread.sleep(2000)
  connet.startPing(Peer(LocalAddress.getFirstNonLoopbackAddress(true,false)))
}