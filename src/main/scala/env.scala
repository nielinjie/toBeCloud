package nielinjie.app.toBeCloud
package comm

import nielinjie.util.io.LocalAddress

object Env{
  def getRootIp=LocalAddress.getFirstNonLoopbackAddress(true, false).getHostAddress
}