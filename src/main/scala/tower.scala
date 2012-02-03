package nielinjie.app.toBeCloud
package comm
import nielinjie.util.io.Flasher
import java.net.InetAddress
import nielinjie.util.io.FlashMessage
import config.Config
import nielinjie.util.io.LocalAddress
import reactive.Observing

class Tower(val config: Config) extends Observing {
  val flasher = new Flasher(config.flashSendPort, config.flashRevPort, LocalAddress.getFirstNonLoopbackAddress(true, false).getHostAddress + ":" + config.connetPongPort)
  val watching = flasher.keepWatching
  var peers_ = List[Peer]()
  val addressPattern = "(.*):(.*)".r
  watching.msges.foreach {
    messages =>
      peers_ = messages.distinct.map {
        message: FlashMessage =>
          message.message match {
            case addressPattern(ip, port) => Peer(ip, port.toInt)
          }
      }
  }
  def peers = if (config.mockPeers.isEmpty) peers_ else config.mockPeers
  def start = {
    watching.start
    flasher.keepFlashing(config.flashInterval)
  }

}

case class Peer(ip: String, port: Int) {
  def asString = "%s:%s".format(ip, port)
}
object Peer {
  val addressPattern = "(.*):(.*)".r
  def fromString(ipAndPort: String) = {
    val addressPattern(ip, port) = ipAndPort
    Peer(ip, port.toInt)
  }

}