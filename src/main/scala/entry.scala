package nielinjie.app.toBeCloud
package app

import comm.Tower
import nielinjie.util.io.Logger
import config.Configs
import scalaz._
import Scalaz._
import reactive.Observing

object Main extends App with Logger with Observing {
  val tower = new Tower(Configs.default)
  val tower2 = new Tower(Configs.defaultDeveloping2)
  tower.start
  tower2.start
  //  tower.watching.msges.change.foreach{
  //    case p=>logger.debug("tower " +p.toString)
  //  }
  //  tower2.watching.msges.change.foreach{
  //    case p=>logger.debug("tower2 " + p.toString)
  //  }
  //  tower.peers.change.foreach{
  //    case p=>logger.debug("tower " +p.toString)
  //  }
  //  tower2.peers.change.foreach{
  //    case p=>logger.debug("tower2 " + p.toString)
  //  }
  logger.debug("tower peers - %s ".format(tower.peers))
//  logger.debug("tower messages - %s".format(tower.messages))
  logger.debug("tower2 peers - %s ".format(tower2.peers))
//  logger.debug("tower2 messages - %s".format(tower2.messages))
  Thread.sleep(2000)
  logger.debug("tower peers - %s ".format(tower.peers))
//  logger.debug("tower messages - %s".format(tower.messages))
  logger.debug("tower2 peers - %s ".format(tower2.peers))
//  logger.debug("tower2 messages - %s".format(tower2.messages))
  Thread.sleep(3000)
  logger.debug("tower peers - %s ".format(tower.peers))
//  logger.debug("tower messages - %s".format(tower.messages))
  logger.debug("tower2 peers - %s ".format(tower2.peers))
//  logger.debug("tower2 messages - %s".format(tower2.messages))
}