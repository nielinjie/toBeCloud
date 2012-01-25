package nielinjie.app.toBeCloud
package app
import config._
import comm._
import nielinjie.util.io.Logger

class Cloud(config: Config) extends Logger {
  val tower = new Tower(config)
  val connet = new Connect(config)
  def startTower = tower.start
  def startConnet = connet.startPong
  def standup = {
    startTower
    startConnet
  }

  def ask = {
    tower.peers.foreach {
      peer =>
        logger.info("start ask peer - %s".format(peer))
        connet.startPing(peer)
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