package nielinjie.app.toBeCloud
package config
import java.io.File
import nielinjie.util.io.FileUtil
import comm._
import domain._
import nielinjie.util.io.LocalAddress

object Configs {
  def default = defaultDeveloping
  object defaultProduct extends Config {
    val flashSendPort = 3001
    val flashRevPort = 3002
    val flashInterval = 1000 * 3 * 60

    val connetPingPort: Int = 3003
    val connetPongPort = 3004

    val configFilePath = new File(FileUtil.home.toOption.getOrElse(new File(".")), ".toBeCloud")
    val webPort=3800
  }
  object defaultDeveloping extends Config {
    val flashSendPort: Int = 3001
    val flashRevPort: Int = 3002
    val flashInterval: Int = 1000

    val connetPingPort: Int = 3003
    val connetPongPort = 3004

    val configFilePath = new File("./configMock/.toBeCloud")

    override val mockPeers = List(Peer("mock2",LocalAddress.getFirstNonLoopbackAddress(true, false).getHostAddress, 3801))
    val webPort=3800

  }
  object defaultDeveloping2 extends Config {
    val flashSendPort: Int = 3005
    val flashRevPort: Int = 3006
    val flashInterval: Int = 1000

    val connetPingPort: Int = 3007
    val connetPongPort = 3008

    val configFilePath = new File("./configMock2/.toBeCloud")
    override val mockPeers = List(Peer("mock",LocalAddress.getFirstNonLoopbackAddress(true, false).getHostAddress, 3800))
    val webPort=3801
  }
}
trait Config {
  val flashInterval: Int
  val flashRevPort: Int
  val flashSendPort: Int

  val connetPingPort: Int
  val connetPongPort: Int

  val configFilePath: File
  val mockPeers: List[Peer]=List()
  val webPort:Int
}