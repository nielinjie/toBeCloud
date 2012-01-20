package nielinjie.app.toBeCloud
package config
import java.io.File
import nielinjie.util.io.FileUtil

object Configs {
  def default = defaultDeveloping
  object defaultProduct extends Config {
    val flashSendPort = 3001
    val flashRevPort = 3002
    val flashInterval = 1000 * 3 * 60

    val connetPingPort: Int = 3003
    val connetPongPort = 3004
    
    val configFilePath = new File(FileUtil.home.toOption.getOrElse(new File(".")), ".toBeCloud")
  }
  object defaultDeveloping extends Config {
    val flashSendPort: Int = 3001
    val flashRevPort: Int = 3002
    val flashInterval: Int = 1000

    val connetPingPort: Int = 3003
    val connetPongPort = 3004
    
    val configFilePath = new File("./configMock/.toBeCloud")
  }
}
trait Config {
  val flashInterval: Int
  val flashRevPort: Int
  val flashSendPort: Int

  val connetPingPort: Int
  val connetPongPort: Int
  
  val configFilePath: File
}