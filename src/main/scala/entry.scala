package nielinjie.app.toBeCloud
package app

import comm.Tower
import nielinjie.util.io.Log
import config.Configs

import scalaz._
import Scalaz._

object Main extends App with Log {
  val tower = new Tower(Configs.default)
  debug(tower.peers.toString)
  debug(tower.messages.toString)
  Thread.sleep(2000)
  debug(tower.peers.toString)
  debug(tower.messages.toString)
  Thread.sleep(3000)
  debug(tower.peers.toString)
  debug(tower.messages.toString)
}