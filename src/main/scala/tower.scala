package nielinjie.app.toBeCloud
package comm
import domain._
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
  watching.msges.foreach {
    messages =>
      peers_ = messages.distinct.map {
        message: FlashMessage =>
          Peer.fromString(message.message)
      }
  }
  def peers = if (config.mockPeers.isEmpty) peers_ else config.mockPeers
  def start = {
    watching.start
    flasher.keepFlashing(config.flashInterval)
  }

}

