package nielinjie.app.toBeCloud
package comm
import nielinjie.util.io.Flasher
import java.net.InetAddress
import nielinjie.util.io.FlashMessage
import config.Config

class Tower(val config: Config) {
  val flasher = new Flasher(config.flashSendPort, config.flashRevPort)
  val watching = flasher.keepWatching
  watching.start
  flasher.keepFlashing(config.flashInterval)
  def peers = watching.messages.distinct.map {
    message: FlashMessage =>
      Peer(message.origin)
  }
  def messages = watching.messages
}

case class Peer(address: InetAddress)