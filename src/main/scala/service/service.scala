package nielinjie.app.toBeCloud
package web

import domain._
import config._
import unfiltered.jetty.Http

trait Service extends CommandPlan with FileService {
  self: {
    def domain:Domain
    def config:Config
    } =>
  val server = Http(config.webPort)
    .context("/files")(fileContext)
    .filter(commandPlan)
}