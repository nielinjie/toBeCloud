package nielinjie.app.toBeCloud
package app
import config._
import comm._
import nielinjie.util.io.Logger

class Cloud(config: Config) extends Logger {
  val tower = new Tower(config)
  def startTower = tower.start
  def standup = {
    startTower
  }

  def ask = {
    tower.peers.foreach {
      peer =>
        logger.info("start ask peer - %s".format(peer))
    }
  }
}

object Standup extends App {
  val c1 = new Cloud(Configs.defaultDeveloping)
  val c2 = new Cloud(Configs.defaultDeveloping2)
  c1.standup
  c2.standup
  Thread.sleep(5000)
  c1.ask

}